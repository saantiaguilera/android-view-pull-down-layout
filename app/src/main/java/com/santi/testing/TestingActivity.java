package com.santi.testing;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.santi.pulldownview.PullDownView;
import com.santi.pulldownview.R;

public class TestingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        View headerView = new View(this);
        headerView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        headerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TestingActivity.this, "Header was clicked", Toast.LENGTH_SHORT).show();
            }
        });
        headerView.setTag(1);

        View contentView = new View(this);
        contentView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        contentView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
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
                .build().show(6000);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
