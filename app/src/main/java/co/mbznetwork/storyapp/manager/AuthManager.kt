package co.mbznetwork.storyapp.manager

import co.mbznetwork.storyapp.di.IODispatcher
import co.mbznetwork.storyapp.eventbus.AuthEventBus
import co.mbznetwork.storyapp.model.auth.AuthenticationState
import co.mbznetwork.storyapp.repository.AuthRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class AuthManager @Inject constructor(
    private val authRepository: AuthRepository,
    private val authEventBus: AuthEventBus,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    val shouldLogin = authEventBus.state.mapNotNull {
        when(it) {
            is AuthenticationState.SignedIn -> false
            is AuthenticationState.Unauthenticated -> true
            else -> null
        }
    }.stateIn(this, SharingStarted.WhileSubscribed(), false)

    fun start() {
        syncAuth()
        monitorSignOutEvent()
    }

    fun stop() {
        this.cancel()
    }

    private fun syncAuth() {
        launch {
            authRepository.getToken().flowOn(ioDispatcher).cancellable().collectLatest {
                authEventBus.setState(
                    it?.let {
                        AuthenticationState.SignedIn(it)
                    } ?: AuthenticationState.Unauthenticated()
                )
            }
        }
    }

    private fun monitorSignOutEvent() {
        launch {
            authEventBus.state.filterIsInstance<AuthenticationState.NeedSignOut>().cancellable().flowOn(
                ioDispatcher
            ).collectLatest {
                authRepository.removeToken()
                authEventBus.setUnAuthorized()
            }
        }
    }
}