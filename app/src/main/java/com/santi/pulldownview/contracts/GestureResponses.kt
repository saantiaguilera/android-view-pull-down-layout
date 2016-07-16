package com.santi.pulldownview.contracts

/**
 * Created by saguilera on 7/15/16.
 */
internal interface GestureResponses {
    fun onScroll(position: Float)
    fun showContent()
    fun hideContent()
}