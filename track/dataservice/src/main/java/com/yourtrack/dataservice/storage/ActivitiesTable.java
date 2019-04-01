package com.yourtrack.dataservice.storage;

import android.database.sqlite.SQLiteDatabase;

final class ActivitiesTable extends SqlTableBase {

    static final String ACTIVITIES = "ACTIVITIES";

    static final String ID = "_id";
    static final String TITLE = "TITLE";
    static final String TYPE = "TYPE";
    static final String STARTTIME = "START";

    private static final String CREATE = "create table " + ACTIVITIES +"(" +
            ID + " integer primary key, " +
            TITLE + " text not null, " +
            TYPE + " text not null, " +
            STARTTIME + " long not null" +
            ");";

    private static final String CREATE_STARTTIME_INDEX = "create index ACTIVITIES_STARTTIME_INDEX on " + ACTIVITIES + "(" +
            STARTTIME +
            ");";

    @Override
    void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE);
        db.execSQL(CREATE_STARTTIME_INDEX);
    }

    @Override
    void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
