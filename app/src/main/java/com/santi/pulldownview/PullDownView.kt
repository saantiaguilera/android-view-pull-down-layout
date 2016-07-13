package com.santi.pulldownview

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import java.lang.ref.WeakReference

/**
 *
 * Created by santi on 12/07/16.
 */
class PullDownView : FrameLayout {

    private val DEFAULT_TIME_SHOWING = 4000L

    val TIME_NO_EXPIRE = -1

    internal lateinit var header: View
    internal lateinit var content: View

    private val animator by lazy {
        var anim = Animator(this)
        anim.setCallback(object: Animator.Callback {
            override fun onContentHidden() {
                listener?.get()?.onContentHidden()
            }

            override fun onContentShown() {
                listener?.get()?.onContentShown()
            }
        })
        anim
    }

    private var listener: WeakReference<Callback>? = null

    private constructor(context: Context) : this(context, null) {}

    private constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0) {}

    private constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    internal fun build() {
        addView(content)
        addView(header)

        fun modifyContentHeight() {
            content.layoutParams.height = this@PullDownView.height - header.layoutParams.height
        }

        fun modifyContents() {
            if (content.layoutParams.height > this@PullDownView.height) {
                modifyContentHeight()
            }

            content.y = (header.layoutParams.height - content.layoutParams.height).toFloat()
        }

        if (content.layoutParams.height <= 0 || content.layoutParams.width <= 0) {
            afterMeasured {
                modifyContents()
            }
        } else modifyContents()
    }

    fun showContent() {
        animator.showContent()
    }

    fun hideContent() {
        animator.hideContent()
    }

    fun show(time: Long = DEFAULT_TIME_SHOWING) {
        val viewGroup = (context as Activity).findViewById(android.R.id.content) as ViewGroup
        viewGroup.addView(this)

        animator.start(time)
    }

    class Builder(activity: Activity) {

        private val context by lazy { activity }

        private var header: View? = null
        private var content: View? = null
        private var listener: Callback? = null

        fun header(view: View): Builder {
            header = view
            return this
        }

        fun content(view: View): Builder {
            content = view
            return this
        }

        fun listener(callback: Callback): Builder {
            listener = callback
            return this
        }

        fun build(): PullDownView {
            return PullDownView(context).apply {
                header = this@Builder.header?: View(context)
                content = this@Builder.content?: View(context)

                if (this@Builder.listener != null)
                    listener = WeakReference(this@Builder.listener!!)

                build()
            }
        }

    }

    interface Callback {
        fun onContentShown()
        fun onContentHidden()
    }

    interface Animations {
        fun start(time: Long)
    }

    inline fun <T: View> T.afterMeasured(crossinline stuff: T.() -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    stuff()
                }
            }
        })
    }

}
