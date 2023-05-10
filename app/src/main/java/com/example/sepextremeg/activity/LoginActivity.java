package com.example.sepextremeg.activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sepextremeg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends Activity {

    public static String AUTHENTICATION = "AUTHENTICATION";
    public static String My_ID = "My_ID";
    public static String My_Name = "My_Name";
    public static String MY_SERVICE_NO = "MY_SERVICE_NO";
    public static String My_Email = "My_Email";
    public static String My_ProfilePic = "My_ProfilePic";
    public static String My_Role = "My_Role";

//    GoogleSignInClient mSignInClient;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressBar;
    Button signInButton;
    TextView forgotPassword;
    private TextInputEditText etEmail, etPassword;

    String userRole = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //Progress bar
        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Please Wait...");
        progressBar.setMessage("We are setting Everything for you...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        signInButton = findViewById(R.id.signInButton);
        forgotPassword = findViewById(R.id.forgotPassword);

        //Google Signin Options to get gmail and perform a gmail login
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("856073811133-4dhquv6ilhe50doc3633j26dihqvqgt1.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//
//        mSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);

        //Implementing OnClickListener to perform Login action

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etEmail.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else {

                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();

                    System.out.println("email  " + email);
                    System.out.println("pw  " + password);

                    progressBar.show();

                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");

                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(user.getUid());
                                        ValueEventListener eventListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    progressBar.cancel();

                                                    if (dataSnapshot.child("role").getValue().equals("Admin")) {
                                                        //navigating to the main activity after user successfully registers
                                                        saveDateToLocalDb(dataSnapshot.child("id").getValue().toString(),dataSnapshot.child("name").getValue().toString(),dataSnapshot.child("serviceNo").getValue().toString(), dataSnapshot.child("mail").getValue().toString(),
                                                                dataSnapshot.child("profilepic").getValue().toString(), dataSnapshot.child("role").getValue().toString());

                                                        Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    } else {
                                                        //navigating to the main activity after user successfully registers
                                                        saveDateToLocalDb(dataSnapshot.child("id").getValue().toString(),dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("serviceNo").getValue().toString(), dataSnapshot.child("mail").getValue().toString(),
                                                                dataSnapshot.child("profilepic").getValue().toString(), dataSnapshot.child("role").getValue().toString());

                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }

                                                    Toast.makeText(LoginActivity.this, "Successfully logged in",
                                                            Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                            }
                                        };
                                        userNameRef.addValueEventListener(eventListener);
                                    } else {
                                        progressBar.cancel();
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Login failed!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(LoginActivity.this, "Email has been sent to your email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void saveDateToLocalDb(String id, String name, String serviceNo,String email, String profilepic, String userrole) {

        Log.d("log4", "done");
        // Storing data into SharedPreferences
        sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // Storing the key and its value as the data fetched from edittext
        myEdit.putString(My_ID, id);
        myEdit.putString(My_Name, name);
        myEdit.putString(MY_SERVICE_NO, serviceNo);
        myEdit.putString(My_Email, email);
        myEdit.putString(My_ProfilePic, profilepic);
        myEdit.putString(My_Role, userrole);

        // Once the changes have been made, we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.commit();
    }

}