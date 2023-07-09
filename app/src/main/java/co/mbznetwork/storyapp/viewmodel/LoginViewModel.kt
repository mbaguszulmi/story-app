package co.mbznetwork.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.datasource.api.model.request.LoginRequest
import co.mbznetwork.storyapp.di.IODispatcher
import co.mbznetwork.storyapp.eventbus.AuthEventBus
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.model.ui.UiMessage
import co.mbznetwork.storyapp.model.ui.UiStatus
import co.mbznetwork.storyapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    private val authEventBus: AuthEventBus,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val emailHasError = MutableStateFlow(true)
    private val passwordHasError = MutableStateFlow(true)
    val isLoginEnabled = emailHasError.combine(passwordHasError) { emailError, passwordError ->
        !emailError && !passwordError
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun login(email: String, password: String) {
        viewModelScope.launch(ioDispatcher) {
            uiStatusEventBus.setUiStatus(UiStatus.Loading)

            authRepository.login(LoginRequest(
                email, password
            )).let {
                uiStatusEventBus.setUiStatus(
                    when(it) {
                        is NetworkResult.Success -> {
                            with(it.data.loginResult) {
                                authRepository.saveToken(token)
                                UiStatus.ShowMessage(
                                    UiMessage.ResourceMessage(
                                        R.string.welcome_message,
                                        listOf(name)
                                    )
                                )
                            }
                        }
                        is NetworkResult.Error -> UiStatus.ShowError(
                            UiMessage.StringMessage(it.message)
                        )
                    }
                )
            }
        }
    }

    fun goToRegisterPage() {
        viewModelScope.launch(ioDispatcher) {
            authEventBus.setLoginPage(false)
        }
    }

    fun setEmailHasError(isError: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            emailHasError.value = isError
        }
    }

    fun setPasswordHasError(isError: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            passwordHasError.value = isError
        }
    }
}