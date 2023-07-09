package co.mbznetwork.storyapp.datasource.api.model.response

data class GeneralResponse(
    override val error: Boolean = false,
    override val message: String = ""
): BaseResponse()