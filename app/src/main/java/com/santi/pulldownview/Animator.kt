package com.santi.pulldownview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 *
 * Created by santi on 12/07/16.
 */
internal class Animator(private val view: PullDownView) : PullGesturesDetector.Callback, PullDownView.Animations {

    private var userInteracted = false

    init {
        PullGesturesDetector(view).setCallback(this)
    }

    override fun showContent() = with(ObjectAnimator.ofInt(this, "translationY", view.header.height)) {
        userInteracted = true
        duration = view.context.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        start()
    }

    override fun hideContent() = with(ObjectAnimator.ofInt(this, "translationY", view.header.height.abs() - view.content.height.abs())) {
        userInteracted = true
        duration = view.context.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
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