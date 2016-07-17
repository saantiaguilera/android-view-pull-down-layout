package com.santi.pulldownview

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.santi.pulldownview.contracts.GestureResponses
import com.santi.pulldownview.contracts.ViewVisibilityChanges
import java.lang.ref.WeakReference

/**
 * Ideally it should receive a contract instead of the pulldownview, but this is just for kotlin
 * purposes
 *
 * Created by santi on 12/07/16.
 */
internal class Animator(private val view: PullDownView) : GestureResponses {

    private val TRANSLATION_Y = "translationY"

    private var userInteracted = false
    private var contentShownOnce = false

    private var contentVisible = false

    private var listener: WeakReference<ViewVisibilityChanges>? = null

    init {
        PullGesturesDetector(view).setCallback(this)
    }

    fun setCallback(callback: ViewVisibilityChanges) {
        listener = WeakReference(callback)
    }

    override fun showContent() = with(ObjectAnimator.ofFloat(view.content, TRANSLATION_Y,
            view.content.y, view.header.height.toFloat())) {
        userInteracted = true
        contentShownOnce = true
        duration = view.activity.get().resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        start()

        if (!contentVisible)
            notifyContentShown()

        contentVisible = true
    }

    override fun hideContent() = with(ObjectAnimator.ofFloat(view.content, TRANSLATION_Y,
            view.content.y,
            (view.header.height.abs() - view.content.height.abs()).toFloat())) {
        userInteracted = true
        duration = view.activity.get().resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        addListener(object: android.animation.Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: android.animation.Animator?) { }
            override fun onAnimationCancel(p0: android.animation.Animator?) { }
            override fun onAnimationStart(p0: android.animation.Animator?) { }
            override fun onAnimationEnd(p0: android.animation.Animator?) {
                if (contentShownOnce)
                    hideHeader()
            }
        })
        start()

        if (contentVisible)
            notifyContentHidden()

        contentVisible = false
    }

    override fun onScroll(position: Float) {
        userInteracted = true

        when {
            !contentShownOnce && (view.content.y + view.content.height <= view.header.height &&
                                    position < 0) -> {
                hideHeader()
            }

            view.content.y + position <= view.header.height -> view.content.y += position

            else -> view.content.y = view.header.height.toFloat()
        }
    }

    private fun showHeader() {
        view.header.visibility = View.VISIBLE
        view.container.startAnimation(AnimationUtils.loadAnimation(view.activity.get(), R.anim.slide_in_bottom))
    }

    fun hideHeader() {
        val animation = AnimationUtils.loadAnimation(view.activity.get(), R.anim.slide_out_top)
        animation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.header.visibility = View.INVISIBLE
                listener?.get()?.onViewHidden()
            }

            override fun onAnimationStart(p0: Animation?) {
                view.content.visibility = View.INVISIBLE
            }

        })

        view.container.startAnimation(animation)
    }

    fun start(time: Long) {
        showHeader()

        if (time > 0) {
            view.container.postDelayed({
                if (!userInteracted)
                    hideHeader()
            }, time)
        }
    }

    private fun notifyContentHidden() {
        listener?.get()?.onContentHidden()
    }

    private fun notifyContentShown() {
        listener?.get()?.onContentShown()
    }

    fun Int.abs(): Int = if (this < 0) -this else this

}