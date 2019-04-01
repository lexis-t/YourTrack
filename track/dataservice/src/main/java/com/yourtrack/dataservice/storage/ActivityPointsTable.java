package com.yourtrack.dataservice.storage;

import android.database.sqlite.SQLiteDatabase;

final class ActivityPointsTable extends SqlTableBase {

    static final String POINTS = "POINTS";

    static final String ID = "_id";
    static final String ACTIVITY = "ACTIVITY";
    static final String TIME = "TIME";
    static final String LATITUDE = "LATITUDE";
    static final String LONGITUDE = "LONGITUDE";
    static final String LOCATION_ACCURACY = "ACCURACY";
    static final String SEGMENT_END = "SEGMENT_END";
    static final String VELOCITY = "VELOCITY";
    static final String ELEVATION = "ELEVATION";
    static final String ELEVATION_ACCURACY = "ELEVATION_ACCURACY";
    static final String HR = "HR";
    static final String HR_ACCURACY = "HR_ACCURACY";

    private static final String CREATE = "create table " + POINTS + "(" +
            ID + " integer primary key, " +
            ACTIVITY + " integer not null, " +
            TIME + " integer not null, " +
            LATITUDE + " text, " +
            LONGITUDE + " text, " +
            LOCATION_ACCURACY + " text, " +
            SEGMENT_END + " integer default '0', " +
            VELOCITY + " text, " +
            ELEVATION + " text, " +
            ELEVATION_ACCURACY + " text, " +
            HR + " integer, " +
            HR_ACCURACY + " integer" +
            ");";




    @Override
    void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
