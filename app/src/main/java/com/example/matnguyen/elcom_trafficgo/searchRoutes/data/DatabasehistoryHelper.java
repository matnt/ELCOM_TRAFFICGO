package com.example.matnguyen.elcom_trafficgo.searchRoutes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.matnguyen.elcom_trafficgo.alarm.model.Alarm;
import com.example.matnguyen.elcom_trafficgo.alarm.util.AlarmUtils;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.utils.HistoryUtils;

import java.util.ArrayList;


public class DatabasehistoryHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "history.db";
    private static final int SCHEMA = 1;

    private static final String TABLE_NAME = "history";
    public static final String _ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_LAT = "lat";
    public static final String COL_LNG = "lng";
    private static DatabasehistoryHelper sInstance = null;

    public static synchronized DatabasehistoryHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabasehistoryHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public DatabasehistoryHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " TEXT PRIMARY KEY , " +
                COL_NAME + " VARCHAR, " +
                COL_LAT + " FLOAT NOT NULL, " +
                COL_LNG + " FLOAT NOT NULL " + ");";

        sqLiteDatabase.execSQL(CREATE_HISTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        throw new UnsupportedOperationException("This shouldn't happen yet!");
    }


    public void addNewPoint(Point point){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = HistoryUtils.toContentValues(point);
        //Log.e("DATABSE 2", point.getId());
        db.insert(TABLE_NAME, null, content);
        db.close();
    }
    public int updateHistory(Point point) {
        Log.e("DATABASE", point.getId());
        final String where = _ID + "=?";
        final String[] whereArgs = new String[] { point.getId() };
        return getWritableDatabase()
                .update(TABLE_NAME, HistoryUtils.toContentValues(point), where, whereArgs);

    }
    public int deleteHistory(Point point) {
        return deleteHistory(point.getId());
    }

    int deleteHistory(String id) {
       // final String where = _ID + "=?";
        final String[] whereArgs = new String[] { id};
        //return getWritableDatabase().delete(TABLE_NAME, where, whereArgs);
        return getWritableDatabase().delete(TABLE_NAME, null, whereArgs);
    }

    public ArrayList<Point> getPoints() {

        Cursor c = null;

        try{
            c = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
            return HistoryUtils.buildHistoryList(c);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }

    }


}
