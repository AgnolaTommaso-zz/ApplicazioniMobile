package ch.supsi.dti.isin.meteoapp.utility;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;
import ch.supsi.dti.isin.meteoapp.model.Location;


public class APIParser {
    private static String APIKEY="cc3c629069bce6858e29dedf0f73213a";

    public static JsonObjectRequest getLocationInfo(final double latitude, final double longitude, final VolleyCallback callback){
        String url="https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&units=metric&appid="+APIKEY;

        JsonObjectRequest jor=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("Meteo onResponse API", response.toString());
                    JSONObject mainObject=response.getJSONObject("main");
                    //JSONObject weatherObject=response.getJSONObject("weather");

                    callback.onSuccess(new Location(response.getString("name"),
                            String.valueOf(latitude),
                            String.valueOf(longitude),
                            String.valueOf(mainObject.getDouble("temp")),
                            String.valueOf(mainObject.getDouble("humidity")),
                            response.getJSONArray("weather").getJSONObject(0).getString("description")
                            ));

                }catch (JSONException e){
                    Log.i("Meteo onResponse API", e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Meteo API onError", error.toString());

            }
        });



        return jor;
    }

    public static JsonObjectRequest getLocationInfo(String name, final VolleyCallback callback) {
        String url="https://api.openweathermap.org/data/2.5/weather?q="+name+"&units=metric&appid="+APIKEY;
        JsonObjectRequest jor=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("Meteo onResponse Name", response.toString());
                    JSONObject mainObject=response.getJSONObject("main");
                    JSONObject coordObject=response.getJSONObject("coord");
                    //JSONObject weatherObject=response.getJSONObject("weather");

                    callback.onSuccess(new Location(response.getString("name"),
                            String.valueOf(coordObject.getDouble("lat")),
                            String.valueOf(coordObject.getDouble("lon")),
                            String.valueOf(mainObject.getDouble("temp")),
                            String.valueOf(mainObject.getDouble("humidity")),
                            response.getJSONArray("weather").getJSONObject(0).getString("description")
                    ));

                }catch (JSONException e){
                    Log.i("Meteo onResponse API", e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Meteo API onError", error.toString());

            }
        });

        return jor;


    }
}
