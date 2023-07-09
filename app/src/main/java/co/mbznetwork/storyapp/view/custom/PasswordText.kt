package co.mbznetwork.storyapp.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import co.mbznetwork.storyapp.R

class PasswordText: MyTextInput, View.OnTouchListener {
    private lateinit var eyeDrawable: Drawable
    private lateinit var eyeSlashDrawable: Drawable
    private var isPasswordVisible = false
    private val endDrawable get() = if (isPasswordVisible) eyeSlashDrawable else eyeDrawable
    private val currentInputType get() = if (isPasswordVisible)
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
    else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

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
        eyeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye) as Drawable
        eyeSlashDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_slash) as Drawable
        inputType = currentInputType
        setOnTouchListener(this)
        setButtonDrawables(endOfTheText = eyeDrawable)

        addTextChangedListener {
            setErrorStatus((it?.toString() ?: "").let { str ->
                if (str.trim().length < 8) context.getString(R.string.error_password_min_length)
                else null
            })
        }
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        setButtonDrawables(endOfTheText = endDrawable)
        inputType = currentInputType
    }

    override fun onTouch(p0: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val viewButtonStart: Float
            val viewButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                viewButtonEnd = (endDrawable.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < viewButtonEnd -> isClearButtonClicked = true
                }
            } else {
                viewButtonStart = (width - paddingEnd - endDrawable.intrinsicWidth).toFloat()
                when {
                    event.x > viewButtonStart -> isClearButtonClicked = true
                }
            }

            return if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        togglePasswordVisibility()
                        true
                    }
                    else -> false
                }
            } else false
        }
        return false
    }
}