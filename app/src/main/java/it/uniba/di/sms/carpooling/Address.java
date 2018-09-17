package it.uniba.di.sms.carpooling;

/**
 * Created by tommaso on 15/06/2018.
 */

public class Address {
    String googleAddress;
    double latitude;
    double longitude;

    public Address(String googleAddress, double latitude, double longitude) {
        this.googleAddress = googleAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Address(){

    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
