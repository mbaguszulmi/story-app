package co.mbznetwork.storyapp.util

import android.content.ContextWrapper
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import co.mbznetwork.storyapp.R
import com.google.android.material.snackbar.Snackbar

object MessageDialog {
    fun showError(rootView: View, aView: View, message: String?="") {
        showTop(rootView, aView, message, R.color.colorError)
    }

    fun showMessage(rootView: View, aView: View, message: String? = "") {
        showTop(rootView, aView, message)
    }

    private fun showTop(rootView: View, aView: View, message: String?="", color: Int? = null) {
        Snackbar.make(rootView, message?:"Unknown error", Snackbar.LENGTH_LONG).apply {
            setTextColor(ContextWrapper(rootView.context).getColor(R.color.white))
            color?.let { view.setBackgroundResource(it) }
            view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
                val offsetViewBounds = Rect()
                aView.getDrawingRect(offsetViewBounds)
                (rootView as ViewGroup).offsetDescendantRectToMyCoords(aView, offsetViewBounds)
                gravity = Gravity.TOP
                topMargin = offsetViewBounds.top + aView.layoutParams.height
            }
            animationMode = Snackbar.ANIMATION_MODE_FADE
        }.show()
    }

    fun showError(rootView: View, message: String?="") {
        Snackbar.make(rootView, message?:"Unknown error", Snackbar.LENGTH_LONG).apply {
            view.setBackgroundResource(R.color.colorError)
            animationMode = Snackbar.ANIMATION_MODE_FADE
        }.show()

    }
}