package com.example.matnguyen.elcom_trafficgo.searchRoutes.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.matnguyen.elcom_trafficgo.alarm.data.DatabaseHelper._ID;
import static com.example.matnguyen.elcom_trafficgo.searchRoutes.data.DatabasehistoryHelper.COL_LAT;
import static com.example.matnguyen.elcom_trafficgo.searchRoutes.data.DatabasehistoryHelper.COL_LNG;
import static com.example.matnguyen.elcom_trafficgo.searchRoutes.data.DatabasehistoryHelper.COL_NAME;

public final class HistoryUtils {

    public static ContentValues toContentValues(Point point) {
        final ContentValues cv = new ContentValues(10);
        cv.put(COL_NAME, point.getName());
        cv.put(COL_LAT, point.getLat());
        cv.put(COL_LNG, point.getLng());

        return cv;
    }

    public static ArrayList<Point> buildHistoryList(Cursor c) {

        if (c == null) return new ArrayList<>();

        final int size = c.getCount();

        final ArrayList<Point> points = new ArrayList<>(size);

        if (c.moveToFirst()){
            do {

                final long id = c.getLong(c.getColumnIndex(_ID));
                final String name = c.getString(c.getColumnIndex(COL_NAME));
                final float lat = c.getFloat(c.getColumnIndex(COL_LAT));
                final float lng = c.getFloat(c.getColumnIndex(COL_LNG));

                final Point point = new Point(id, name, lat, lng);
                //final Point point = new Point(id, lat, lng);
                points.add(point);

            } while (c.moveToNext());
        }

        return points;

    }
}
