package co.mbznetwork.storyapp.datasource.api

import co.mbznetwork.storyapp.config.STORY_INITIAL_PAGE
import co.mbznetwork.storyapp.datasource.api.model.request.LoginRequest
import co.mbznetwork.storyapp.datasource.api.model.request.RegisterRequest
import co.mbznetwork.storyapp.datasource.api.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoryApi {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<GeneralResponse>

    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<GeneralResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = STORY_INITIAL_PAGE,
        @Query("size") size: Int = 20
    ): Response<GetStoriesResponse>

    @GET("stories/{id}")
    suspend fun getStory(@Path("id") id: String): Response<GetStoryResponse>
}
