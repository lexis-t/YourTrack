package com.yourtrack.dataservice.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

final class ActivityTypesTable extends SqlTableBase {

    static final String ACTIVITY_TYPES = "ACTIVITY_TYPES";

    //static final String ID = "_id";
    static final String NAME = "NAME";
    static final String DOMAIN = "DOMAIN";

    private static final String CREATE = "create table "+ ACTIVITY_TYPES + "(" +
            NAME + " text primary key, " +
            DOMAIN + " text not null" +
            ");";

    private static final String INSERT_NEW = "insert into " + ACTIVITY_TYPES + "(" +
            NAME + ", " + DOMAIN + ") values (?,?);";


    @Override
    void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE);

        SQLiteStatement insertStatement = db.compileStatement(INSERT_NEW);

        insertStatement.bindAllArgsAsStrings(new String[]{"walking", ActivityDomains.STEP});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"running", ActivityDomains.STEP});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"trailrunning", ActivityDomains.STEP});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"skiing", ActivityDomains.STEP});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"skitouring", ActivityDomains.STEP});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"pool swiming", ActivityDomains.SWIM});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"open water swiming", ActivityDomains.SWIM});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"road cycling", ActivityDomains.CYCLE});
        insertStatement.executeInsert();

        insertStatement.bindAllArgsAsStrings(new String[]{"sport cycling", ActivityDomains.CYCLE});
        insertStatement.executeInsert();

    }

    @Override
    void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
