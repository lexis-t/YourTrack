package com.yourtrack.dataservice.storage;

import android.database.sqlite.SQLiteDatabase;

abstract class SqlTableBase {
    abstract void createTable(SQLiteDatabase db);
    abstract void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion);
}
