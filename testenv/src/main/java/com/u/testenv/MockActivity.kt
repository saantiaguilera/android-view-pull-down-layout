package com.u.testenv

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.santi.pulldownview.PullDownView
import com.santi.pulldownview.contracts.ContentCallback
import com.santi.pulldownview.contracts.ViewCallback

class MockActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock)

        val headerView = View(this)
        headerView.setBackgroundColor(resources.getColor(R.color.colorAccent))
        headerView.layoutParams = FrameLayout.LayoutParams(640, 150)
        headerView.setOnClickListener { Toast.makeText(this@MockActivity, "Header was clicked", Toast.LENGTH_SHORT).show() }
        headerView.tag = 1

        val contentView = View(this)
        contentView.setBackgroundColor(resources.getColor(R.color.colorPrimary))

        val params = FrameLayout.LayoutParams(630, ViewGroup.LayoutParams.MATCH_PARENT)
        params.bottomMargin = 80
        contentView.layoutParams = params

        contentView.setOnClickListener { Toast.makeText(this@MockActivity, "Content was clicked", Toast.LENGTH_SHORT).show() }
        contentView.tag = 2

        PullDownView.Builder(this)
                .content(contentView)
                .header(headerView)
                .onViewVisibilityChanged {
                    Log.w(this@MockActivity.javaClass.name, "onViewDismissed")
                }
                .onContentVisibilityChanged ({
                    Log.w("", "onContentVisibilityChanged")
                }, {
                    Log.w("", "")
                })
                .build().showHeader(6000)
    }

}
