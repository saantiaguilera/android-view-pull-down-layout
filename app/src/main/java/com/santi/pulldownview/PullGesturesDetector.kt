package com.santi.pulldownview

import android.view.MotionEvent
import android.view.View
import java.lang.ref.WeakReference

/**
 * Google you did it again ! This shit doesnt work ! GestureDetector.OnGestureListener
 * Created by santi on 12/07/16.
 */
internal class PullGesturesDetector(private val view: PullDownView) {

    private enum class STATE {
        SHOWN,
        HIDDEN
    }

    private lateinit var state: STATE

    private lateinit var callback: WeakReference<Callback?>

    fun setCallback(listener: Callback) {
        callback = WeakReference(listener)

        //Use view.header for someones and content for others
        view.content.setOnTouchListener(touchInstance)
        view.header.setOnTouchListener(touchInstance)
    }

    private fun end() {
        if (view.content.y + view.content.height > view.height / 2.5) {
            callback.get()?.showContent()
            state = STATE.SHOWN
        } else {
            callback.get()?.hideContent()
            state = STATE.HIDDEN
        }
    }

    private fun onScroll(p: Float): Boolean {
        callback.get()?.onScroll(p)
        return true
    }

    private fun onShowPress() {
        when (state) {
            STATE.HIDDEN -> {
                state = STATE.SHOWN
                callback.get()?.showContent()
            }

            STATE.SHOWN -> {
                state = STATE.HIDDEN
                callback.get()?.hideContent()
            }
        }

    }

    val touchInstance = object: View.OnTouchListener {
        var moved = false
        var y = 0

        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            if (motionEvent == null)
                return false

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moved = false
                    y = motionEvent.rawY.toInt()
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    moved = true
                    onScroll(motionEvent.rawY - y)
                    y = motionEvent.rawY.toInt()
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        when (view) {
                            this@PullGesturesDetector.view.header -> if (!this@PullGesturesDetector.view.header.performClick()) onShowPress()
                            this@PullGesturesDetector.view.content -> this@PullGesturesDetector.view.content.performClick()
                        }
                    } else end()
                    return true
                }

                else -> return false
            }
        }
    }

    interface Callback {
        fun onScroll(position: Float)
        fun showContent()
        fun hideContent()
    }

}