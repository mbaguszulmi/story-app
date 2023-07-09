package co.mbznetwork.storyapp.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.forEach
import com.google.android.material.textfield.TextInputLayout

class MyTextInputLayout: TextInputLayout {
    var errorChangedListener : ((Boolean) -> Unit)? = null
    private lateinit var onGlobalLayoutListener: OnGlobalLayoutListener

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ): super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        onGlobalLayoutListener = OnGlobalLayoutListener {
            findMyTextInput()?.apply {
                errorChangedListener = {
                    this@MyTextInputLayout.error = it
                    errorIconDrawable = null
                    isErrorEnabled = !it.isNullOrBlank()
                }
                viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)
        errorChangedListener?.invoke(enabled)
    }

    private fun findMyTextInput(view: ViewGroup): MyTextInput? {
        view.forEach {
            when(it) {
                is ViewGroup -> findMyTextInput(it)?.let { textInput ->
                    return textInput
                }
                is MyTextInput -> return it
                else -> {}
            }
        }

        return null
    }

    private fun findMyTextInput() = findMyTextInput(this)
}