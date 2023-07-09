package co.mbznetwork.storyapp.model.auth

sealed class AuthenticationState {
    data class SignedIn(
        val token: String
    ): AuthenticationState()

    data class Unauthenticated(
        val reason: ReasonUnauthenticated = ReasonUnauthenticated.NOT_SIGNED_IN
    ): AuthenticationState()

    object NeedSignOut: AuthenticationState()
}

enum class ReasonUnauthenticated {
    ERROR,
    NOT_SIGNED_IN,
}
