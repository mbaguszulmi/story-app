package co.mbznetwork.storyapp.di

import co.mbznetwork.storyapp.BuildConfig
import co.mbznetwork.storyapp.datasource.api.StoryApi
import co.mbznetwork.storyapp.eventbus.AuthEventBus
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackendModule {
    @Provides
    @Singleton
    fun provideAuthHeader(
        authEventBus: AuthEventBus
    ) = Interceptor {
        with(it) {
            proceed(
                request().newBuilder().let { builder ->
                    authEventBus.getToken()?.let { token ->
                        builder.addHeader("Authorization", "Bearer $token")
                    } ?: builder
                }.build()
            ).also { response ->
                if (response.code == 401) {
                    authEventBus.needSignOut()
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().setLevel(
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    )

    @Provides
    @Singleton
    fun provideHttpClient(
        authHeader: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) = OkHttpClient.Builder()
        .addNetworkInterceptor(loggingInterceptor).addInterceptor(authHeader).build()

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(
        httpClient: OkHttpClient,
        gson: Gson
    ) = Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(httpClient)
        .build()

    @Provides
    @Singleton
    fun provideStoryApi(
        retrofit: Retrofit
    ) = retrofit.create(StoryApi::class.java)
}