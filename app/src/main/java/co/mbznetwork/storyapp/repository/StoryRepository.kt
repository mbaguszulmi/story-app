package co.mbznetwork.storyapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import co.mbznetwork.storyapp.datasource.api.StoryApi
import co.mbznetwork.storyapp.datasource.api.model.response.GeneralResponse
import co.mbznetwork.storyapp.datasource.database.dao.StoryDao
import co.mbznetwork.storyapp.datasource.database.entity.Story
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.repository.mediator.StoryRemoteMediator
import co.mbznetwork.storyapp.util.ApiUtil
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val storyApi: StoryApi,
    private val storyRemoteMediator: StoryRemoteMediator,
    private val storyDao: StoryDao,
    private val gson: Gson
) {
    suspend fun addStory(
        photo: File,
        mime: String,
        description: String,
        latLng: LatLng? = null
    ): NetworkResult<GeneralResponse> {
        val photoMultipart = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            photo.asRequestBody(mime.toMediaType())
        )

        val requestBody = mutableMapOf<String, RequestBody>().apply {
            put("description", description.toRequestBody("text/plain".toMediaType()))
            latLng?.run {
                put("lat", latitude.toString().toRequestBody("text/plain".toMediaType()))
                put("lon", longitude.toString().toRequestBody("text/plain".toMediaType()))
            }
        }
        return ApiUtil.finalize(gson) {
            storyApi.addStory(
                photoMultipart,
                requestBody
            )
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3,
                initialLoadSize = 6
            ),
            remoteMediator = storyRemoteMediator,
            pagingSourceFactory = {
                storyDao.getStories()
            }
        ).flow
    }

    suspend fun getStoriesWithLocation() = ApiUtil.finalize(gson) {
        storyApi.getStories(location = 1)
    }

    suspend fun getStory(id: String) = ApiUtil.finalize(gson) {
        storyApi.getStory(id)
    }
}