package co.mbznetwork.storyapp.datasource.api.model.response

abstract class BaseResponse {
    abstract val error: Boolean
    abstract val message: String
}
