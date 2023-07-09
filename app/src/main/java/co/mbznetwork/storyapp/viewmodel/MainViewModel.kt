package co.mbznetwork.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.storyapp.di.IODispatcher
import co.mbznetwork.storyapp.eventbus.AuthEventBus
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import co.mbznetwork.storyapp.model.auth.AuthenticationState
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.model.ui.StoryDisplay
import co.mbznetwork.storyapp.model.ui.UiMessage
import co.mbznetwork.storyapp.model.ui.UiStatus
import co.mbznetwork.storyapp.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    private val authEventBus: AuthEventBus,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _stories = MutableStateFlow<List<StoryDisplay>>(emptyList())
    val stories = _stories.asStateFlow()

    fun getStories() {
        viewModelScope.launch(ioDispatcher) {
            if (authEventBus.state.value is AuthenticationState.SignedIn) {
                uiStatusEventBus.setUiStatus(UiStatus.Loading)

                _stories.value = storyRepository.getStories().let {
                    when(it) {
                        is NetworkResult.Success -> {
                            uiStatusEventBus.setUiStatus(UiStatus.Idle)
                            it.data.listStory.map { story ->
                                StoryDisplay.fromStory(story)
                            }
                        }
                        is NetworkResult.Error -> {
                            uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                                UiMessage.StringMessage(it.message)
                            ))
                            emptyList()
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(ioDispatcher) {
            authEventBus.needSignOut()
        }
    }
}