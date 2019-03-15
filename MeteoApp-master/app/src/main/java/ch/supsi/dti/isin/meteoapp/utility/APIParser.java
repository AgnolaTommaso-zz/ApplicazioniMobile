package ch.supsi.dti.isin.meteoapp.utility;

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

import ch.supsi.dti.isin.meteoapp.model.Location;


public class APIParser {
    private static String APIKEY="17909991fbcd2169ff7202ad0651c695";

    public static JsonObjectRequest getLocationInfo(final double latitude, final double longitude, final VolleyCallback callback){
        String url="http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&units=metric&appid="+APIKEY;

        JsonObjectRequest jor=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
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
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error

            }
        });



        return jor;
    }
}
