package com.yourtrack.dataservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DataService extends Service {
    private final static String LOG_TAG = "YourTrack";

    private final static String ACTION_START = "ACTION_START";
    private final static String ACTION_STOP = "ACTION_STOP";

    public final static String ACTION_DATA = "ACTION_DATA";
    public final static String EXTRA_SENSOR = "sensor";
    public final static String EXTRA_VALUE = "value";
    public final static String EXTRA_ACCURACY = "accuracy";
    public final static String EXTRA_TIMESTAMP = "timestamp";

    public final static String EXTRA_SENSOR_HR = "heartrate";
//    public final static String EXTRA_ACCURACY = "accuracy";

    public DataService() {
    }

    SensorManager sensorManager;
    SensorEventListener hrListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.i(LOG_TAG, "Heart rate: " + event.values[0] + " , accuracy: " + event.accuracy);

            Intent i = new Intent(ACTION_DATA);
            i.putExtra(EXTRA_SENSOR, EXTRA_SENSOR_HR);
            i.putExtra(EXTRA_VALUE, event.values[0]);
            i.putExtra(EXTRA_ACCURACY, event.accuracy);
            i.putExtra(EXTRA_TIMESTAMP, event.timestamp);

            LocalBroadcastManager.getInstance(DataService.this).sendBroadcast(i);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onDestroy() {

        sensorManager.unregisterListener(hrListener);

        sensorManager = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent i, int id, int flags) {

        Log.d(LOG_TAG, "New intent ===========================================================");

        if (i != null && i.getAction() != null) {
            switch (i.getAction()) {
                case ACTION_START:
                    Log.d(LOG_TAG, "Start tracking command");
                    startForeground();
                    collectData();
                    return START_STICKY;
                case ACTION_STOP:
                    stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    private void startForeground() {
        Intent i = new Intent();
        i.setClassName("com.yourtrack.watch", "com.yourtrack.watch.TrackActivity");

        Notification n = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.tracking))
                .setSmallIcon(R.drawable.ic_heartrate_bw)
                .setContentIntent(PendingIntent.getActivity(this, 0, i, 0)).build();

        startForeground(0, n);
    }

    private void collectData() {
        Sensor hrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (hrSensor == null) {
            Log.w(LOG_TAG, "HR sensor is unavailable");
            return;
        }

        Log.i(LOG_TAG, "HR sensor: is wakeup: " + hrSensor.isWakeUpSensor() + ", reporting mode: " + hrSensor.getReportingMode());

        if (sensorManager.registerListener(hrListener, hrSensor, SensorManager.SENSOR_DELAY_UI)) {
            Log.i(LOG_TAG, "HR monitoring is started");
        }
        else {
            Log.i(LOG_TAG, "HR monitoring is failed");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public static void startTracking(Context context) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void stopTracking(Context context) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

}
