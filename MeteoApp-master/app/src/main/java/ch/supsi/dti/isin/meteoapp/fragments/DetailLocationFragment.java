package ch.supsi.dti.isin.meteoapp.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.activities.MainActivity;
import ch.supsi.dti.isin.meteoapp.database.DbHelper;
import ch.supsi.dti.isin.meteoapp.database.DbSchema;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.utility.APIParser;
import ch.supsi.dti.isin.meteoapp.utility.VolleyCallback;

import static ch.supsi.dti.isin.meteoapp.activities.DetailActivity.newIntent;

public class DetailLocationFragment extends Fragment implements VolleyCallback {
    private static final String ARG_LOCATION_ID = "location_id";

    private Location mLocation;
    private TextView mIdTextView;
    private ImageView mIdimageView;

    private TextView citytv, temperaturetv, humiditytv, descriptiontv;


    public static DetailLocationFragment newInstance(UUID locationId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION_ID, locationId);

        DetailLocationFragment fragment = new DetailLocationFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID locationId = (UUID) getArguments().getSerializable(ARG_LOCATION_ID);
        mLocation = LocationsHolder.get(getActivity()).getLocation(locationId);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        JsonObjectRequest jor = APIParser.getLocationInfo(mLocation.getName(), DetailLocationFragment.this);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jor);

        View v = inflater.inflate(R.layout.fragment_detail_location, container, false);

        mIdTextView = v.findViewById(R.id.id_textView);
        //mIdTextView.setText(mLocation.getId().toString());
        mIdimageView = v.findViewById(R.id.weatherIcon);
        citytv=v.findViewById(R.id.citytv);
        temperaturetv=v.findViewById(R.id.temperaturetv);
        humiditytv=v.findViewById(R.id.humiditytv);
        descriptiontv=v.findViewById(R.id.descriptiontv);

        Location currentLocation = LocationsHolder.get(getActivity()).getLocation(mLocation.getId());


        mIdimageView.setImageDrawable(getDrawableById(currentLocation.getWeatherid()));

        citytv.setText(currentLocation.getName());
        temperaturetv.setText(currentLocation.getTemperature());
        humiditytv.setText(currentLocation.getHumity());
        descriptiontv.setText(currentLocation.getDescription());

        return v;
    }

    private Drawable getDrawableById(int id){
        Log.i("Meteo ID", ""+ id);
        Drawable d;
        switch (id-(id%100)){
            case 200://thunderstorm
                d=getResources().getDrawable(R.drawable.thunderstorm);
                break;
            case 300://drizzle
                d= getResources().getDrawable(R.drawable.snowerrain);
                break;
            case 500://rain
                d= getResources().getDrawable(R.drawable.rain);
                break;
            case 600://snow
                d= getResources().getDrawable(R.drawable.snow);
                break;
            case 700://mist
                d= getResources().getDrawable(R.drawable.mist);
                break;
            default://clear and clouds
                if(id==800) {//clear{
                    d= getResources().getDrawable(R.drawable.clearsky);
                }
                else {//clouds
                    d= getResources().getDrawable(R.drawable.scatteredclouds);
                }
                break;
        }
        return d;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_detail_location, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ContentValues values = new ContentValues();

        switch (item.getItemId()) {
            case R.id.menu_remove:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.remove) + "?");
                builder.setPositiveButton(getString(R.string.okDialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocationsHolder.get(getActivity()).getLocation(mLocation.getId());
                        SQLiteDatabase mDatabase = new DbHelper(getContext()).getWritableDatabase();

                        String selection = DbSchema.LocationsTable.Cols.NAME + " LIKE ?";
                        String[] selectionArgs = {mLocation.getName()};
                        int deletedRows = mDatabase.delete(DbSchema.LocationsTable.TABLE_NAME, selection, selectionArgs);
                        Log.i("Deleted ", "nr: " +deletedRows);
                        LocationsHolder.get(getContext()).getLocations().remove(mLocation);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(getString(R.string.cancelDialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(Location location) {
        int index = LocationsHolder.get(getContext()).getLocations().indexOf(mLocation);
        LocationsHolder.get(getActivity()).updateLocation(index, location);
        LocationsHolder.get(getActivity()).getLocations().get(index).setName(location.getName());
        Location currentLocation = LocationsHolder.get(getActivity()).getLocation(mLocation.getId());

        citytv.setText(currentLocation.getName());
        temperaturetv.setText(currentLocation.getTemperature());
        humiditytv.setText(currentLocation.getHumity());
        descriptiontv.setText(currentLocation.getDescription());
    }
}

