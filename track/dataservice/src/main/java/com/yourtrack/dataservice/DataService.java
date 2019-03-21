package com.yourtrack.dataservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DataService extends Service {
    private final static String LOG_TAG = "YourTrack";

    private static final int REQUEST_ID_ACTIVITY_TRANSITION = 2;

    private final static String ACTION_START = "ACTION_START";
    private final static String ACTION_STOP = "ACTION_STOP";
    private final static String ACTION_ACTIVITY_TRANSITION = "ACTION_ACTIVITY_TRANSITION";

    public final static String ACTION_DATA = "ACTION_DATA";
    public final static String EXTRA_SENSOR_TYPE = "sensor";
    public final static String EXTRA_VALUE = "value";
    public final static String EXTRA_ACCURACY = "accuracy";
    public final static String EXTRA_TIMESTAMP = "timestamp";
    public final static String EXTRA_ACTIVITY = "activity";

    public final static String EXTRA_SENSOR_HR = "heartrate";
    public final static String EXTRA_ACTIVITY_UNKNOWN = "unknown";
    public final static String EXTRA_ACTIVITY_WALK = "walk";
    public final static String EXTRA_ACTIVITY_RUN = "run";
    public final static String EXTRA_ACTIVITY_BICYCLE = "bicycle";
    public final static String EXTRA_ACTIVITY_DRIVE = "drive";
    public final static String EXTRA_ACTIVITY_SLEEP = "sleep";

    private final static List<ActivityTransition> ACTIVITY_TRANSITIONS = new ArrayList<>();

    static {
        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        ACTIVITY_TRANSITIONS.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

    }

    private boolean isStarted = false;
    private PendingIntent activityTransitionsPendingIntent = null;

    private float lastHrValue = 0.0f;
    private int lastHrAccuracy = -1;
    private long lastHrTimestamp = -1L;

    private String lastActivityType = EXTRA_ACTIVITY_UNKNOWN;
    private long lastActivityTimestamp = -1L;

    private SensorManager sensorManager;
    private SensorEventListener hrListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.i(LOG_TAG, "Heart rate: " + event.values[0] + " , accuracy: " + event.accuracy);

            lastHrValue = event.values[0];
            lastHrAccuracy = event.accuracy;
            lastHrTimestamp = event.timestamp;

            Intent i = new Intent(ACTION_DATA);
            i.putExtra(EXTRA_SENSOR_TYPE, EXTRA_SENSOR_HR);
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

        stop();
        isStarted = false;

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
                    startCollectData();
                    return START_STICKY;
                case ACTION_ACTIVITY_TRANSITION:
                    Log.d(LOG_TAG, "Activity transition");
                    if (isStarted && ActivityTransitionResult.hasResult(i)) {
                        processActivityTransition(ActivityTransitionResult.extractResult(i));
                    }
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
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_heartrate_bw))
                .setContentIntent(PendingIntent.getActivity(this, 0, i, 0)).build();

        startForeground(0, n);
        isStarted = true;
    }

    private void startCollectData() {
        Sensor hrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (hrSensor == null) {
            Log.w(LOG_TAG, "HR sensor is unavailable");
        }
        else {

            Log.i(LOG_TAG, "HR sensor: is wakeup: " + hrSensor.isWakeUpSensor() + ", reporting mode: " + hrSensor.getReportingMode());

            if (sensorManager.registerListener(hrListener, hrSensor, SensorManager.SENSOR_DELAY_UI)) {
                Log.i(LOG_TAG, "HR monitoring is started");
            } else {
                Log.i(LOG_TAG, "HR monitoring is failed");
            }
        }

        Intent i = new Intent(this, DataService.class);
        i.setAction(ACTION_ACTIVITY_TRANSITION);
        PendingIntent pi = PendingIntent.getService(this, REQUEST_ID_ACTIVITY_TRANSITION, i, 0);

        Task<Void> registerTask = ActivityRecognition.getClient(this).requestActivityTransitionUpdates(new ActivityTransitionRequest(ACTIVITY_TRANSITIONS), pi);
        registerTask.addOnFailureListener(r -> Log.w(LOG_TAG, "Failed to request activity transitions"));
        registerTask.addOnSuccessListener(r -> activityTransitionsPendingIntent = pi);

        if (lastHrTimestamp != -1L) {
            Intent intent = new Intent(ACTION_DATA);
            intent.putExtra(EXTRA_SENSOR_TYPE, EXTRA_SENSOR_HR);
            intent.putExtra(EXTRA_VALUE, lastHrValue);
            intent.putExtra(EXTRA_ACCURACY, lastHrAccuracy);
            intent.putExtra(EXTRA_TIMESTAMP, lastHrTimestamp);

            LocalBroadcastManager.getInstance(DataService.this).sendBroadcast(intent);
        }

        if (lastActivityTimestamp != -1) {
            Intent intent = new Intent(ACTION_DATA);
            intent.putExtra(EXTRA_ACTIVITY, lastActivityType);
            intent.putExtra(EXTRA_TIMESTAMP, lastActivityTimestamp);
        }

    }

    private void processActivityTransition(ActivityTransitionResult r) {

        if (r != null) {
            for (ActivityTransitionEvent transition : r.getTransitionEvents()) {
                if (transition.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    Log.i(LOG_TAG, "User started new activity: " + transition.getActivityType());

                    switch (transition.getActivityType()) {
                        case DetectedActivity.WALKING:
                            lastActivityType = EXTRA_ACTIVITY_WALK;
                            break;
                        case DetectedActivity.RUNNING:
                            lastActivityType = EXTRA_ACTIVITY_RUN;
                            break;
                        case DetectedActivity.ON_BICYCLE:
                            lastActivityType = EXTRA_ACTIVITY_BICYCLE;
                            break;
                        case DetectedActivity.IN_VEHICLE:
                            lastActivityType = EXTRA_ACTIVITY_DRIVE;
                            break;
                        default:
                            lastActivityType = EXTRA_ACTIVITY_UNKNOWN;
                            break;

                    }
                } else {
                    Log.i(LOG_TAG, "User stopped activity: " + transition.getActivityType());
                    lastActivityType = EXTRA_ACTIVITY_UNKNOWN;
                }
            }

            lastActivityTimestamp = System.currentTimeMillis() - (r.getTransitionEvents().get(r.getTransitionEvents().size() - 1).getElapsedRealTimeNanos() / 1000);

            Intent i = new Intent(ACTION_DATA);
            i.putExtra(EXTRA_ACTIVITY, lastActivityType);
            i.putExtra(EXTRA_TIMESTAMP, lastActivityTimestamp);

            LocalBroadcastManager.getInstance(DataService.this).sendBroadcast(i);
        }
    }

    private void stop() {
        if (activityTransitionsPendingIntent != null) {
            ActivityRecognition.getClient(this).removeActivityTransitionUpdates(activityTransitionsPendingIntent);
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
