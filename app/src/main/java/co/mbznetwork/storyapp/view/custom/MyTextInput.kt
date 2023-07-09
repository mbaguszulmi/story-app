package co.mbznetwork.storyapp.view.custom

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

abstract class MyTextInput: TextInputEditText {
    var errorChangedListener : ((String?) -> Unit)? = null

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ): super(context, attrs, defStyleAttr)

    fun setErrorStatus(error: String?) {
        errorChangedListener?.invoke(error)
    }
}