package com.example.sepextremeg.activity;

import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.MY_SERVICE_NO;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;
import static com.example.sepextremeg.activity.LoginActivity.My_Name;
import static com.example.sepextremeg.activity.LoginActivity.My_ProfilePic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.adapters.AddedInfoRecyclerViewAdapter;
import com.example.sepextremeg.adapters.AddedQualificationInfoRecyclerViewAdapter;
import com.example.sepextremeg.adapters.AddedSalaryInfoRecyclerViewAdapter;
import com.example.sepextremeg.model.Profile;
import com.example.sepextremeg.model.Publications;
import com.example.sepextremeg.model.Qualifications;
import com.example.sepextremeg.model.SalaryScale;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewOverallProfileDetailsActivity extends AppCompatActivity {

    private TextView tvName, tvContact, tvJAddress, tvEmail;
    private RecyclerView rvQualifications, rvPublications,rvSalary;
    private ImageView img_provider, backBtn;
    private LinearLayout llViewMore;
    private ConstraintLayout clMainView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private NestedScrollView nestedScrollView;
    private ProgressDialog progressDialog;
    private String userID = "", employeeServiceNo = "";

    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    ArrayList<Qualifications> qualificationsArrayList;
    private AddedQualificationInfoRecyclerViewAdapter addedInfoDetailsRecycleViewAdapter;
    ArrayList<Publications> publicationsArrayList;
    private AddedInfoRecyclerViewAdapter infoRecyclerViewAdapter;
    private AddedSalaryInfoRecyclerViewAdapter addedSalaryInfoRecyclerViewAdapter;
    SalaryScale salaryScale;
    ArrayList<SalaryScale> salaryScaleArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_overall_profile_details);

        backBtn = findViewById(R.id.backBtn);
        tvName = findViewById(R.id.tvName);
        tvContact = findViewById(R.id.tvContact);
        tvJAddress = findViewById(R.id.tvJAddress);
        tvEmail = findViewById(R.id.tvEmail);
        img_provider = findViewById(R.id.img_provider);
        llViewMore = findViewById(R.id.llViewMore);
        rvQualifications = findViewById(R.id.rvQualifications);
        rvPublications = findViewById(R.id.rvPublications);
        rvSalary = findViewById(R.id.rvSalary);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        qualificationsArrayList = new ArrayList<>();
        publicationsArrayList = new ArrayList<>();
        salaryScaleArrayList = new ArrayList<>();

        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        employeeServiceNo = sharedPreferences.getString(MY_SERVICE_NO, "");
        tvEmail.setText("Service No: " + employeeServiceNo);

        userID = sharedPreferences.getString(My_ID, "");
        String un = sharedPreferences.getString(My_Name, "");
        tvName.setText(un);

        String image = sharedPreferences.getString(My_ProfilePic, "");
        Picasso.get().load(image).into(img_provider);

        loadPersonalInfo();

        llViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                nestedScrollView.fullScroll(View.FOCUS_DOWN);
                nestedScrollView.scrollTo(0, rvPublications.getBottom());
            }
        });
    }

    private void loadPersonalInfo() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        if (userID != null && !userID.equals("")) {
            DatabaseReference userNameRef = rootRef.child("profile").child(userID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        tvContact.setText("Contact me : " + profile.getPhone());
                        tvJAddress.setText("Find me : " + profile.getAddress());
                    }

                    loadQualifications();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addListenerForSingleValueEvent(eventListener);

        }
    }

    private void loadQualifications(){
        if (userID != null && !userID.equals("")){
            DatabaseReference databaseReference = mDatabase.child("Qualifications").child(userID);

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        qualificationsArrayList.add(ds.getValue(Qualifications.class));
                        getAddedInfoDetails();
                    }
                    System.out.println("qualifications size------- " + qualificationsArrayList.size());

                    loadPublications();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            databaseReference.addValueEventListener(eventListener);

        }

    }

    public void getAddedInfoDetails() {

        rvQualifications.setVisibility(View.VISIBLE);

        addedInfoDetailsRecycleViewAdapter = new AddedQualificationInfoRecyclerViewAdapter(this, qualificationsArrayList);
        rvQualifications.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvQualifications.setAdapter(addedInfoDetailsRecycleViewAdapter);
    }

    private void loadPublications(){
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
                        getAddedPublicationInfoDetails();
                        progressDialog.dismiss();
                    }
                    System.out.println("pub size------- " + publicationsArrayList.size());
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.hideShimmer();

                    loadSalaryInfo();
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

    public void getAddedPublicationInfoDetails() {

        rvPublications.setVisibility(View.VISIBLE);

        infoRecyclerViewAdapter = new AddedInfoRecyclerViewAdapter(this, publicationsArrayList);
        rvPublications.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPublications.setAdapter(infoRecyclerViewAdapter);
    }

    private void loadSalaryInfo() {

        if (userID != null && !userID.equals("")){
            DatabaseReference userNameRef = mDatabase.child("SalaryScale").child(userID);

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        // Get Post object and use the values to update the UI
                        SalaryScale salaryScale = dataSnapshot.getValue(SalaryScale.class);
                        assert salaryScale != null;
                        salaryScaleArrayList.add(salaryScale);
                        getAddedSalaryInfoDetails();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addValueEventListener(eventListener);
        }

    }

    public void getAddedSalaryInfoDetails() {

        rvSalary.setVisibility(View.VISIBLE);

        addedSalaryInfoRecyclerViewAdapter = new AddedSalaryInfoRecyclerViewAdapter(this, salaryScaleArrayList);
        rvSalary.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSalary.setAdapter(addedSalaryInfoRecyclerViewAdapter);
    }
}