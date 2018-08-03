package com.example.matnguyen.elcom_trafficgo.searchRoutes.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.matnguyen.elcom_trafficgo.alarm.model.Alarm;
import com.example.matnguyen.elcom_trafficgo.alarm.util.AlarmUtils;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.utils.HistoryUtils;

import java.util.ArrayList;


public class DatabasehistoryHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "history.db";
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
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " VARCHAR, " +
                COL_LAT + " FLOAT NOT NULL, " +
                COL_LNG + " FLOAT NOT NULL " + ");";

        sqLiteDatabase.execSQL(CREATE_HISTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        throw new UnsupportedOperationException("This shouldn't happen yet!");
    }

    public float addHistory() {
        return addHistory(new Point());
    }

    float addHistory(Point point) {
        return getWritableDatabase().insert(TABLE_NAME, null, HistoryUtils.toContentValues(point));
    }
    public int updateHistory(Point point) {
        final String where = _ID + "=?";
        final String[] whereArgs = new String[] { Long.toString(point.getId()) };
        return getWritableDatabase()
                .update(TABLE_NAME, HistoryUtils.toContentValues(point), where, whereArgs);
    }
    public int deleteHistory(Point point) {
        return deleteHistory(point.getId());
    }

    int deleteHistory(long id) {
        final String where = _ID + "=?";
        final String[] whereArgs = new String[] { Long.toString(id) };
        return getWritableDatabase().delete(TABLE_NAME, where, whereArgs);
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
