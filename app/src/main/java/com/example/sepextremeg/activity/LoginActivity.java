package com.example.sepextremeg.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sepextremeg.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

public class LoginActivity extends Activity {

    public static String AUTHENTICATION = "AUTHENTICATION";
    public static String My_ID = "My_ID";
    public static String My_Name = "My_Name";
    public static String My_Email = "My_Email";
    public static String My_ProfilePic = "My_ProfilePic";
    public static String My_Role = "My_Role";

    GoogleSignInClient mSignInClient;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressBar;
    LinearLayout signInButton;
    Spinner spinner;

    String[] roles = {"Select role", "Admin", "Staff"};
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

        spinner = (Spinner) findViewById(R.id.Spinner);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                userRole = roles[i];
                Toast.makeText(getApplicationContext(), roles[i], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        signInButton = findViewById(R.id.GoogleSignInBtn);


        //Google Signin Options to get gmail and perform a gmail login
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("856073811133-4dhquv6ilhe50doc3633j26dihqvqgt1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);

        //Implementing OnClickListener to perform Login action
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userRole.equals("Select role")){
                    Toast.makeText(LoginActivity.this, "Please select user role", Toast.LENGTH_SHORT).show();
                } else {
                    //Showing all Gmails
                    Intent intent = mSignInClient.getSignInIntent();
                    startActivityForResult(intent, 100);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Log.d("loginggg", "done");
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            if (googleSignInAccountTask.isSuccessful()) {
                progressBar.show();
                try {
                    GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
                    Log.d("logingggg 1", "done");
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken(), null);

                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Log.d("log1", "done");

                                    //Hashmap to store the userdetails and setting it to fireabse
                                    HashMap<String, Object> user_details = new HashMap<>();

                                    //Accessing the user details from gmail
                                    String id = googleSignInAccount.getId().toString();
                                    String name = googleSignInAccount.getDisplayName().toString();
                                    String mail = googleSignInAccount.getEmail().toString();
                                    String pic = googleSignInAccount.getPhotoUrl().toString();

                                    //storing data in hashmap
                                    user_details.put("id", id);
                                    user_details.put("name", name);
                                    user_details.put("mail", mail);
                                    user_details.put("profilepic", pic);
                                    user_details.put("role", userRole);

                                    //Adding data to firebase
                                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child(id)
                                            .updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        //Checking for user role and adding data to firebase
                                                        if (userRole == "Admin") {
                                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                            DatabaseReference myRef = database.getReference().child("users").child("Admin");

                                                            //Hashmap to store the userdetails and setting it to fireabase
                                                            HashMap<String, Object> Admin_details = new HashMap<>();

                                                            Admin_details.put("id", id);
                                                            Admin_details.put("name", name);
                                                            Admin_details.put("mail", mail);
                                                            Admin_details.put("profilepic", pic);
                                                            Admin_details.put("role", userRole);

                                                            saveDateToLocalDb(id, name, mail, pic, userRole);

                                                            Log.d("log2", "done");

                                                            //updating the user details in firebase
                                                            myRef.child(id).updateChildren(Admin_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        progressBar.cancel();

                                                                        Log.d("log3", "done");

                                                                        //navigating to the main activity after user successfully registers
                                                                        Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                                                                        //Clears older activities and tasks
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });


                                                        } else {

                                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                            DatabaseReference myRef = database.getReference().child("users").child("Staff");

                                                            //Hashmap to store the userdetails and setting it to fireabse
                                                            HashMap<String, Object> Staff_details = new HashMap<>();


                                                            Staff_details.put("id", id);
                                                            Staff_details.put("name", name);
                                                            Staff_details.put("mail", mail);
                                                            Staff_details.put("profilepic", pic);
                                                            Staff_details.put("role", userRole);

                                                            Staff_details.put("totalDays", "0");
                                                            Staff_details.put("attendance", "0");

                                                            saveDateToLocalDb(id, name, mail, pic, userRole);

                                                            //updating the user details in firebase
                                                            myRef.child(id).updateChildren(Staff_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        progressBar.cancel();

                                                                        //navigating to the main activity after user successfully registers
                                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                        //Clears older activities and tasks
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });

                                                        }


                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }

                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void saveDateToLocalDb(String id, String name, String email, String profilepic, String userrole) {

        Log.d("log4", "done");
        // Storing data into SharedPreferences
        sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // Storing the key and its value as the data fetched from edittext
        myEdit.putString(My_ID, id);
        myEdit.putString(My_Name, name);
        myEdit.putString(My_Email, email);
        myEdit.putString(My_ProfilePic, profilepic);
        myEdit.putString(My_Role, userrole);

        // Once the changes have been made, we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.commit();


    }

}