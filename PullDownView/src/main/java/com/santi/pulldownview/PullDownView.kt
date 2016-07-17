package com.santi.pulldownview

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.santi.pulldownview.contracts.ContentCallback
import com.santi.pulldownview.contracts.ViewCallback
import com.santi.pulldownview.contracts.ViewVisibilityChanges
import java.lang.ref.WeakReference

/**
 *
 * Created by santi on 12/07/16.
 */
class PullDownView(activity: Activity) {

    private val DEFAULT_TIME_SHOWING = 0L

    internal val activity = WeakReference<Activity>(activity)

    internal val container = FrameLayout(activity)
    internal lateinit var header: View
    internal lateinit var content: View

    private val animator by lazy {
        val anim = Animator(this)
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
            //Also decrease with the bottom margin only in this case because its when the margin
            //Will be visible (in the other cases it wont be because its not fullscreen the content
            val params = content.layoutParams
            params.height = container.height -
                    header.height -
                    (content.layoutParams as FrameLayout.LayoutParams).bottomMargin
            content.layoutParams = params
        }

        fun modifyContents() {
            if (content.height > container.height ||
                    content.layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                modifyContentHeight()
            }

            content.y = (header.height - content.layoutParams.height).toFloat()
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
        //val viewGroup = activity.findViewById(android.R.id.content) as ViewGroup
        val viewGroup = activity.get().getWindow().getDecorView().findViewById(android.R.id.content) as ViewGroup
        viewGroup.addView(container)

        animator.start(time)
    }

    fun hideHeader() {
        animator.hideHeader()
    }

    private fun destroy() {
        val viewGroup = activity.get().findViewById(android.R.id.content) as ViewGroup
        viewGroup.removeView(container)
        container.removeAllViews()
    }

    class Builder(activity: Activity) {

        private val activity = WeakReference<Activity>(activity)

        //Header is nullable because we want to enforce the user to yes or yes use it. Content is optional
        private var header: View? = null
        private var content: View = createInvisibleContent()
        private var contentListener: ContentCallback? = null
        private var viewListener: ViewCallback? = null

        private fun createInvisibleContent(): View {
            val invisibleContent = View(activity.get())
            invisibleContent.visibility = View.GONE
            return invisibleContent
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

        //Once kotlin 1.1 reaches we will have typedefs and this wont be needed :))))
        fun onViewVisibilityChanged(viewVisibilityChanged: () -> Unit = {}): Builder {
            onViewVisibilityChanged(object: ViewCallback {
                override fun onViewDismissed() {
                    viewVisibilityChanged()
                }
            })

            return this
        }

        fun onContentVisibilityChanged(contentShown: () -> Unit = {}, contentHidden: () -> Unit = {}): Builder {
            onContentVisibilityChanged(object: ContentCallback {
                override fun onContentShown() {
                    contentShown()
                }

                override fun onContentHidden() {
                    contentHidden()
                }
            })

            return this
        }

        fun build(): PullDownView {
            return PullDownView(activity.get()).apply {
                header = this@Builder.header!!
                content = this@Builder.content

                (header.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER_HORIZONTAL
                (content.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER_HORIZONTAL

                if (this@Builder.contentListener != null)
                    contentListener = WeakReference(this@Builder.contentListener!!)

                if(this@Builder.viewListener != null)
                    viewListener = WeakReference(this@Builder.viewListener!!)

                build()
            }
        }

    }

    //Since the user can call this notification in the oncreate() or in some point were the activity isnt still drawn, we need to use the hateful OnGlobalLayout
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
