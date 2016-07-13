package com.santi.pulldownview

import android.content.Context
import android.graphics.Point
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.lang.ref.WeakReference

/**
 * Google you did it again ! This shit doesnt work ! GestureDetector.OnGestureListener
 * Created by santi on 12/07/16.
 */
internal class PullGesturesDetector(private val view: PullDownView) {

    private enum class STATE {
        UNDEFINED,
        FULL,
        EMPTY
    }

    private var state: STATE = STATE.UNDEFINED

    private lateinit var callback: WeakReference<Callback?>

    fun setCallback(listener: Callback) {
        callback = WeakReference(listener)

        //Use view.header for someones and content for others
        view.setOnTouchListener (object: View.OnTouchListener {
            var moved = false
            var y = 0

            override fun onTouch(p0: View?, motionEvent: MotionEvent?): Boolean {
                if (motionEvent == null)
                    return false

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        moved = false
                        y = motionEvent.y.toInt()
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        moved = true
                        onScroll(motionEvent.y - y)
                        y = motionEvent.y.toInt()
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (!moved)
                            onShowPress()
                        else end(y.toFloat())
                        return true
                    }

                    else -> return false
                }
            }
        })
    }

    private fun end(y: Float) {
        if (y > view.content.height / 2.5)
            callback.get()?.showContent()
        else callback.get()?.hideContent()
    }

    private fun onScroll(p: Float): Boolean {
        callback.get()?.onScroll(p)
        state = STATE.UNDEFINED
        return true
    }

    private fun onShowPress() {
        when (state) {
            STATE.EMPTY, STATE.UNDEFINED -> {
                state = STATE.FULL
                callback.get()?.showContent()
            }

            STATE.FULL -> {
                state = STATE.EMPTY
                callback.get()?.hideContent()
            }
        }

    }

    interface Callback {
        fun onScroll(position: Float)
        fun showContent()
        fun hideContent()
    }

}