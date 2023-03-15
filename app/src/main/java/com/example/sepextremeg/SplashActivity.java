package com.example.sepextremeg;

import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_Role;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.sepextremeg.activity.AdminDashboardActivity;
import com.example.sepextremeg.activity.LoginActivity;
import com.example.sepextremeg.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {

    private Handler handler = new Handler();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
         setContentView(R.layout.splash_screen);
         super.onCreate(savedInstanceState);

        handler.postDelayed(runnable,3000);
        mAuth = FirebaseAuth.getInstance();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            FirebaseUser user = mAuth.getCurrentUser();
            Intent intent;
            if(user != null){
                SharedPreferences sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
                String role = sharedPreferences.getString(My_Role, "role");
                if(role.equals("Admin")){
                    Log.d("adminn", "loginn");
                    intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                } else {
                    Log.d("staff", "loginn");
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }

            }else{
                Log.d("nonlogin", "not loginn");
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }
    };

}
