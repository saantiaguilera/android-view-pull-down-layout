package com.santi.testing;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.santi.pulldownview.PullDownView;
import com.santi.pulldownview.R;

public class TestingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        View blackView = new View(this);
        blackView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        blackView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
        blackView.setTag(1);

        View blackView2 = new View(this);
        blackView2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        blackView2.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500));
        blackView2.setTag(2);

        new PullDownView.Builder(this)
                .content(blackView2)
                .header(blackView)
                .build().show(6000);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
