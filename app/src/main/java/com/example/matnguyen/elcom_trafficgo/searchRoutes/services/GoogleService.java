package com.example.matnguyen.elcom_trafficgo.searchRoutes.services;


import android.os.AsyncTask;
import android.util.Log;


import com.example.matnguyen.elcom_trafficgo.MapsActivity;
import com.google.android.gms.maps.model.LatLng;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GoogleService {
    private static final String TAG = "GOOGLE SERVICE";
    //public String activity;

    public static String requestDirection(String urlReq) throws IOException {
        String responeString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlReq);
            //Log.e(TAG, "URL REQUEST: " + urlReq);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            responeString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responeString;
    }

    public static String getRequestUrl(LatLng origin, LatLng des, String vehicle){
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_des = "destination=" + des.latitude + "," + des.longitude;
        String sensor = "sensor=false";
        String mode = "mode=" + vehicle;
        String param = str_org + "&" + str_des + "&" + mode;
        String output = "json";
        String add = "&units=metric&alternatives=true";
        //String key = "AIzaSyAxq6ocfyyURFVv-EDcJx1b2N8v1m_N0vI";
        String url = "https://maps.googleapis.com/maps/api/directions/"+ output + "?" + param + add;

        Log.e(TAG, url);

        return url;

    }

    public static class TaskRequestDirection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responeString = "";
            try {
                //Log.e(TAG, "String 0: " + strings[0]);
                responeString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responeString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TaskParser taskParser = new TaskParser();
            MapsActivity.TaskParser taskParser = new MapsActivity.TaskParser();
            Log.e(TAG, "go to on post excute");
            taskParser.execute(s);
        }
    }


}
