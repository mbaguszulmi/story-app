package co.mbznetwork.storyapp.util

import androidx.fragment.app.FragmentManager
import co.mbznetwork.storyapp.view.fragment.LoadingOverlay

object LoadingDialogUtil {

    fun show(fragmentManager: FragmentManager): LoadingOverlay {
        return LoadingOverlay().apply {
            show(fragmentManager, "LoadingOverlay")
        }
    }

    fun dismiss(fragment: LoadingOverlay) {
        fragment.dismiss()
    }

}