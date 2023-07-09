package co.mbznetwork.storyapp.datasource.api.model.request

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
