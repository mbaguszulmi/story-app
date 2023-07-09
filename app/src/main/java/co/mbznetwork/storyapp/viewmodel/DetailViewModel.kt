package co.mbznetwork.storyapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.storyapp.di.IODispatcher
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.model.ui.StoryDetailDisplay
import co.mbznetwork.storyapp.model.ui.UiMessage
import co.mbznetwork.storyapp.model.ui.UiStatus
import co.mbznetwork.storyapp.repository.StoryRepository
import co.mbznetwork.storyapp.util.DateHelper
import co.mbznetwork.storyapp.view.ARG_STORY_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    private val savedStateHandle: SavedStateHandle,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {
    private val _story = MutableStateFlow(StoryDetailDisplay())
    val story = _story.asStateFlow()

    init {
        getStory()
    }

    private fun getStory() {
        viewModelScope.launch(ioDispatcher) {
            savedStateHandle.get<String>(ARG_STORY_ID)?.let {
                uiStatusEventBus.setUiStatus(UiStatus.Loading)
                uiStatusEventBus.setUiStatus(
                    storyRepository.getStory(it).let { result ->
                        when(result) {
                            is NetworkResult.Success -> {
                                _story.value = with(result.data.story) {
                                    StoryDetailDisplay(
                                        id,
                                        name,
                                        description,
                                        photoUrl,
                                        DateHelper.formatDateTime(createdAt)
                                    )
                                }
                                UiStatus.Idle
                            }
                            is NetworkResult.Error -> UiStatus.ShowError(
                                UiMessage.StringMessage(result.message)
                            )
                        }
                    }
                )
            }
        }
    }

}