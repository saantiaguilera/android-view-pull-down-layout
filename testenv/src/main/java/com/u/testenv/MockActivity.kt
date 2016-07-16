package com.u.testenv

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.santi.pulldownview.PullDownView

class MockActivity : Activity() {

    fun createHeader(): View {
        val headerView = ImageView(this)
        headerView.setImageResource(R.drawable.header)
        headerView.adjustViewBounds = true
        headerView.layoutParams = FrameLayout.LayoutParams(700, ViewGroup.LayoutParams.WRAP_CONTENT)
        headerView.setOnClickListener { Toast.makeText(this@MockActivity, "Header was clicked", Toast.LENGTH_SHORT).show() }
        return headerView
    }

    fun createContent(): View {
        val contentView = ImageView(this)

        val params = FrameLayout.LayoutParams(690, ViewGroup.LayoutParams.MATCH_PARENT)


        contentView.layoutParams = params
        contentView.scaleType = ImageView.ScaleType.CENTER_CROP
        contentView.setImageResource(R.drawable.content)
        return contentView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock)

        findViewById(R.id.activity_mock_button).setOnClickListener {
            PullDownView.Builder(this)
                    .content(createContent())
                    .header(createHeader())
                    .onViewVisibilityChanged {
                        Log.w(this@MockActivity.javaClass.name, "onViewDismissed")
                    }
                    .onContentVisibilityChanged ({
                        Log.w("", "onContentVisibilityShown")
                    }, {
                        Log.w("", "onContentVsibilityHidden")
                    })
                    .build().showHeader(2000)
        }
    }

}
