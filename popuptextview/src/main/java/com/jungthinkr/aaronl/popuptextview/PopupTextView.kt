package com.jungthinkr.aaronl.popuptextview

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import com.jungthinkr.aaronl.popuptextview.utils.hideKeyboard
import com.jungthinkr.aaronl.popuptextview.utils.showKeyboard
import java.util.*
import kotlin.Comparator


class PopupTextView : EditText {

    private var attrs: AttributeSet? = null
    private var defStyleAttr: Int = 0
    private var clone: EditText? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.attrs = attrs
        setupUI()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context) {
        this.attrs = attrs
        this.defStyleAttr = defStyleAttr
        setupUI()
    }

    private fun setupUI() {
        isFocusableInTouchMode = false

        val activity = (context as? Activity) ?: throw IllegalArgumentException("Context needs to be activity context")
        val rootView = activity.findViewById<ViewGroup>(R.id.content)
        val backgroundLayout = FrameLayout(context)


        clone = EditText(context, attrs, defStyleAttr)
        clone?.isFocusableInTouchMode = true

        clone?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                text = s
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /* no-op */
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /* no-op */
            }
        })

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            if (clone?.parent == rootView) {
                val halfOfParentWidth = (rootView?.width ?: 0) / 2
                val halfOfViewWidth = (clone?.width ?: 0) / 2

                clone?.translationY = r.bottom.toFloat().minus(clone?.height?.toFloat() ?: 0f).minus(300f)
                clone?.translationX = (halfOfParentWidth - halfOfViewWidth).toFloat()
            }
        }

        backgroundLayout.setBackgroundColor(Color.BLACK)
        backgroundLayout.alpha = 0.5f
        backgroundLayout.setOnClickListener {
            // remove pop up state
            clone?.hideKeyboard()
            rootView?.removeView(backgroundLayout)
            rootView?.removeView(clone)
        }


        setOnClickListener {
            rootView?.addView(backgroundLayout)
            rootView?.addView(clone)
            clone?.showKeyboard()
            val cloneBaseWidth = (rootView?.width ?: 0) * 2 / 3

            clone?.layoutParams?.width = cloneBaseWidth
            clone?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            clone?.textSize = textSize
        }
    }
}