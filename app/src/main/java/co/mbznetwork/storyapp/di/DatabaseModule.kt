package co.mbznetwork.storyapp.di

import android.content.Context
import androidx.room.Room
import co.mbznetwork.storyapp.datasource.database.APP_DATABASE_NAME
import co.mbznetwork.storyapp.datasource.database.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, AppDb::class.java, APP_DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideStoryDao(database: AppDb) = database.storyDao()

    @Provides
    @Singleton
    fun provideStoryRemoteKeyDao(database: AppDb) = database.storyRemoteKeyDao()
}