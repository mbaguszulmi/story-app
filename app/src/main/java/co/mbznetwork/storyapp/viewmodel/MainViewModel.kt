package co.mbznetwork.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.di.IODispatcher
import co.mbznetwork.storyapp.eventbus.AuthEventBus
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import co.mbznetwork.storyapp.model.ui.StoryDisplay
import co.mbznetwork.storyapp.model.ui.UiMessage
import co.mbznetwork.storyapp.model.ui.UiStatus
import co.mbznetwork.storyapp.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    storyRepository: StoryRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    private val authEventBus: AuthEventBus,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    val stories = storyRepository.getStories().map {
        it.map { story ->
            StoryDisplay.fromStory(story)
        }.also {
            uiStatusEventBus.setUiStatus(UiStatus.Idle)
        }
    }.catch {
        uiStatusEventBus.setUiStatus(UiStatus.ShowError(it.message?.let { message ->
            UiMessage.StringMessage(message)
        } ?: UiMessage.ResourceMessage(R.string.error_occurred)))
    }.cachedIn(
        viewModelScope
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PagingData.empty())

    init {
        viewModelScope.launch(ioDispatcher) {
            uiStatusEventBus.setUiStatus(UiStatus.Loading)
        }
    }

    fun logout() {
        viewModelScope.launch(ioDispatcher) {
            authEventBus.needSignOut()
        }
    }
}