package com.santi.pulldownview.contracts

/**
 * Created by saguilera on 7/15/16.
 */
internal interface ViewVisibilityChanges {
    fun onContentHidden()
    fun onContentShown()
    fun onViewHidden()
}