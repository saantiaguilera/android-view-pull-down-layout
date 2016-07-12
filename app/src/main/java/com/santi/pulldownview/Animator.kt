package com.santi.pulldownview

import android.content.Context
import java.lang.ref.WeakReference

/**
 * Created by santi on 12/07/16.
 */
internal class Animator(context: Context) : PullGesturesDetector.Callback {

    init {
        PullGesturesDetector(context).setCallback(this)
    }

    private lateinit var callback: WeakReference<PullGesturesDetector.Callback?>

    fun setCallback(listener: PullGesturesDetector.Callback) {
        callback = WeakReference(listener)
    }

    override fun onFullscreen() {
        callback.get()?.onFullscreen()
    }

    override fun onEmpty() {
        callback.get()?.onEmpty()
    }

    override fun onScroll(position: Float) {
        callback.get()?.onScroll(position)
    }

    //The callback hell is real!
    interface Callback {
        fun onFullscreen()
        fun onEmpty()
        fun onScroll(position: Float)
    }

}