package co.mbznetwork.storyapp

import android.app.Application
import android.content.Intent
import co.mbznetwork.storyapp.di.AppScope
import co.mbznetwork.storyapp.manager.AuthManager
import co.mbznetwork.storyapp.view.AuthActivity
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class StoryApp: Application() {

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    @AppScope
    lateinit var appScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        authManager.start()
        initTimber()
        observeShouldLogin()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun observeShouldLogin() {
        appScope.launch {
            authManager.shouldLogin.collectLatest {
                if (it) startActivity(
                    Intent(this@StoryApp, AuthActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            }
        }
    }

    override fun onTerminate() {
        authManager.stop()
        appScope.cancel()
        super.onTerminate()
    }
}
