package com.santi.pulldownview

import android.app.Activity
import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout

/**
 *
 * Created by santi on 12/07/16.
 */
class PullDownView : FrameLayout, PullGesturesDetector.Callback {

    private val DEFAULT_TIME_SHOWING = 4000L

    internal lateinit var header: View
    internal lateinit var content: View

    private val animator by lazy { Animator(this) }

    private constructor(context: Context) : this(context, null) {}

    private constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0) {}

    private constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    internal fun build() {
        addView(content)
        addView(header)

        content.visibility = GONE //until i do it

        content.afterMeasured {
            if (content.layoutParams.height > this@PullDownView.layoutParams.height) {
                header.afterMeasured { content.layoutParams.height = this@PullDownView.layoutParams.height - header.layoutParams.height }
            }
        }
    }

    fun show(time: Long = DEFAULT_TIME_SHOWING) {
        val viewGroup = (context as Activity).findViewById(android.R.id.content) as ViewGroup
        viewGroup.addView(this)

        animator.start(time)
    }

    override fun onScroll(position: Float) {
    }

    override fun onFullscreen() {
    }

    override fun onEmpty() {
    }

    class Builder(activity: Activity) {

        private val context by lazy { activity }

        private var header: View? = null
        private var content: View? = null

        fun header(view: View): Builder {
            header = view
            return this
        }

        fun content(view: View): Builder {
            content = view
            return this
        }

        fun build(): PullDownView {
            return PullDownView(context).apply {
                header = this@Builder.header?: View(context)
                content = this@Builder.content?: View(context)
                build()
            }
        }

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
