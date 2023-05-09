package com.example.sepextremeg.activity;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class CreateUserActivity extends AppCompatActivity {

    private TextInputEditText etUserName, etEmail, etServiceNo, etPassword, etConPassword;
    private Spinner spinner;
    private Button createUserBtn;
    private ImageView backBtn;
    ProgressDialog progressBar;

    String[] roles = {"Select role", "Admin", "Dean", "Head", "Senior Lecturer", "Lecturer", "Assistant Lecturer", "Instructor"};
    String userRole = "";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_user);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etServiceNo = findViewById(R.id.etServiceNo);
        etPassword = findViewById(R.id.etPassword);
        etConPassword = findViewById(R.id.etConPassword);
        backBtn = findViewById(R.id.backBtn);
        spinner = findViewById(R.id.spinner);
        createUserBtn = findViewById(R.id.createUserBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Please Wait...");
        progressBar.setMessage("User is creating...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapter = new ArrayAdapter(CreateUserActivity.this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                userRole = roles[i];
                Toast.makeText(CreateUserActivity.this, roles[i], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Implementing OnClickListener to perform Login action
        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userRole.equals("Select role")) {
                    Toast.makeText(CreateUserActivity.this, "Please select user role", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(etUserName.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etServiceNo.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, "Please enter service no", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etEmail.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etConPassword.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
                } else if (!etConPassword.getText().toString().equals(etPassword.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                } else {
                        String email = etEmail.getText().toString();
                        String password = etPassword.getText().toString();

                        progressBar.show();

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(CreateUserActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            //Hashmap to store the userdetails and setting it to fireabse
                                            HashMap<String, Object> user_details = new HashMap<>();

                                            //storing data in hashmap
                                            assert user != null;
                                            user_details.put("id", user.getUid());
                                            user_details.put("name", etUserName.getText().toString());
                                            user_details.put("serviceNo", etServiceNo.getText().toString());
                                            user_details.put("mail", etEmail.getText().toString());
                                            user_details.put("profilepic", "");
                                            user_details.put("password", etPassword.getText().toString());
                                            user_details.put("role", userRole);

                                            //Adding data to firebase
                                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child(user.getUid())
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

                                                                    Admin_details.put("id", user.getUid());
                                                                    Admin_details.put("name", etUserName.getText().toString());
                                                                    Admin_details.put("mail", etEmail.getText().toString());
                                                                    Admin_details.put("profilepic", "");
                                                                    Admin_details.put("password", etPassword.getText().toString());
                                                                    Admin_details.put("role", userRole);

                                                                    Log.d("log2", "done");

                                                                    //updating the user details in firebase
                                                                    myRef.child(user.getUid()).updateChildren(Admin_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                progressBar.cancel();

                                                                                Log.d("log3", "done");

                                                                                Toast.makeText(CreateUserActivity.this, "User created successfully",
                                                                                        Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }
                                                                    });

                                                                    clearTexts();
                                                                }
                                                                else {

                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                    DatabaseReference myRef = database.getReference().child("users").child("Staff");

                                                                    //Hashmap to store the userdetails and setting it to fireabse
                                                                    HashMap<String, Object> Staff_details = new HashMap<>();

                                                                    Staff_details.put("id", user.getUid());
                                                                    Staff_details.put("name", etUserName.getText().toString());
                                                                    Staff_details.put("serviceNo", etServiceNo.getText().toString());
                                                                    Staff_details.put("mail", etEmail.getText().toString());
                                                                    Staff_details.put("profilepic", "");
                                                                    Staff_details.put("password", etPassword.getText().toString());
                                                                    Staff_details.put("role", userRole);

                                                                    //updating the user details in firebase
                                                                    myRef.child(user.getUid()).updateChildren(Staff_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                progressBar.cancel();

                                                                                Toast.makeText(CreateUserActivity.this, "User created successfully",
                                                                                        Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }
                                                                    });

                                                                    clearTexts();
                                                                }

                                                            }
                                                        }
                                                    });

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(CreateUserActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void clearTexts(){
        etUserName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConPassword.setText("");

    }
}