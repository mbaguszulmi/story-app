package co.mbznetwork.storyapp.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun AppCompatActivity.activityLifecycle(
    lifecycle: Lifecycle.State = Lifecycle.State.RESUMED,
    callback: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(lifecycle) {
            callback()
        }
    }
}

fun Fragment.fragmentLifecycle(
    lifecycle: Lifecycle.State = Lifecycle.State.RESUMED,
    callback: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(lifecycle) {
            callback()
        }
    }
}