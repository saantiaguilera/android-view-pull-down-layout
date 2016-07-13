package com.santi.pulldownview

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 *
 * Created by santi on 12/07/16.
 */
internal class Animator(private val view: PullDownView) : PullGesturesDetector.Callback, PullDownView.Animations {

    private var userInteracted = false
    private var contentShownOnce = false

    init {
        PullGesturesDetector(view).setCallback(this)
    }

    override fun showContent() = with(ObjectAnimator.ofFloat(view.content, View.TRANSLATION_Y, view.header.height.toFloat())) {
        userInteracted = true
        contentShownOnce = true
        duration = view.context.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        start()
    }

    override fun hideContent() = with(ObjectAnimator.ofFloat(view.content, View.TRANSLATION_Y, (view.header.height.abs() - view.content.height.abs()).toFloat())) {
        userInteracted = true
        duration = view.context.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
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
    }

    override fun onScroll(offset: Float) {
        userInteracted = true
        view.content.y += offset
    }

    private fun showHeader() {
        view.header.visibility = View.VISIBLE
        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.slide_in_bottom))
    }

    private fun hideHeader() {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_out_top)
        animation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.header.visibility = View.INVISIBLE
            }

            override fun onAnimationStart(p0: Animation?) {
                view.content.visibility = View.INVISIBLE
            }

        })

        view.startAnimation(animation)
    }

    override fun start(time: Long) {
        showHeader()

        if (time > 0) {
            view.postDelayed({
                if (!userInteracted)
                    hideHeader()
            }, time)
        }
    }

    fun Int.abs(): Int = if (this < 0) -this else this

}