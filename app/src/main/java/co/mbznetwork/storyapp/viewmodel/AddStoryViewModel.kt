package co.mbznetwork.storyapp.viewmodel

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.di.IODispatcher
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.model.ui.UiMessage
import co.mbznetwork.storyapp.model.ui.UiStatus
import co.mbznetwork.storyapp.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private var photo: File? = null

    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
    val photoBitmap = _photoBitmap.asStateFlow()

    private val _shouldFinish = MutableSharedFlow<Boolean>()
    val shouldFinish = _shouldFinish.asSharedFlow()

    fun setPhoto(bitmap: Bitmap, cacheDir: File) {
        viewModelScope.launch(ioDispatcher) {
            val f = File(cacheDir, "photo_${UUID.randomUUID()}.jpeg")
            f.createNewFile()
            ByteArrayOutputStream().let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
                it.toByteArray()
            }.let {
                FileOutputStream(f).apply {
                    write(it)
                    flush()
                    close()
                }
            }

            photo = f
            _photoBitmap.value = bitmap
        }
    }

    fun setPhoto(bitmapInputStream: InputStream, cacheDir: File) {
        viewModelScope.launch(ioDispatcher) {
            setPhoto(BitmapFactory.decodeStream(bitmapInputStream), cacheDir)
        }
    }

    fun addStory(description: String, contentResolver: ContentResolver) {
        viewModelScope.launch(ioDispatcher) {
            getInputError(description)?.let {
                uiStatusEventBus.setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(it)))
            } ?: kotlin.run {
                photo?.let {
                    uiStatusEventBus.setUiStatus(UiStatus.Loading)

                    uiStatusEventBus.setUiStatus(
                        storyRepository.addStory(
                            it,
                            MimeTypeMap.getSingleton().getExtensionFromMimeType(
                                contentResolver.getType(Uri.fromFile(it))
                            ) ?: "image/jpeg",
                            description
                        ).let { result ->
                            when(result) {
                                is NetworkResult.Error -> UiStatus.ShowError(UiMessage.StringMessage(
                                    result.message
                                ))
                                else -> {
                                    _shouldFinish.emit(true)
                                    UiStatus.Idle
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun getInputError(description: String): Int? {
        return if (description.isBlank()) R.string.error_missing_description
        else if (photo == null) R.string.error_missing_photo
        else null
    }

}