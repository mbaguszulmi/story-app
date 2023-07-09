package co.mbznetwork.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.storyapp.eventbus.AuthEventBus
import co.mbznetwork.storyapp.model.auth.AuthenticationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    authEventBus: AuthEventBus
): ViewModel() {
    val isLoginPage = authEventBus.isLoginPage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    val isAuthenticated = authEventBus.state.filterIsInstance<AuthenticationState.SignedIn>()
        .map { true }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
}