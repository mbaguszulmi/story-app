package co.mbznetwork.storyapp.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.ActivityStoryMapsBinding
import co.mbznetwork.storyapp.util.activityLifecycle
import co.mbznetwork.storyapp.viewmodel.StoryMapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StoryMapsActivity : BaseActivity<ActivityStoryMapsBinding>(), OnMapReadyCallback {

    private val storyMapViewModel by viewModels<StoryMapViewModel>()

    private lateinit var mMap: GoogleMap

    override val layoutId: Int
        get() = R.layout.activity_story_maps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.apply {
            title = getString(R.string.story_map)
            setDisplayHomeAsUpEnabled(true)
        }

        observeStoryLocations()
        observeLatLngBound()
    }

    private fun observeStoryLocations() {
        activityLifecycle {
            storyMapViewModel.storyLocations.collectLatest {
                if (it.isEmpty()) return@collectLatest
                mMap.apply {
                    clear()

                    it.forEach { story ->
                        story.apply {
                            addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(name)
                                    .snippet(shortDesc)
                            )?.apply {
                                tag = story.id
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeLatLngBound() {
        activityLifecycle {
            storyMapViewModel.mapLatLngBound.collectLatest {
                it?.let {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        it,
                        80
                    ))
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap.apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@StoryMapsActivity, R.raw.map_style
                )
            )
            setOnInfoWindowClickListener {
                startActivity(Intent(
                    this@StoryMapsActivity, DetailActivity::class.java
                ).putExtra(ARG_STORY_ID, it.tag as String))
            }
        }
        storyMapViewModel.setMapReady()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
