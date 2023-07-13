package co.mbznetwork.storyapp.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.storyapp.di.IODispatcher
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.model.ui.StoryLocationDisplay
import co.mbznetwork.storyapp.model.ui.UiMessage
import co.mbznetwork.storyapp.model.ui.UiStatus
import co.mbznetwork.storyapp.repository.StoryRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StoryMapViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _storyLocations = MutableStateFlow(emptyList<StoryLocationDisplay>())

    private val isMapReady = MutableStateFlow(false)

    val storyLocations = isMapReady.flatMapLatest {
        if (it) _storyLocations.asStateFlow()
        else emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val mapLatLngBound = storyLocations.map {
        if (it.isEmpty()) null
        else {
            var tempBoundBuilder = LatLngBounds.builder()
            var tempSouthwest: LatLng? = null
            var tempNortheast: LatLng? = null
            LatLngBounds.builder().apply {
                it.forEach { story ->
                    story.apply {
                        tempBoundBuilder.include(latLng)
                        val tempBound = tempBoundBuilder.build()
                        if (isBoundsVisible(tempBound)) {
                            tempSouthwest = tempBound.southwest
                            tempNortheast = tempBound.northeast
                            include(latLng)
                        } else {
                            tempBoundBuilder = LatLngBounds.Builder().apply {
                                tempSouthwest?.let { sw ->
                                    tempNortheast?.let { ne ->
                                        include(sw)
                                        include(ne)
                                    }
                                }
                            }
                        }
                    }
                }
            }.build()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
        getStories()
    }

    private fun getStories() {
        viewModelScope.launch(ioDispatcher) {
            uiStatusEventBus.setUiStatus(UiStatus.Loading)
            uiStatusEventBus.setUiStatus(
                storyRepository.getStoriesWithLocation().let {
                    when(it) {
                        is NetworkResult.Success -> {
                            _storyLocations.value = it.data.listStory.map { story ->
                                StoryLocationDisplay.fromStoryResponse(story)
                            }
                            UiStatus.Idle
                        }
                        is NetworkResult.Error -> {
                            UiStatus.ShowError(UiMessage.StringMessage(
                                it.message
                            ))
                        }
                    }
                }
            )
        }
    }

    fun setMapReady() {
        viewModelScope.launch(ioDispatcher) {
            isMapReady.emit(true)
        }
    }

    private fun isBoundsVisible(bounds: LatLngBounds): Boolean {
        return with(bounds) {
            val result = FloatArray(1)
            Location.distanceBetween(
                .0,
                southwest.longitude,
                .0,
                northeast.longitude, result
            )
            result[0] < 747300
        }
    }

}