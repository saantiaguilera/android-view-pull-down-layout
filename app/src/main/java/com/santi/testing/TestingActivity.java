package com.santi.testing;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.santi.pulldownview.PullDownView;
import com.santi.pulldownview.R;
import com.santi.pulldownview.contracts.ContentCallback;
import com.santi.pulldownview.contracts.ViewCallback;

import java.lang.reflect.Field;
import java.util.Map;

public class TestingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        View headerView = new View(this);
        headerView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        headerView.setLayoutParams(new FrameLayout.LayoutParams(640, 150));
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TestingActivity.this, "Header was clicked", Toast.LENGTH_SHORT).show();
            }
        });
        headerView.setTag(1);

        View contentView = new View(this);
        contentView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(630, ViewGroup.LayoutParams.MATCH_PARENT);
        params.bottomMargin = 80;
        contentView.setLayoutParams(params);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TestingActivity.this, "Content was clicked", Toast.LENGTH_SHORT).show();
            }
        });
        contentView.setTag(2);

        new PullDownView.Builder(this)
                .content(contentView)
                .header(headerView)
                .onViewVisibilityChanged(new ViewCallback() {
                    @Override
                    public void onViewDismissed() {
                        Log.w(this.getClass().getName(), "onViewDismissed");
                    }
                })
                .onContentVisibilityChanged(new ContentCallback() {
                    @Override
                    public void onContentShown() {
                        Log.w(this.getClass().getName(), "onContentShown");
                    }

                    @Override
                    public void onContentHidden() {
                        Log.w(this.getClass().getName(), "onContentHidden");
                    }
                })
                .build().showHeader(6000);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
