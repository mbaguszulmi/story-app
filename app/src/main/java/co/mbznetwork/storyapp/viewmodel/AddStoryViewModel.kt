package co.mbznetwork.storyapp.viewmodel

import android.annotation.SuppressLint
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
import co.mbznetwork.storyapp.repository.LocationRepository
import co.mbznetwork.storyapp.repository.StoryRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val locationRepository: LocationRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private var photo: File? = null

    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
    val photoBitmap = _photoBitmap.asStateFlow()

    private val _shouldFinish = MutableSharedFlow<Boolean>()
    val shouldFinish = _shouldFinish.asSharedFlow()

    private val _shouldCheckLocationPermission = MutableSharedFlow<Boolean>()
    val shouldCheckLocationPermission = _shouldCheckLocationPermission.asSharedFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    private val isMapReady = MutableStateFlow(false)

    val userLocation = isMapReady.flatMapLatest {
        if (it) _userLocation.asStateFlow()
        else emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private var userLocationJob: Job? = null

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
                            description,
                            userLocation.value
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

    @SuppressLint("MissingPermission")
    fun getCurrentUserLocation() {
        userLocationJob = viewModelScope.launch(ioDispatcher) {
            locationRepository.getLocation().cancellable().catch {
                uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                    it.message?.let { message ->
                        UiMessage.StringMessage(message)
                    } ?: UiMessage.ResourceMessage(R.string.error_occurred)
                ))
            }.flowOn(ioDispatcher).collect {
                _userLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    fun toggleUserLocation() {
        viewModelScope.launch(ioDispatcher) {
            if (userLocation.value != null) {
                userLocationJob?.cancel()
                userLocationJob = null
                _userLocation.value = null
            } else {
                _shouldCheckLocationPermission.emit(true)
            }
        }
    }

    fun setMapReady() {
        viewModelScope.launch(ioDispatcher) {
            isMapReady.value = true
        }
    }

}