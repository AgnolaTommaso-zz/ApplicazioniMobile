package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationsHolder {

    private static LocationsHolder sLocationsHolder;
    private List<Location> mLocations;

    public static LocationsHolder get(Context context) {
        if (sLocationsHolder == null)
            sLocationsHolder = new LocationsHolder(context);

        return sLocationsHolder;
    }

    private LocationsHolder(Context context) {
        mLocations = new ArrayList<>();
        Location loc=new Location();
        loc.setName("Loc. Corrente");
        mLocations.add(loc);
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    public Location getLocation(UUID id) {
        for (Location location : mLocations) {
            if (location.getId().equals(id))
                return location;
        }

        return null;
    }

    public void addLocation(Location location){
        mLocations.add(location);
    }

    public void updateLocation(int pos, Location newLocation){
        mLocations.get(pos).setName(newLocation.getName());
        mLocations.get(pos).setDescription(newLocation.getDescription());
        mLocations.get(pos).setHumity(newLocation.getHumity());
        mLocations.get(pos).setLatitude(newLocation.getLatitude());
        mLocations.get(pos).setLongitude(newLocation.getLongitude());
        mLocations.get(pos).setTemperature(newLocation.getTemperature());
    }
}
