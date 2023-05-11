package com.example.sepextremeg.Fragments;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;
import static com.example.sepextremeg.activity.LoginActivity.My_Name;
import static com.example.sepextremeg.activity.LoginActivity.My_ProfilePic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.AddProfileDetailsActivity;
import com.example.sepextremeg.activity.AddPublicationDetailsActivity;
import com.example.sepextremeg.activity.AddQualificationDetailsActivity;
import com.example.sepextremeg.activity.LoginActivity;
import com.example.sepextremeg.activity.ViewMyInvoiceDetailsActivity;
import com.example.sepextremeg.activity.ViewOverallProfileDetailsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView imageView;
    TextView userName;
    Button signOutBtn, editProfileBtn, editPublicationsBtn,editQualificationBtn,
            resetPasswordBtn, updatePwButton, deleteAccBtn, viewSalaryDetailsBtn, viewOverallProfileBtn;
    LinearLayout llActions, llPasswordReset;
    EditText etNewPassword;
    public static boolean isEdit = false;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = (CircleImageView) view.findViewById(R.id.ProfileImageView);
        userName = (TextView) view.findViewById(R.id.UserNameTxt);
        signOutBtn = (Button) view.findViewById(R.id.SignOutBtn);
        editProfileBtn = (Button) view.findViewById(R.id.editProfileBtn);
        resetPasswordBtn = (Button) view.findViewById(R.id.resetPasswordBtn);
        llActions = view.findViewById(R.id.llActions);
        llPasswordReset = view.findViewById(R.id.llPasswordReset);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        updatePwButton = view.findViewById(R.id.updatePwButton);
        deleteAccBtn = view.findViewById(R.id.deleteAccBtn);
        editPublicationsBtn = view.findViewById(R.id.editPublicationsBtn);
        editQualificationBtn = view.findViewById(R.id.editQualificationBtn);
        viewSalaryDetailsBtn = view.findViewById(R.id.viewSalaryDetailsBtn);
        viewOverallProfileBtn = view.findViewById(R.id.viewOverallProfileBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        String un = sharedPreferences.getString(My_Name, "");
        userName.setText(un);

        String userId = sharedPreferences.getString(My_ID, "");

        String image = sharedPreferences.getString(My_ProfilePic, "");

        if (!image.equals("")) {
            Picasso.get().load(image).into(imageView);
        }

        checkRecordExists();

        //add my profile details
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddProfileDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //add my publication details
        editPublicationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPublicationDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //add my qualification details
        editQualificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddQualificationDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //view my salary details
        if (viewSalaryDetailsBtn != null) {
            viewSalaryDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ViewMyInvoiceDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }

        //view my info details
        if (viewOverallProfileBtn != null) {
            viewOverallProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ViewOverallProfileDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }

        //implementing onClickListener to make the user signOut
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //User Signout
                FirebaseAuth.getInstance().signOut();

                //Redirecting to starting Activity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llActions.setVisibility(View.GONE);
                llPasswordReset.setVisibility(View.VISIBLE);
            }
        });

        if (llPasswordReset != null){
            updatePwButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(etNewPassword.getText().toString())){
                        Toast.makeText(requireActivity(), "Please enter new password", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        user.updatePassword(etNewPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User password updated.");
                                            Toast.makeText(requireActivity(), "User password updated.", Toast.LENGTH_SHORT).show();

                                            llActions.setVisibility(View.VISIBLE);
                                            llPasswordReset.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(requireActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        mDatabase.child("AllUsers").child(userId).child("password").setValue(etNewPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("database", "updated");

                                        }else {
                                            Log.d("database", "failed");
                                        }
                                    }
                                });

                        mDatabase.child("users").child("Staff").child(userId).child("password").setValue(etNewPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("database", "updated");
                                        }else {
                                            Log.d("database", "failed");
                                        }
                                    }
                                });

                    }
                }
            });
        }

        deleteAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               delete(userId);
            }
        });
        return view;
    }

    private void checkRecordExists() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        String userID = sharedPreferences.getString(My_ID, "");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        if (userID != null && !userID.equals("")){
            DatabaseReference userNameRef = rootRef.child("profile").child(userID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        isEdit = true;
                        editProfileBtn.setText("Edit Profile Details");

                        viewOverallProfileBtn.setVisibility(View.VISIBLE);
                    } else {
                        isEdit = false;
                        editProfileBtn.setText("Add Profile Details");
                        viewOverallProfileBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addListenerForSingleValueEvent(eventListener);

            DatabaseReference reference = rootRef.child("Publications").child(userID);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        isEdit = true;
                        System.out.println("111");
                        editPublicationsBtn.setText("Edit Publication Details");
                    } else {
                        isEdit = false;
                        editPublicationsBtn.setText("Add Publication Details");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            reference.addListenerForSingleValueEvent(listener);

            DatabaseReference qualiRef = rootRef.child("Qualifications").child(userID);
            ValueEventListener qualiListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        isEdit = true;
                        editQualificationBtn.setText("Edit Qualification Details");
                    } else {
                        isEdit = false;
                        editQualificationBtn.setText("Add Qualification Details");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            qualiRef.addListenerForSingleValueEvent(qualiListener);

            DatabaseReference salaryRef = rootRef.child("SalaryScale").child(userID);
            ValueEventListener salListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                      viewSalaryDetailsBtn.setVisibility(View.VISIBLE);
                    } else {
                        viewSalaryDetailsBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            salaryRef.addListenerForSingleValueEvent(salListener);
        }

    }

    private void delete(String id) {
        // creating a variable for our Database
        // Reference for Firebase.
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference().child("AllUsers");
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("users");

        DatabaseReference dbrefProfile= FirebaseDatabase.getInstance().getReference().child("profile");
        DatabaseReference dbrefQualification= FirebaseDatabase.getInstance().getReference().child("Qualifications");
        DatabaseReference dbrefPublications= FirebaseDatabase.getInstance().getReference().child("Publications");
        DatabaseReference dbrefSalaryScale= FirebaseDatabase.getInstance().getReference().child("SalaryScale");

        // we are use add listerner
        // for event listener method
        // which is called with query.
        Query query=dbref.child(id);
        Query query1=databaseReference.child(id);

        Query query5=dbrefProfile.child(id);
        Query query2=dbrefQualification.child(id);
        Query query3=dbrefPublications.child(id);
        Query query4=dbrefSalaryScale.child(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //delete user from auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted successfully.");
                        }
                    }
                });
    }
}