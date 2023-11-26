package com.mileage.tracker.Account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mileage.tracker.Helper.MiscHelper;
import com.mileage.tracker.MainActivity;
import com.mileage.tracker.Models.UserModel;
import com.mileage.tracker.R;

public class RegistrationActivity extends AppCompatActivity {

    EditText editTextUserName,editTextEmail
            ,editTextPassword;

    Button buttonCreateAccount;
    TextView textViewAlreadyHaveAccount;


    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    MiscHelper miscHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        miscHelper=new MiscHelper(this);
        initDB();
        initViews();
    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users");
    }

    private void initViews() {
        editTextUserName=findViewById(R.id.editTextUserName);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);

        buttonCreateAccount=findViewById(R.id.buttonCreateAccount);
        textViewAlreadyHaveAccount=findViewById(R.id.textViewAlreadyHaveAccount);
        textViewAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegistration();
            }
        });

    }

    private void doRegistration() {
        UserModel userModel=new UserModel();
        userModel.setEmail(editTextEmail.getText().toString());
        userModel.setName(editTextUserName.getText().toString());
        String password=editTextPassword.getText().toString();

        if(userModel.getEmail().equals("")){
            editTextEmail.setError("Email Required");
        }else if(!miscHelper.isEmailValid(userModel.getEmail())){
            editTextEmail.setError("Invalid email");
        }else if(userModel.getName().equals("")){
            editTextUserName.setError("Name Required");
        }else if(password.equals("")){
            editTextPassword.setError("Password required");
        }else if(password.length()<5){
            editTextPassword.setError("Password is too short, at least 5 characters");
        }else {
            Dialog dialogLoading=miscHelper.openNetLoaderDialog();
            auth.createUserWithEmailAndPassword(userModel.getEmail(),password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(RegistrationActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    user=auth.getCurrentUser();
                    reference.child(user.getUid()).child("profile")
                            .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startNewSession();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();     
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                 dialogLoading.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Account creation failed "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void startNewSession() {
        Intent intent=new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}