package co.mbznetwork.storyapp.view.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.widget.addTextChangedListener
import co.mbznetwork.storyapp.R

class EmailText: MyTextInput {
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
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        addTextChangedListener {
            setErrorStatus((it?.toString() ?: "").let { str ->
                if (str.isBlank()) context.getString(R.string.error_empty_email)
                else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches())
                    context.getString(R.string.error_invalid_email)
                else null
            })
        }
    }
}
