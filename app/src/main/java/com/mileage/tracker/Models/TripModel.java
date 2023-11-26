package com.mileage.tracker.Models;

public class TripModel {
    String tripName;
    String tripStartLocationName;
    String tripEndLocationName;
    String tripStartLat;
    String tripStartLng;
    String tripStartDateAndTime;
    String tripEndLat;
    String tripEndLng;
    String tripEndDateAndTime;
    String tripTotalMiles;


    public String getTripTotalMiles() {
        return tripTotalMiles;
    }

    public void setTripTotalMiles(String tripTotalMiles) {
        this.tripTotalMiles = tripTotalMiles;
    }

    public String getTripStartLocationName() {
        return tripStartLocationName;
    }

    public void setTripStartLocationName(String tripStartLocationName) {
        this.tripStartLocationName = tripStartLocationName;
    }

    public String getTripEndLocationName() {
        return tripEndLocationName;
    }

    public void setTripEndLocationName(String tripEndLocationName) {
        this.tripEndLocationName = tripEndLocationName;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripStartLat() {
        return tripStartLat;
    }

    public void setTripStartLat(String tripStartLat) {
        this.tripStartLat = tripStartLat;
    }

    public String getTripStartLng() {
        return tripStartLng;
    }

    public void setTripStartLng(String tripStartLng) {
        this.tripStartLng = tripStartLng;
    }

    public String getTripStartDateAndTime() {
        return tripStartDateAndTime;
    }

    public void setTripStartDateAndTime(String tripStartDateAndTime) {
        this.tripStartDateAndTime = tripStartDateAndTime;
    }

    public String getTripEndLat() {
        return tripEndLat;
    }

    public void setTripEndLat(String tripEndLat) {
        this.tripEndLat = tripEndLat;
    }

    public String getTripEndLng() {
        return tripEndLng;
    }

    public void setTripEndLng(String tripEndLng) {
        this.tripEndLng = tripEndLng;
    }

    public String getTripEndDateAndTime() {
        return tripEndDateAndTime;
    }

    public void setTripEndDateAndTime(String tripEndDateAndTime) {
        this.tripEndDateAndTime = tripEndDateAndTime;
    }
}
