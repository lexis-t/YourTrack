package com.yourtrack.dataservice.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yourtrack.dataservice.YTException;
import com.yourtrack.dataservice.activity.Point;

import java.util.Collection;

public final class Storage {
    static final int VERSION = 1;

    private SQLiteOpenHelper dbHelper;
    private SqlTableBase tables[] = new SqlTableBase[] {new ActivityTypesTable(), new ActivitiesTable(), new ActivityPointsTable()};

    public Storage(Context context) {
        this.dbHelper = new SQLiteOpenHelper(context, "EnduranceDB", null, VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                for (SqlTableBase t: tables) {
                    t.createTable(db);
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                for (SqlTableBase t: tables) {
                    t.onUpgradeDb(db, oldVersion, newVersion);
                }
            }
        };
    }

    public long createActivity(String title, String type, long startTime) throws YTException {
        try {
            ContentValues v = new ContentValues(3);
            v.put(ActivitiesTable.TITLE, title);
            v.put(ActivitiesTable.TYPE, type);
            v.put(ActivitiesTable.STARTTIME, startTime);
            return dbHelper.getWritableDatabase().insertOrThrow(ActivitiesTable.ACTIVITIES, null, v);
        }
        catch (SQLException e){
            throw new YTException("Failed to create new activity", e);
        }
    }

    public void addPointsToActivity(long activityId, Collection<Point> points) throws YTException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();

            ContentValues v = new ContentValues();

            for (Point p: points) {
                v.put(ActivityPointsTable.ACTIVITY, activityId);
                v.put(ActivityPointsTable.TIME, p.getTime());
                if (p.hasLocation()) {
                    v.put(ActivityPointsTable.LATITUDE, p.getLatitude());
                    v.put(ActivityPointsTable.LONGITUDE, p.getLongitude());
                    v.put(ActivityPointsTable.LOCATION_ACCURACY, p.getAccuracy());
                }
                if (p.hasAltitude()) {
                    v.put(ActivityPointsTable.ELEVATION, p.getAltitude());
                    v.put(ActivityPointsTable.ELEVATION_ACCURACY, p.getAltitudeAccuracy());
                }
                if (p.hasHeartRate()) {
                    v.put(ActivityPointsTable.HR, p.getHeartRate());
                    v.put(ActivityPointsTable.HR_ACCURACY, p.getHeartRateAccuracy());
                }
                db.insertOrThrow(ActivityPointsTable.POINTS, null, v);
                v.clear();
            }

            db.setTransactionSuccessful();;
        }
        catch (SQLException e) {
            throw new YTException("Failed to save activity track", e);
        }
        finally {
            db.endTransaction();
        }
    }
}
