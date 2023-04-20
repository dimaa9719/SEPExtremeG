package com.example.sepextremeg.activity;

import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.Fragments.ProfileFragment.isEdit;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sepextremeg.R;
import com.example.sepextremeg.adapters.AddedInfoRecyclerViewAdapter;
import com.example.sepextremeg.model.Profile;
import com.example.sepextremeg.model.Publications;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddPublicationDetailsActivity extends AppCompatActivity {

    LinearLayout addDetailsView, addOrNextView;
    EditText etPubTitle,etPubType, etAuthorName,etOrganisation, etLanguage,etCityCountry,etYearPublished, etPermLink;
    Button addDetailsBtn, addAnotherBtn;
    ImageView backBtn;
    RecyclerView added_info_recycle_view;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    Publications publications;
    String userID;
    ArrayList<Publications> publicationsArrayList;
    private AddedInfoRecyclerViewAdapter addedInfoDetailsRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_publication_details);

        addDetailsView = findViewById(R.id.addDetailsView);
        addOrNextView = findViewById(R.id.addOrNextView);
        etPubTitle = findViewById(R.id.etPubTitle);
        etPubType = findViewById(R.id.etPubType);
        etAuthorName = findViewById(R.id.etAuthorName);
        etOrganisation = findViewById(R.id.etOrganisation);
        etLanguage = findViewById(R.id.etLanguage);
        etCityCountry = findViewById(R.id.etCityCountry);
        etYearPublished = findViewById(R.id.etYearPublished);
        etPermLink = findViewById(R.id.etPermLink);
        addDetailsBtn = findViewById(R.id.addDetailsBtn);
        addAnotherBtn = findViewById(R.id.addAnotherBtn);
        added_info_recycle_view = findViewById(R.id.added_info_recycle_view);
        backBtn = findViewById(R.id.backBtn);

        publications = new Publications();
        publicationsArrayList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        userID = sharedPreferences.getString(My_ID, "");

        if (isEdit){
            readData();
            addOrNextView.setVisibility(View.VISIBLE);

        } else {
            addDetailsView.setVisibility(View.VISIBLE);
        }

        if (addOrNextView != null){
            addAnotherBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addOrNextView.setVisibility(View.GONE);
                    addDetailsView.setVisibility(View.VISIBLE);
                }
            });
        }

        addDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });

        backBtn.setOnClickListener(view -> {onBackPressed();});

    }

    private void insertData(){

        publicationsArrayList.clear();
        publications.setPubTitle(String.valueOf(etPubTitle.getText()));
        publications.setPubType(String.valueOf(etPubType.getText()));
        publications.setAuthorName(String.valueOf(etAuthorName.getText()));
        publications.setOrganisation(String.valueOf(etOrganisation.getText()));
        publications.setYearPublished(String.valueOf(etYearPublished.getText()));
        publications.setLanguage(String.valueOf(etLanguage.getText()));
        publications.setCitynCountry(String.valueOf(etCityCountry.getText()));
        publications.setPermLink(String.valueOf(etPermLink.getText()));
        publicationsArrayList.add(publications);

        DatabaseReference databaseReference = mDatabase.child("Publications").child(userID);
        String publicationID = databaseReference.push().getKey();

        assert publicationID != null;
        databaseReference.child(publicationID).setValue(publications)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            etAuthorName.setText("");
                            etCityCountry.setText("");
                            etPermLink.setText("");
                            etPubTitle.setText("");
                            etPubType.setText("");
                            etOrganisation.setText("");
                            etYearPublished.setText("");
                            etLanguage.setText("");

                            Toast.makeText(AddPublicationDetailsActivity.this,
                                    "Successfully Added",Toast.LENGTH_SHORT).show();

                            addDetailsView.setVisibility(View.GONE);
                            addOrNextView.setVisibility(View.VISIBLE);

                            getAddedInfoDetails();

                        }else {

                            Toast.makeText(AddPublicationDetailsActivity.this,
                                    "Failed to Add Data",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void getAddedInfoDetails() {

        added_info_recycle_view.setVisibility(View.VISIBLE);

        addedInfoDetailsRecycleViewAdapter = new AddedInfoRecyclerViewAdapter(this, publicationsArrayList);
        added_info_recycle_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        added_info_recycle_view.setAdapter(addedInfoDetailsRecycleViewAdapter);
    }

    private void readData() {

        if (userID != null && !userID.equals("")){
            DatabaseReference databaseReference = mDatabase.child("Publications").child(userID);

            progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Loading Data...");

            progressDialog.show();

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        publicationsArrayList.add(ds.getValue(Publications.class));
                        getAddedInfoDetails();
                        progressDialog.dismiss();
                    }
                    System.out.println("pub size------- " + publicationsArrayList.size());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    progressDialog.dismiss();
                }
            };
            databaseReference.addValueEventListener(eventListener);

        }

    }

//    public void editDetails(int position){
//        added_info_recycle_view.setVisibility(View.GONE);
//        addDetailsView.setVisibility(View.VISIBLE);
//        addOrNextView.setVisibility(View.GONE);
//        guestUserView.setVisibility(View.GONE);
//        String value = "Edit Details";
//        addDetailsBtn.setText(value);
//        isEdit = true;
//        updateIndex = position;
//        tempDataSet = appointmentBulkList.get(position).bulkRequest;
//        adapterSetup(this.testResponse, appointmentBulkList.get(position));
//    }
//
//    public void removeItem(int position){
//        appointmentBulkList.remove(position);
//        totalAmount = apiTotFee * appointmentBulkList.size();
//        totalAllocateTime = apiTotTime * appointmentBulkList.size();
//        String value;
//        if (appointmentBulkList.size() == 0){
//            value = apiTotTime + " mins video call";
//            tvCallTime.setText(value);
//        }
//        else{
//            value = totalAllocateTime + " mins video call";
//            tvCallTime.setText(value);
//        }
//    }

}