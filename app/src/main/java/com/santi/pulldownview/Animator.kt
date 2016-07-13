package com.santi.pulldownview

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 * Created by santi on 12/07/16.
 */
internal class Animator(private val view: PullDownView) : PullGesturesDetector.Callback, PullDownView.Animations {

    private var userInteracted = false

    init {
        PullGesturesDetector(view.context).setCallback(this)
    }

    override fun showContent() {
    }

    override fun hideContent() {
    }

    override fun onScroll(position: Float) {
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

}