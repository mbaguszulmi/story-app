package co.mbznetwork.storyapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.ActivityAddStoryBinding
import co.mbznetwork.storyapp.util.IntentChooserHelper
import co.mbznetwork.storyapp.util.activityLifecycle
import co.mbznetwork.storyapp.viewmodel.AddStoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private const val BITMAP_DATA = "data"

@AndroidEntryPoint
class AddStoryActivity : BaseActivity<ActivityAddStoryBinding>() {

    private val addStoryViewModel by viewModels<AddStoryViewModel>()

    private val imageChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> result.data?.apply {
                data?.let {
                    contentResolver.openInputStream(it)?.let { input ->
                        addStoryViewModel.setPhoto(input, cacheDir)
                    }
                } ?: kotlin.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        getParcelableExtra(BITMAP_DATA, Bitmap::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        getParcelableExtra(BITMAP_DATA)
                    }?.let {
                        addStoryViewModel.setPhoto(it, cacheDir)
                    }
                }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> result.data?.apply {
                data?.let {
                    contentResolver.openInputStream(it)?.let { input ->
                        addStoryViewModel.setPhoto(input, cacheDir)
                    }
                }
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            launchImagePicker()
        } else {
            launchGalleryPicker()
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_add_story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observeShouldFinish()
    }

    private fun initView() {
        supportActionBar?.apply {
            title = getString(R.string.add_story)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.apply {
            lifecycleOwner = this@AddStoryActivity
            vm = addStoryViewModel

            btnAddPhoto.setOnClickListener {
                checkRequiredPermission()
            }
            buttonAdd.setOnClickListener {
                addStoryViewModel.addStory(
                    edAddDescription.text?.toString() ?: "",
                    contentResolver
                )
            }
        }
    }

    private fun checkRequiredPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            launchImagePicker()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun observeShouldFinish() {
        activityLifecycle {
            addStoryViewModel.shouldFinish.collectLatest {
                if (it) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchImagePicker() {
        imageChooserLauncher.launch(
            IntentChooserHelper.imageSelectCameraCapture(getString(R.string.text_select_picture))
        )
    }

    private fun launchGalleryPicker() {
        galleryLauncher.launch(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
            }
        )
    }
}
