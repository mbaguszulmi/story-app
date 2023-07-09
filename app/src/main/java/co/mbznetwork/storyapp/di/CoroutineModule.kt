package co.mbznetwork.storyapp.di

import co.mbznetwork.storyapp.model.dispatcher.DefaultDispatcherProvider
import co.mbznetwork.storyapp.model.dispatcher.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @IODispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(dispatcherProvider: DispatcherProvider) = dispatcherProvider.main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(dispatcherProvider: DispatcherProvider) = dispatcherProvider.default

    @Provides
    @AppScope
    fun provideAppScope(@IODispatcher ioDispatcher: CoroutineDispatcher) = CoroutineScope(
        SupervisorJob() + ioDispatcher)

}