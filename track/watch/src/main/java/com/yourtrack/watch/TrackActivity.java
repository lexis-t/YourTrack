package com.yourtrack.watch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

public class TrackActivity extends WearableActivity {

    private View mBackgroundView;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mBackgroundView = findViewById(R.id.background_view);
        mTextView = findViewById(R.id.main_indicator);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        mBackgroundView.setBackgroundColor(getResources().getColor(R.color.ambient_background));
        mTextView.setTextColor(getResources().getColor(R.color.ambient_foreground));
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        mBackgroundView.setBackgroundColor(getResources().getColor(R.color.interactive_background));
        mTextView.setTextColor(getResources().getColor(R.color.interactive_heartrate));

    }
}
