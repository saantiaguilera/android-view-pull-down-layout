package com.santi.pulldownview

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.santi.pulldownview.contracts.GestureResponses
import java.lang.ref.WeakReference

/**
 * Google you did it again ! This shit doesnt work ! <=GestureDetector.OnGestureListener
 * Created by santi on 12/07/16.
 */
internal class PullGesturesDetector(private val view: PullDownView) {

    private enum class STATE {
        SHOWN,
        HIDDEN
    }

    private lateinit var state: STATE

    private lateinit var callback: WeakReference<GestureResponses?>

    fun setCallback(listener: GestureResponses) {
        callback = WeakReference(listener)

        if (view.content.visibility == View.VISIBLE)
            view.content.setOnTouchListener(PullGesturesTouchListener())

        view.header.setOnTouchListener(PullGesturesTouchListener())
    }

    private fun end() {
        if (view.content.visibility == View.VISIBLE) {
            if (view.content.y + view.content.height > (view.container.height / 2) + view.header.height) {
                callback.get()?.showContent()
                state = STATE.SHOWN
            } else {
                callback.get()?.hideContent()
                state = STATE.HIDDEN
            }
        } else callback.get()?.hideContent()
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

    private inner class PullGesturesTouchListener : View.OnTouchListener {

        val gestureDetector by lazy {
            GestureDetector(view.activity, GestureListener())
        }

        var moved = false
        var y = 0

        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            if (motionEvent == null)
                return false

            val gestureResult = gestureDetector.onTouchEvent(motionEvent)

            if (motionEvent.action != MotionEvent.ACTION_DOWN && gestureResult)
                return true

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

    private inner class GestureListener: GestureDetector.SimpleOnGestureListener() {

        val SWIPE_THRESHOLD = 100
        val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val diffY = e2!!.y - e1!!.y

            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0)
                    callback.get()?.showContent()
                else callback.get()?.hideContent()

                return true
            }

            return false
        }

        //Because of issue were it doesnt start tracking until onDown is recvd
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

    }

}