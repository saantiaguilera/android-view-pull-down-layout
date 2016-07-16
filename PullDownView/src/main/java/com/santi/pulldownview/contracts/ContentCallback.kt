package com.santi.pulldownview.contracts

/**
 *
 * Created by saguilera on 7/15/16.
 */
//With kotlin 1.1 we can do typedefs and change this for OnContentShown = () -> Unit
interface ContentCallback {
    fun onContentShown() {}
    fun onContentHidden() {}
}