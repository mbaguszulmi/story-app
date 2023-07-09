package co.mbznetwork.storyapp.eventbus

import co.mbznetwork.storyapp.model.auth.AuthenticationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventBus @Inject constructor() {
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState.Unauthenticated())
    val state = _state.asStateFlow()

    private val _isLoginPage = MutableSharedFlow<Boolean>()
    val isLoginPage = _isLoginPage.asSharedFlow()

    fun getToken() = _state.value.let {
        when(it) {
            is AuthenticationState.SignedIn -> it.token
            else -> null
        }
    }

    fun setUnAuthorized() {
        _state.value = AuthenticationState.Unauthenticated()
    }

    fun setState(state: AuthenticationState) {
        _state.value = state
    }

    fun needSignOut() {
        _state.value = AuthenticationState.NeedSignOut
    }

    suspend fun setLoginPage(isLoginPage: Boolean) {
        _isLoginPage.emit(isLoginPage)
    }
}