package com.santi.pulldownview

import android.app.Activity
import android.view.*
import android.widget.FrameLayout
import com.santi.pulldownview.contracts.ContentCallback
import com.santi.pulldownview.contracts.ViewCallback
import com.santi.pulldownview.contracts.ViewVisibilityChanges
import java.lang.ref.WeakReference

/**
 *
 * Created by santi on 12/07/16.
 */
class PullDownView(val activity: Activity) {

    private val DEFAULT_TIME_SHOWING = 0L

    internal val container = FrameLayout(activity)
    internal lateinit var header: View
    internal lateinit var content: View

    private val animator by lazy {
        var anim = Animator(this)
        anim.setCallback(object: ViewVisibilityChanges {
            override fun onViewHidden() {
                destroy()
                viewListener?.get()?.onViewDismissed()
            }

            override fun onContentHidden() {
                contentListener?.get()?.onContentHidden()
            }

            override fun onContentShown() {
                contentListener?.get()?.onContentShown()
            }
        })
        anim
    }

    private var contentListener: WeakReference<ContentCallback>? = null
    private var viewListener: WeakReference<ViewCallback>? = null

    init {
        container.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    internal fun build() {
        container.addView(content)
        container.addView(header)

        fun modifyContentHeight() {
            content.layoutParams.height = this@PullDownView.container.height - header.layoutParams.height
        }

        fun modifyContents() {
            if (content.layoutParams.height > this@PullDownView.container.height) {
                modifyContentHeight()
            }

            content.y = (header.layoutParams.height - content.layoutParams.height).toFloat()
        }

        if (content.layoutParams.height <= 0 || content.layoutParams.width <= 0) {
            container.afterMeasured {
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

    fun showHeader(time: Long = DEFAULT_TIME_SHOWING) {
        val viewGroup = activity.findViewById(android.R.id.content) as ViewGroup
        viewGroup.addView(container)

        animator.start(time)
    }

    fun hideHeader() {
        animator.hideHeader()
    }

    private fun destroy() {
        val viewGroup = activity.findViewById(android.R.id.content) as ViewGroup
        viewGroup.removeView(container)
    }

    class Builder(activity: Activity) {

        private val context by lazy { activity }

        //Header is nullable because we want to enforce the user to yes or yes use it. Content is optional
        private var header: View? = null
        private var content: View = createInvisibleContent()
        private var contentListener: ContentCallback? = null
        private var viewListener: ViewCallback? = null

        private fun createInvisibleContent(): View {
            val invisibleContent = View(context)
            invisibleContent.visibility = View.GONE
            return invisibleContent;
        }

        fun header(view: View): Builder {
            header = view
            return this
        }

        fun content(view: View): Builder {
            content = view
            return this
        }

        fun onContentVisibilityChanged(contentCallback: ContentCallback): Builder {
            contentListener = contentCallback
            return this
        }

        fun onViewVisibilityChanged(viewCallback: ViewCallback): Builder {
            viewListener = viewCallback
            return this
        }

        fun build(): PullDownView {
            return PullDownView(context).apply {
                header = this@Builder.header!!
                content = this@Builder.content

                if (this@Builder.contentListener != null)
                    contentListener = WeakReference(this@Builder.contentListener!!)

                if(this@Builder.viewListener != null)
                    viewListener = WeakReference(this@Builder.viewListener!!)

                build()
            }
        }

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
