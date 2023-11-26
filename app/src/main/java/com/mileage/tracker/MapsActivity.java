package com.mileage.tracker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.mileage.tracker.Data.LocationData;
import com.mileage.tracker.Helper.MiscHelper;
import com.mileage.tracker.Models.TripModel;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    String type;
    String name;
    LocationData locationData;
    private FusedLocationProviderClient fusedLocationClient;
    ProgressDialog progressDialog;
    MiscHelper miscHelper;
    Boolean tracker=true;
    Marker markerCurrentLocation=null;
    private Polyline currentPolyline;
    Button buttonAllTrips;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationData = new LocationData(this);
        getIntentData();
        miscHelper=new MiscHelper(this);
        buttonAllTrips=findViewById(R.id.buttonAllTrips);
        buttonAllTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void getIntentData() {
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(type.equals("draw")){
            TripModel tmd=locationData.getTripModel();
           drawMarkerSimple(new LatLng(Double.parseDouble(tmd.getTripStartLat()),Double.parseDouble(tmd.getTripStartLng())),true,tmd.getTripStartLocationName());
            drawMarkerSimple(new LatLng(Double.parseDouble(tmd.getTripEndLat()),Double.parseDouble(tmd.getTripEndLng())),false,tmd.getTripEndLocationName());
            requestDirections(new LatLng(Double.parseDouble(tmd.getTripStartLat()),Double.parseDouble(tmd.getTripStartLng())),new LatLng(Double.parseDouble(tmd.getTripEndLat()),Double.parseDouble(tmd.getTripEndLng())));
        }else {
            if(locationData.getTrip()){
                TripModel tripModel=locationData.getTripModel();
                drawMarkerSimple(new LatLng(Double.parseDouble(tripModel.getTripStartLat()),Double.parseDouble(tripModel.getTripStartLng())),true,tripModel.getTripStartLocationName());
            }
            openDialog();
            requestLocation();
        }

    }

    private void requestDirections(LatLng origin, LatLng destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=AIzaSyDed2DVcgbEXHzuFFlDLrQNX-3ovH5sS7w";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the response and draw the route on the map
                        drawRouteFromResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors
                    }
                });

        queue.add(request);
    }

    private void drawRouteFromResponse(JSONObject response) {
        try {
            JSONArray routes = response.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String encodedPolyline = overviewPolyline.getString("points");

            List<LatLng> polylinePoints = decodePoly(encodedPolyline);

            if (currentPolyline != null) {
                currentPolyline.remove();
            }

            currentPolyline = mMap.addPolyline(new PolylineOptions()
                    .addAll(polylinePoints)
                    .width(10)
                    .color(Color.BLUE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }

        return poly;
    }


    private void drawMarkerSimple(LatLng location, boolean b, String placeName) {
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.title(placeName);

        BitmapDescriptor icon;
        if(b){
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            markerOptions.snippet("Trip start point");
        }else {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            markerOptions.snippet("Trip end point");
        }
        markerOptions.icon(icon);


        markerOptions.position(new LatLng(location.latitude,location.longitude));
        Marker marker= mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        marker.showInfoWindow();
    }

    private void openDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting current location...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void requestLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // Update interval in milliseconds
                .setFastestInterval(5000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                progressDialog.dismiss();
                if(!type.equals("draw")){
                    markerMovement(location);
                }
                if(tracker){
                    Toast.makeText(MapsActivity.this, ""+latitude+longitude, Toast.LENGTH_SHORT).show();
                    getAddress(new LatLng(location.getLatitude(),location.getLongitude()));
                    tracker=false;
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           finish();
            Toast.makeText(this, "Need permissions", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void markerMovement(Location location) {
        if(markerCurrentLocation!=null){
            markerCurrentLocation.remove();
        }
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.title("Current Location");
        BitmapDescriptor icon;
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        markerOptions.snippet(location.getSpeed()+"MPH");
        markerOptions.icon(icon);
        markerOptions.position(new LatLng(location.getLatitude(),location.getLongitude()));
        markerCurrentLocation= mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
        markerCurrentLocation.showInfoWindow();

    }

    private void getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String placeName="unknown";
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
               placeName = addresses.get(0).getFeatureName();
               uploadData(location,placeName);

            } else {
                placeName="unknown";
                uploadData(location,placeName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            placeName="unknown";
            uploadData(location,placeName);
        }

    }

    private void uploadData(LatLng location,String placeName) {
        drawMarker(location,locationData.getTrip(),placeName);
    }
    private void drawMarker(LatLng location, boolean trip, String placeName) {
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.title(placeName);
        if(locationData.getTrip()){
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            markerOptions.snippet("Trip end point");
            markerOptions.icon(icon);
            locationData.setTrip(false);
            uploadFinalData(location,placeName);
        }else {
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            markerOptions.snippet("Trip start point");
            markerOptions.icon(icon);
            locationData.setTrip(true);
            updateLocationPref(location,placeName);
        }
        markerOptions.position(new LatLng(location.latitude,location.longitude));
        Marker marker= mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        marker.showInfoWindow();
    }

    private void uploadFinalData(LatLng location, String placeName) {
      TripModel tr=locationData.getTripModel();
      tr.setTripEndLat(location.latitude+"");
      tr.setTripEndLng(location.longitude+"");
      tr.setTripEndDateAndTime(miscHelper.currentDate()+"  "+miscHelper.currentTime());
      tr.setTripEndLocationName(placeName);
      locationData.setTripModel(tr);
      getTotalMiles();
    }

    private void getTotalMiles() {
        TripModel tripModel=locationData.getTripModel();
        RequestQueue queue = Volley.newRequestQueue(this);
        String origin = tripModel.getTripStartLat()+","+tripModel.getTripStartLng();
        String destination = tripModel.getTripEndLat()+","+tripModel.getTripEndLng();

        String apiKey = "AIzaSyDed2DVcgbEXHzuFFlDLrQNX-3ovH5sS7w";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray routes = response.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);
                            JSONArray legs = route.getJSONArray("legs");
                            JSONObject leg = legs.getJSONObject(0);
                            JSONObject distance = leg.getJSONObject("distance");
                            String distanceText = distance.getString("text");
                              uploadFinalDataOnFirebase(distanceText);
                        } catch (Exception e) {
                            e.printStackTrace();
                            uploadFinalDataOnFirebase("0");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        uploadFinalDataOnFirebase("API Error");

                        // You can log the error or take other appropriate action
                    }
                }
        );
        queue.add(request);
    }

    private void uploadFinalDataOnFirebase(String distanceText) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(user.getUid())
                .child("trips");

        TripModel tripModel=locationData.getTripModel();
        tripModel.setTripTotalMiles(distanceText);
        Dialog dialog=miscHelper.openNetLoaderDialog();
        reference.push().setValue(tripModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                 dialog.dismiss();
                Toast.makeText(MapsActivity.this, "Data uploaded successfully ", Toast.LENGTH_SHORT).show();
                locationData.clearTrip();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            dialog.dismiss();
                Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLocationPref(LatLng location, String placeName) {
        TripModel tr=new TripModel();
        tr.setTripName(name);
        tr.setTripStartDateAndTime(miscHelper.currentDate() +"  "+miscHelper.currentTime());
        tr.setTripStartLat(location.latitude+"");
        tr.setTripStartLng(location.longitude+"");
        tr.setTripStartLocationName(placeName);
        locationData.setTripModel(tr);
    }
}