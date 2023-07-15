package co.mbznetwork.storyapp.datasource.api

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
        @PartMap requestBody: Map<String, @JvmSuppressWildcards RequestBody>,
    ): Response<GeneralResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): Response<GetStoriesResponse>

    @GET("stories/{id}")
    suspend fun getStory(@Path("id") id: String): Response<GetStoryResponse>
}
