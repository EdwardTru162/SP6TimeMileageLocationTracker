package com.mileage.tracker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mileage.tracker.Account.RegistrationActivity;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        intiFirebaseAuth();
        runThreadDelay();
    }
    private void intiFirebaseAuth() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user!=null)
            user.reload();
    }
    private void runThreadDelay() {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    if (isLogin()) {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreen.this, RegistrationActivity.class));
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
    private boolean isLogin() {
        return user != null;
    }
}