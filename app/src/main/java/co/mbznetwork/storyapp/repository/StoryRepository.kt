package co.mbznetwork.storyapp.repository

import co.mbznetwork.storyapp.datasource.api.StoryApi
import co.mbznetwork.storyapp.datasource.api.model.response.GeneralResponse
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.util.ApiUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val storyApi: StoryApi,
    private val gson: Gson
) {
    suspend fun addStory(
        photo: File,
        mime: String,
        description: String
    ): NetworkResult<GeneralResponse> {
        val photoMultipart = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            photo.asRequestBody(mime.toMediaType())
        )
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        return ApiUtil.finalize(storyApi.addStory(photoMultipart, descriptionRequestBody), gson)
    }

    suspend fun getStories() = ApiUtil.finalize(storyApi.getStories(), gson)

    suspend fun getStory(id: String) = ApiUtil.finalize(storyApi.getStory(id), gson)
}