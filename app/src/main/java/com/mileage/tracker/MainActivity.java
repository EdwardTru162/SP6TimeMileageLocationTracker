package com.mileage.tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mileage.tracker.Account.RegistrationActivity;
import com.mileage.tracker.Adapters.TripAdapter;
import com.mileage.tracker.Data.LocationData;
import com.mileage.tracker.Helper.MiscHelper;
import com.mileage.tracker.Models.TripModel;
import com.mileage.tracker.Models.UserModel;
import android.Manifest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textViewName,textViewLogout;
    LinearLayout layoutEmpty;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    Button buttonAddNewTrip;
    TripAdapter tripAdapter;
    List<TripModel> tripModels;
    List<String>  ids;
    MiscHelper miscHelper;
    LocationData locationData;
    LocationManager locationManager;
    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        miscHelper=new MiscHelper(this);
        locationData=new LocationData(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initDB();
        initViews();
        getProfile();
        initRecyclerView();
        getTripsData();
    }
    private void getTripsData() {
        Dialog dialogLoading=miscHelper.openNetLoaderDialog();
        reference.child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogLoading.dismiss();
                tripModels.clear();
                ids.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    tripModels.add(data.getValue(TripModel.class));
                     ids.add(data.getKey());
                }
                if(tripModels.size()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              dialogLoading.dismiss();
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void initRecyclerView() {
        tripModels=new ArrayList<>();
        ids=new ArrayList<>();
        tripAdapter = new TripAdapter(tripModels, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tripAdapter);
        tripAdapter.setOnItemClickListener(new TripAdapter.onItemClickListener() {
            @Override
            public void show(int position) {
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("type","draw");
                intent.putExtra("name","none");
                locationData.setTripModel(tripModels.get(position));
                startActivity(intent);
            }
            @Override
            public void delete(int position) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Are you sure want to delete : "+tripModels.get(position).getTripName())
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  deleteItemFinally(position);

                                }
                            })
                            .show();
                }

        });

    }

    private void deleteItemFinally(int position) {
        Dialog dialogLoading=miscHelper.openNetLoaderDialog();
        reference.child("trips").child(ids.get(position))
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialogLoading.dismiss();
                        Toast.makeText(MainActivity.this, "successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      dialogLoading.dismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getProfile() {
        reference.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserModel userModel=snapshot.getValue(UserModel.class);
                    textViewName.setText(userModel.getName());
                }else {
                    auth.signOut();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference=FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(user.getUid());
    }

    private void initViews() {
        textViewName=findViewById(R.id.textViewName);
        textViewLogout=findViewById(R.id.textViewLogout);
        layoutEmpty=findViewById(R.id.layoutEmpty);
        recyclerView=findViewById(R.id.recyclerView);
        buttonAddNewTrip=findViewById(R.id.buttonAddNewTrip);
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              openLogout();
            }
        });
        buttonAddNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGPS();
            }
        });
    }
    /*****************  location Permission  ********/
    private void checkGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermissions();
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            declareTripPoints();
        } else {
            showSettingsAlert();
        }
    }
    private void declareTripPoints() {
        if(locationData.getTrip()){
            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("type","end");
            intent.putExtra("name","name");
            startActivity(intent);
        }else {
            startNewTrip();
        }



    }

    private void startNewTrip() {
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_trips);
        dialog.setCancelable(false);
        dialog.show();

        EditText editTextTripName;
        Button buttonCancel,buttonConfirm;
        editTextTripName=dialog.findViewById(R.id.editTextTripName);
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
        buttonConfirm=dialog.findViewById(R.id.buttonConfirm);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tripName=editTextTripName.getText().toString();
                if(tripName.equals("")){
                    editTextTripName.setError("Trip name required");
                    Toast.makeText(MainActivity.this, "Trip name required", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.dismiss();
                    Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                    intent.putExtra("type","start");
                    intent.putExtra("name",tripName);
                    startActivity(intent);

                }
            }
        });

    }

    private boolean checkLocationPermissions() {
        int permissionResult;
        List<String> permissionsList = new ArrayList<>();
        for (String p : permissions) {
            permissionResult = ContextCompat.checkSelfPermission(this, p);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(p);
            }
        }
        if (!permissionsList.isEmpty()) { //this is okay yes
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), 202);
            return false;
        }else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 202) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_d), Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this, getString(R.string.permission_success), Toast.LENGTH_SHORT).show();
                checkGPS();
            }
        }
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.gps_n));
        alertDialog.setMessage(getString(R.string.gps_instruction));
        alertDialog.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent,2021);
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2021) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, getString(R.string.gps_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.gps_need), Toast.LENGTH_SHORT).show();

                checkGPS();
            }
        }
    }



    private void openLogout() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure to logout from this application?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(user!=null){
                            auth.signOut();
                            finish();
                            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationData loc=new LocationData(this);
        if(loc.getTrip()){
            buttonAddNewTrip.setText("End trip :"+locationData.getTripModel().getTripName());
        }else {
            buttonAddNewTrip.setText("Add new trip");
        }
    }
}