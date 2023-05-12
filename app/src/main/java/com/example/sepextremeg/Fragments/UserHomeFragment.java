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

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.AddProfileDetailsActivity;
import com.example.sepextremeg.activity.AddPublicationDetailsActivity;
import com.example.sepextremeg.activity.AddQualificationDetailsActivity;
import com.example.sepextremeg.activity.ViewMyInvoiceDetailsActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeFragment extends Fragment {

    TextView tvUserName, tvAddProfileInfo, tvAddQualifications, tvAddPublications, tvViewInvoiceDetails;
    CircleImageView emp_image;
    CardView card_view_type_info,card_view_type_qualifications,
            card_view_type_publications, card_view_type_invoice;
    public static boolean isEdit = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_home, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvAddProfileInfo = view.findViewById(R.id.tvAddProfileInfo);
        tvAddQualifications = view.findViewById(R.id.tvAddQualifications);
        tvAddPublications = view.findViewById(R.id.tvAddPublications);
        tvViewInvoiceDetails = view.findViewById(R.id.tvViewInvoiceDetails);
        emp_image = view.findViewById(R.id.ProfileImageView);
        card_view_type_info = view.findViewById(R.id.card_view_type);
        card_view_type_qualifications = view.findViewById(R.id.card_view_type1);
        card_view_type_publications = view.findViewById(R.id.card_view_type2);
        card_view_type_invoice = view.findViewById(R.id.card_view_type3);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        String un = sharedPreferences.getString(My_Name, "");
        tvUserName.setText("Hello " + un);

        String image = sharedPreferences.getString(My_ProfilePic, "");
        if (image !=null && !image.equals("")){
            Picasso.get().load(image).into(emp_image);
        }

        //add my profile details
        card_view_type_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddProfileDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //add my publication details
        card_view_type_publications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPublicationDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //add my qualification details
        card_view_type_qualifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddQualificationDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //view my salary details
        card_view_type_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewMyInvoiceDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        checkRecordExists();

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
                        tvAddProfileInfo.setText("Edit Profile Details");
                    } else {
                        isEdit = false;
                        tvAddProfileInfo.setText("Add Profile Details");
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
                        tvAddPublications.setText("Edit Publication Details");
                    } else {
                        isEdit = false;
                        tvAddPublications.setText("Add Publication Details");
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
                        tvAddQualifications.setText("Edit Qualification Details");
                    } else {
                        isEdit = false;
                        tvAddQualifications.setText("Add Qualification Details");
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
                        card_view_type_invoice.setVisibility(View.VISIBLE);
                    } else {
                        card_view_type_invoice.setVisibility(View.GONE);
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

}