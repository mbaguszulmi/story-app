package co.mbznetwork.storyapp.util

import co.mbznetwork.storyapp.datasource.api.model.response.BaseResponse
import co.mbznetwork.storyapp.datasource.api.model.response.GeneralResponse
import co.mbznetwork.storyapp.model.network.NetworkResult
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import timber.log.Timber

object ApiUtil {
    suspend fun <T: BaseResponse>finalize(gson: Gson, call: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    if (!it.error) NetworkResult.Success(it)
                    else NetworkResult.Error(it.message)
                } ?: NetworkResult.Error("Error occurred")
            } else {
                response.errorBody()?.let {
                    try {
                        val body = gson.fromJson(
                            it.charStream(),
                            GeneralResponse::class.java
                        ) as BaseResponse
                        NetworkResult.Error(body.message)
                    } catch (e: JsonSyntaxException) {
                        NetworkResult.Error(response.message())
                    }
                } ?: NetworkResult.Error("Error occurred")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error when processing request.")
            NetworkResult.Error(e.message ?: "Error occurred")
        }
    }
}
