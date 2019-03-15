package ch.supsi.dti.isin.meteoapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.utility.APIParser;
import ch.supsi.dti.isin.meteoapp.utility.VolleyCallback;

public class DetailLocationFragment extends Fragment implements VolleyCallback{
    private static final String ARG_LOCATION_ID = "location_id";

    private Location mLocation;
    private TextView mIdTextView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        JsonObjectRequest jor = APIParser.getLocationInfo(mLocation.getName(), DetailLocationFragment.this);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jor);

        View v = inflater.inflate(R.layout.fragment_detail_location, container, false);

        mIdTextView = v.findViewById(R.id.id_textView);
        //mIdTextView.setText(mLocation.getId().toString());

        Location currentLocation=LocationsHolder.get(getActivity()).getLocation(mLocation.getId());

        mIdTextView.setText(getString(R.string.app_name)+": "+currentLocation.getName()+
                "\n"+ getString(R.string.temperature)+": "+currentLocation.getTemperature()
                +"\n"+ getString(R.string.humidity)+": "+currentLocation.getHumity()
                +"\n"+getString(R.string.description)+": "+currentLocation.getDescription());
        return v;
    }

    @Override
    public void onSuccess(Location location) {
        int index = LocationsHolder.get(getContext()).getLocations().indexOf(mLocation);
        LocationsHolder.get(getActivity()).updateLocation(index, location);
        LocationsHolder.get(getActivity()).getLocations().get(index).setName(location.getName());

    }
}

