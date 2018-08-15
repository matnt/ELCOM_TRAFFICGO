package com.example.matnguyen.elcom_trafficgo.searchRoutes.parsers;

import android.util.Log;

import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Route;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Step;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsParser {

    public List<Route> parseListRoute(JSONObject jsonObject){
        ArrayList<Route> arrListRoute = new ArrayList<>();
        JSONArray jRoutes = null;
        JSONArray jSteps = null;
        JSONArray jLegs = null;
        try {
            jRoutes = jsonObject.getJSONArray("routes");

            // Loop for all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                //Loop for all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    List<Step> steps = new ArrayList<>();
                    Log.e("DIRECTION PARSER", "STEP LENGTH: " + jSteps.length());

                    //Loop for all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String info = (String) ( ((JSONObject) jSteps.get(k)).get("html_instructions"));
                        // step1.add(new Step(info));
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyline(polyline);

                        //Loop for all points
                        for (int l = 0; l < list.size(); l++) {
                            // lat lng of 1 point
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
                            //hm.put("")
                            steps.add(new Step(hm, null, null, info, null));
                        }
                    }
                    Route route = new Route(null, null, null, null, steps);
                    arrListRoute.add(route);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrListRoute;

    }

    /**
     * Method to decode polyline
     * Source : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List decodePolyline(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


//    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
//
//        List<List<HashMap<String, String>>> routes = new ArrayList<>();
//        JSONArray jRoutes = null;
//        JSONArray jLegs = null;
//        JSONArray jSteps = null;
//
//        try {
//
//            jRoutes = jObject.getJSONArray("routes");
//
//            // Loop for all routes
//            for (int i = 0; i < jRoutes.length(); i++) {
//                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
//                List path = new ArrayList<HashMap<String, String>>();
//
//                //Loop for all legs
//                for (int j = 0; j < jLegs.length(); j++) {
//                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
//
//                    //Loop for all steps
//                    for (int k = 0; k < jSteps.length(); k++) {
//                        String polyline = "";
//                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
//                        List list = decodePolyline(polyline);
//                        Log.e("DIRECTION PARSER", list.size() + " ");
//
//                        //Loop for all points
//                        for (int l = 0; l < list.size(); l++) {
//                            HashMap<String, String> hm = new HashMap<>();
//                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
//                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
//                            path.add(hm);
//                        }
//                    }
//                    routes.add(path);
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//
//        }
//
//        return routes;
//    }


//    public HashMap<String, List<String>> parseStep(JSONObject jsonObject){
//        HashMap<String, List<String>> hashMap = new HashMap<>();
//        ArrayList<String> arr;
//        JSONArray jRoutes = null;
//        JSONArray jSteps = null;
//        JSONArray jLegs = null;
//        try {
//            jRoutes = jsonObject.getJSONArray("routes");
//            for(int i = 0; i < jRoutes.length(); i++){
//                arr = new ArrayList<>();
//                jLegs = ( (JSONObject) jRoutes.get(i)).getJSONArray("legs");
//                for(int j = 0; j < jLegs.length(); j++){
//                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
//                    for(int k = 0; k < jSteps.length(); k++){
//                        String info = (String) ((JSONObject) jSteps.get(k)).get("html_instructions");
//                        arr.add(info);
//
//                    }
//                }
//                //hashMap.put( , arr);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return hashMap;
//    }

}