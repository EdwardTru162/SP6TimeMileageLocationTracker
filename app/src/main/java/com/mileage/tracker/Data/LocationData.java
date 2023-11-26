package com.mileage.tracker.Data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.mileage.tracker.Models.TripModel;

public class LocationData {
Context context;
    public LocationData(Context context) {
        this.context = context;
    }
    public void setTrip(Boolean tripStarted) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("userData",MODE_PRIVATE);
        SharedPreferences.Editor myEdit =sharedPreferences.edit();
        myEdit.putBoolean("tripStarted",tripStarted);
        myEdit.apply();
    }
    public boolean getTrip(){
        SharedPreferences sh=context.getSharedPreferences("userData",MODE_PRIVATE);
        if(!sh.contains("tripStarted")){
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putBoolean("tripStarted", false);
            myEdit.apply();
        }
      return   sh.getBoolean("tripStarted",false);
    }

    public void setTripModel(TripModel tm) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("userData",MODE_PRIVATE);
        SharedPreferences.Editor myEdit =sharedPreferences.edit();

        myEdit.putString("tripName",tm.getTripName());
        myEdit.putString("tripStartLocationName",tm.getTripStartLocationName());
        myEdit.putString("tripEndLocationName",tm.getTripEndLocationName());
        myEdit.putString("tripStartLat",tm.getTripStartLat());
        myEdit.putString("tripStartLng",tm.getTripStartLng());
        myEdit.putString("tripStartDateAndTime",tm.getTripStartDateAndTime());
        myEdit.putString("tripEndLat",tm.getTripEndLat());
        myEdit.putString("tripEndLng",tm.getTripEndLng());
        myEdit.putString("tripEndDateAndTime",tm.getTripEndDateAndTime());
        myEdit.putString("tripTotalMiles",tm.getTripTotalMiles());
        myEdit.apply();
    }
    public TripModel getTripModel(){
        SharedPreferences sh=context.getSharedPreferences("userData",MODE_PRIVATE);
        if(!sh.contains("tripName")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripName", "");myEdit.apply();}
        if(!sh.contains("tripStartLocationName")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripStartLocationName", "");myEdit.apply();}
        if(!sh.contains("tripEndLocationName")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripEndLocationName", "");myEdit.apply();}
        if(!sh.contains("tripStartLat")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripStartLat", "");myEdit.apply();}
        if(!sh.contains("tripStartLng")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripStartLng", "");myEdit.apply();}
        if(!sh.contains("tripStartDateAndTime")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripStartDateAndTime", "");myEdit.apply();}
        if(!sh.contains("tripEndLat")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripEndLat", "");myEdit.apply();}
        if(!sh.contains("tripEndLng")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripEndLng", "");myEdit.apply();}
        if(!sh.contains("tripEndDateAndTime")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripEndDateAndTime", "");myEdit.apply();}
        if(!sh.contains("tripTotalMiles")){SharedPreferences.Editor myEdit = sh.edit();myEdit.putString("tripTotalMiles", "");myEdit.apply();}
        TripModel tm=new TripModel();
        tm.setTripName(sh.getString("tripName",""));
        tm.setTripStartLocationName(sh.getString("tripStartLocationName",""));
        tm.setTripEndLocationName(sh.getString("tripEndLocationName",""));
        tm.setTripStartLat(sh.getString("tripStartLat",""));
        tm.setTripStartLng(sh.getString("tripStartLng",""));
        tm.setTripStartDateAndTime(sh.getString("tripStartDateAndTime",""));
        tm.setTripEndLat(sh.getString("tripEndLat",""));
        tm.setTripEndLng(sh.getString("tripEndLng",""));
        tm.setTripEndDateAndTime(sh.getString("tripEndDateAndTime",""));
        tm.setTripTotalMiles(sh.getString("tripTotalMiles",""));
        return  tm;
    }
    public void clearTrip() {
        setTrip(false);
        SharedPreferences sharedPreferences=context.getSharedPreferences("userData",MODE_PRIVATE);
        SharedPreferences.Editor myEdit =sharedPreferences.edit();
        myEdit.putString("tripName","");
        myEdit.putString("tripStartLocationName","");
        myEdit.putString("tripEndLocationName","");
        myEdit.putString("tripStartLat","");
        myEdit.putString("tripStartLng","");
        myEdit.putString("tripStartDateAndTime","");
        myEdit.putString("tripEndLat","");
        myEdit.putString("tripEndLng","");
        myEdit.putString("tripEndDateAndTime","");
        myEdit.putString("tripTotalMiles","");
        myEdit.apply();
    }


}
