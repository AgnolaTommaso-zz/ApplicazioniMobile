package ch.supsi.dti.isin.meteoapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.database.DbHelper;
import ch.supsi.dti.isin.meteoapp.database.DbSchema;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.utility.APIParser;
import ch.supsi.dti.isin.meteoapp.utility.VolleyCallback;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class ListFragment extends Fragment implements VolleyCallback {
    private RecyclerView mLocationRecyclerView;
    private LocationAdapter mAdapter;
    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("perm", "Permission not granted");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        } else {
            Log.i("perm", "Permission granted"); // leggo la posizione del device
            mDatabase = new DbHelper(getContext()).getWritableDatabase();
            startLocationListener();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mLocationRecyclerView = view.findViewById(R.id.recycler_view);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Location> locations = LocationsHolder.get(getActivity()).getLocations();
        mAdapter = new LocationAdapter(locations);
        mLocationRecyclerView.setAdapter(mAdapter);

        return view;
    }

    // Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ContentValues values = new ContentValues();

        switch (item.getItemId()) {
            case R.id.menu_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.addLocation)); // TODO: Aggiungere stringa in strings.xml
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton(getString(R.string.okDialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Location location = new Location();
                        location.setName(input.getText().toString());
                        LocationsHolder.get(getContext()).addLocation(location);

                        values.put(DbSchema.LocationsTable.Cols.NAME, input.getText().toString());
                        mDatabase.insert(DbSchema.LocationsTable.TABLE_NAME, null, values); //TODO crash al primo inserimento di una città
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

    // Holder

    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameTextView;
        private Location mLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DetailActivity.newIntent(getActivity(), mLocation.getId());
            startActivity(intent);
        }

        public void bind(Location location) {
            mLocation = location;
            mNameTextView.setText(mLocation.getName());
        }
    }

    // Adapter

    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Location> mLocations;

        public LocationAdapter(List<Location> locations) {
            mLocations = locations;
        }

        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            Location location = mLocations.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount() {
            return mLocations.size();
        }
    }


    private void startLocationListener() {
        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(0)
                .setInterval(5000);
        SmartLocation.with(getActivity()).location().continuous().config(builder.build())//getActivity per recuperare l'activity
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        Log.i("Meteo onLocationUpdated", location.toString());
                        JsonObjectRequest jor = APIParser.getLocationInfo(location.getLatitude(), location.getLongitude(), ListFragment.this);
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        queue.add(jor);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onSuccess(Location location) {
        Log.i("Meteo onSuccess", location.toString());
        LocationsHolder.get(getActivity()).updateLocation(0, location);
        LocationsHolder.get(getActivity()).getLocations().get(0).setName(location.getName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationListener();
                }
                return;
            }
        }
    }

}
