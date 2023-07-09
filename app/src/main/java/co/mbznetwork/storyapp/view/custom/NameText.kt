package co.mbznetwork.storyapp.view.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.core.widget.addTextChangedListener
import co.mbznetwork.storyapp.R

class NameText: MyTextInput {
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
        inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        addTextChangedListener {
            setErrorStatus((it?.toString() ?: "").let { str ->
                if (str.isBlank()) context.getString(R.string.error_empty_name)
                else null
            })
        }
    }
}
