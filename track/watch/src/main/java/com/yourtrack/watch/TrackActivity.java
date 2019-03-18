package com.yourtrack.watch;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yourtrack.dataservice.DataService;

public class TrackActivity extends WearableActivity {

    private static final String LOG_TAG = "YourTrack";

    private static final int REQUEST_ID_BODY_SENSORS = 1;

    private View backgroundView;
    private TextView textIndicatorView;
    private Button stopButton;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String extraSensor = intent.getStringExtra(DataService.EXTRA_SENSOR);
                if (extraSensor != null && extraSensor.equals(DataService.EXTRA_SENSOR_HR)) {
                    float value = intent.getFloatExtra(DataService.EXTRA_VALUE, 0.0f);
                    int accuracy = intent.getIntExtra(DataService.EXTRA_ACCURACY, -1);
                    long timestamp = intent.getLongExtra(DataService.EXTRA_TIMESTAMP, -1L);

                    runOnUiThread(()->{
                        textIndicatorView.setText(String.format("%d", (int)value));
                    });
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        backgroundView = findViewById(R.id.background_view);
        textIndicatorView = findViewById(R.id.main_indicator);
        stopButton = findViewById(R.id.stop_button);

        stopButton.setOnClickListener(v -> {
            DataService.stopTracking(this);
            finish();
        });

        // Enables Always-on
        setAmbientEnabled();

        IntentFilter f = new IntentFilter(DataService.ACTION_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, f);

        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG_TAG, "Requesting BODY_SENSORS permission");
            requestPermissions(new String[] {Manifest.permission.BODY_SENSORS}, REQUEST_ID_BODY_SENSORS);
        }
        else {
            DataService.startTracking(this);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ID_BODY_SENSORS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(LOG_TAG, "BODY_SENSORS permission is granted");
                DataService.startTracking(this);
            }
            else {
                Log.i(LOG_TAG, "BODY_SENSORS permission is denied");
            }
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        backgroundView.setBackgroundColor(getResources().getColor(R.color.ambient_background));
        textIndicatorView.setTextColor(getResources().getColor(R.color.ambient_foreground));
        stopButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        backgroundView.setBackgroundColor(getResources().getColor(R.color.interactive_background));
        textIndicatorView.setTextColor(getResources().getColor(R.color.interactive_heartrate));
        stopButton.setVisibility(View.VISIBLE);

    }
}
