package com.santi.pulldownview

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import java.lang.ref.WeakReference

/**
 * Created by santi on 12/07/16.
 */
internal class PullGesturesDetector(context: Context) : GestureDetector.OnGestureListener {

    private enum class STATE {
        UNDEFINED,
        FULL,
        EMPTY
    }

    private var state: STATE = STATE.UNDEFINED

    private lateinit var callback: WeakReference<Callback?>

    init {
        GestureDetectorCompat(context, this)
    }

    fun setCallback(listener: Callback) {
        callback = WeakReference(listener)
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
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

        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean = false

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, velY: Float): Boolean {
        when {
            velY > 0 -> {
                callback.get()?.showContent()
                state = STATE.FULL
            }
            velY < 0 -> {
                callback.get()?.hideContent()
                state = STATE.EMPTY
            }
        }
        return true
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        callback.get()?.onScroll(p1?.y?:p0?.y?:0f)
        state = STATE.UNDEFINED
        return true
    }

    override fun onShowPress(p0: MotionEvent?) {}
    override fun onLongPress(p0: MotionEvent?) {}

    interface Callback {
        fun onScroll(position: Float)
        fun showContent()
        fun hideContent()
    }

}