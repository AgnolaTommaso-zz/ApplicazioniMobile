package ch.supsi.dti.isin.meteoapp.model;

import java.util.UUID;

public class Location {
    private UUID Id;
    private String mName;
    private String latitude;
    private String longitude;
    private String temperature;
    private String humity;
    private String description;

    public Location(String mName, String latitude, String longitude, String temperature, String humity, String description) {
        this.mName = mName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.humity = humity;
        this.description = description;
    }

    public Location(String mName){
        this.mName = mName;
        Id = UUID.randomUUID();
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Location() {
        Id = UUID.randomUUID();
    }


    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumity() {
        return humity;
    }

    public String getDescription() {
        return description;
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setHumity(String humity) {
        this.humity = humity;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}