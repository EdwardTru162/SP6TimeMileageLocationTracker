package com.mileage.tracker.Account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.mileage.tracker.Helper.MiscHelper;
import com.mileage.tracker.MainActivity;
import com.mileage.tracker.R;


public class LoginActivity extends AppCompatActivity {
    EditText editTextEmail,editTextPassword;
    TextView forgetPassword;
    Button buttonLogin;
    ImageView backArrow;
    MiscHelper miscHelper;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        miscHelper=new MiscHelper(this);
        initDB();
        initViews();
    }
    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }

    private void initViews() {
        editTextEmail=findViewById(R.id.editTextEmail);
        backArrow=findViewById(R.id.backArrow);
        editTextPassword=findViewById(R.id.editTextPassword);
        forgetPassword=findViewById(R.id.forgetPassword);
        buttonLogin=findViewById(R.id.buttonLogin);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPassword.class));
            }
        });
       buttonLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               doLogin();
           }
       });
       backArrow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
           }
       });
    }
    private void doLogin() {
        String email=editTextEmail.getText().toString();
        String password=editTextPassword.getText().toString();
        if(email.equals("")){
            editTextEmail.setError("Email Required");
        }else if(!miscHelper.isEmailValid(email)){
            editTextEmail.setError("Invalid email");
        }else if(password.equals("")){
            editTextPassword.setError("Password required");
        }else {
            Dialog dialog=miscHelper.openNetLoaderDialog();
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startNewSession();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Failed "+e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }
    private void startNewSession() {
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}