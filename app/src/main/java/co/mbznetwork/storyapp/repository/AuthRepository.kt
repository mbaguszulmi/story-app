package co.mbznetwork.storyapp.repository

import co.mbznetwork.storyapp.datasource.api.StoryApi
import co.mbznetwork.storyapp.datasource.api.model.request.LoginRequest
import co.mbznetwork.storyapp.datasource.api.model.request.RegisterRequest
import co.mbznetwork.storyapp.datasource.datastore.AuthDataStore
import co.mbznetwork.storyapp.util.ApiUtil
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val storyApi: StoryApi,
    private val gson: Gson,
    private val authDataStore: AuthDataStore
) {
    suspend fun register(body: RegisterRequest) = ApiUtil.finalize(storyApi.register(body), gson)

    suspend fun login(body: LoginRequest) = ApiUtil.finalize(storyApi.login(body), gson)

    fun getToken() = authDataStore.get()

    suspend fun saveToken(token: String) = authDataStore.store(token)

    suspend fun removeToken() = authDataStore.remove()
}
