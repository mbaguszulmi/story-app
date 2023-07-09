package co.mbznetwork.storyapp.model.network

import co.mbznetwork.storyapp.datasource.api.model.response.BaseResponse

sealed class NetworkResult<T: BaseResponse> {
    data class Success<T: BaseResponse>(
        val data: T
    ): NetworkResult<T>()

    data class Error<T: BaseResponse>(
        val message: String
    ): NetworkResult<T>()
}
