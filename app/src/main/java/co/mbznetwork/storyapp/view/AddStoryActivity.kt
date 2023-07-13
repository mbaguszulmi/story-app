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
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.ActivityAddStoryBinding
import co.mbznetwork.storyapp.util.IntentChooserHelper
import co.mbznetwork.storyapp.util.activityLifecycle
import co.mbznetwork.storyapp.viewmodel.AddStoryViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit

private const val BITMAP_DATA = "data"

@AndroidEntryPoint
class AddStoryActivity : BaseActivity<ActivityAddStoryBinding>(), OnMapReadyCallback {

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

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            requestLocationService()
        }
    }

    private val enableLocationLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) addStoryViewModel.getCurrentUserLocation()
    }

    private lateinit var googleMap: GoogleMap

    override val layoutId: Int
        get() = R.layout.activity_add_story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observeShouldFinish()
        observeShouldCheckLocationPermission()
        observeUserLocation()
    }

    private fun initView() {
        supportActionBar?.apply {
            title = getString(R.string.add_story)
            setDisplayHomeAsUpEnabled(true)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    private fun observeShouldCheckLocationPermission() {
        activityLifecycle {
            addStoryViewModel.shouldCheckLocationPermission.collectLatest {
                if (it) checkLocationPermission()
            }
        }
    }

    private fun observeUserLocation() {
        activityLifecycle {
            addStoryViewModel.userLocation.collect {
                it?.let {
                    googleMap.apply {
                        clear()
                        addMarker(
                            MarkerOptions().position(it)
                        )
                        animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                it, 15f
                            )
                        )
                    }
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

    private fun checkLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestLocationService() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(1)
        )

        val settings = LocationSettingsRequest.Builder().addLocationRequest(locationRequest.build())
        LocationServices.getSettingsClient(this)
            .checkLocationSettings(settings.build())
            .addOnSuccessListener {
                addStoryViewModel.getCurrentUserLocation()
            }.addOnFailureListener {
                if (it is ResolvableApiException) {
                    enableLocationLauncher.launch(
                        IntentSenderRequest.Builder(it.resolution).build()
                    )
                }
            }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map.apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@AddStoryActivity, R.raw.map_style
                )
            )
        }
        addStoryViewModel.setMapReady()
    }
}
