package co.mbznetwork.storyapp.datasource.api.model.response

data class LoginResponse(
    val loginResult: LoginResult = LoginResult(),
    override val error: Boolean = false,
    override val message: String = "",
): BaseResponse()

data class LoginResult(
    val userId: String = "",
    val name: String = "",
    val token: String = ""
)
