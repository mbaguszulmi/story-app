package co.mbznetwork.storyapp.di

import android.content.Context
import co.mbznetwork.storyapp.datasource.datastore.DataStore
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ) = DataStore(
        context,
        GsonBuilder().create()
    )
}
