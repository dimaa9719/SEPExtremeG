package com.example.sepextremeg.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sepextremeg.R;
import com.example.sepextremeg.adapters.AddedInfoRecyclerViewAdapter;
import com.example.sepextremeg.adapters.AddedQualificationInfoRecyclerViewAdapter;
import com.example.sepextremeg.adapters.HomeUsersGridAdapter;
import com.example.sepextremeg.model.Profile;
import com.example.sepextremeg.model.Publications;
import com.example.sepextremeg.model.Qualifications;
import com.example.sepextremeg.model.SalaryScale;
import com.example.sepextremeg.model.StaffModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StaffMemberActivity extends AppCompatActivity {

    private TextView tvName, tvContact, tvJAddress, tvEmail, tvRefId,
            tvTodayDate,tvContactNo, tvFaculty, tvPosition, tvAuthor, btnDownload, tvAddSalary;
    private RecyclerView rvQualifications, rvPublications, rvQualificationsPdf, rvPublicationsPdf;
    private ImageView img_provider, backBtn;
    private LinearLayout llViewMore, ll_pdf, llAddSalary;
    private ConstraintLayout clMainView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private NestedScrollView nestedScrollView;
    private ProgressDialog progressDialog;
    private String userID = "", employeeName, empServiceNo;
    private int page = -1;
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    ArrayList<Qualifications> qualificationsArrayList;
    private AddedQualificationInfoRecyclerViewAdapter addedInfoDetailsRecycleViewAdapter;
    ArrayList<Publications> publicationsArrayList;
    private AddedInfoRecyclerViewAdapter infoRecyclerViewAdapter;
    private DatabaseReference mDatabase;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Bitmap bitmap;
    public static boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_member);

        backBtn = findViewById(R.id.backBtn);
        tvName = findViewById(R.id.tvName);
        tvContact = findViewById(R.id.tvContact);
        tvJAddress = findViewById(R.id.tvJAddress);
        tvEmail = findViewById(R.id.tvEmail);
        img_provider = findViewById(R.id.img_provider);
        llViewMore = findViewById(R.id.llViewMore);
        rvQualifications = findViewById(R.id.rvQualifications);
        rvPublications = findViewById(R.id.rvPublications);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        ll_pdf = findViewById(R.id.ll_pdf);
        rvPublicationsPdf = findViewById(R.id.rvPublicationsPdf);
        rvQualificationsPdf = findViewById(R.id.rvQualificationsPdf);
        btnDownload = findViewById(R.id.btnDownload);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        tvRefId = findViewById(R.id.tvRefId);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPosition = findViewById(R.id.tvPosition);
        tvFaculty = findViewById(R.id.tvFaculty);
        tvContactNo = findViewById(R.id.tvContactNo);
        clMainView = findViewById(R.id.clMainView);
        llAddSalary = findViewById(R.id.llAddSalary);
        tvAddSalary = findViewById(R.id.tvAddSalary);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        qualificationsArrayList = new ArrayList<>();
        publicationsArrayList = new ArrayList<>();

        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        userID = getIntent().getStringExtra("MemId");
//        tvUserId.setText("User ID: " + userID);

        employeeName = getIntent().getStringExtra("MemName");
        tvName.setText(getIntent().getStringExtra("MemName"));

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        tvTodayDate.setText("Create At: " + currentDateTimeString);

        if(getIntent().getStringExtra("MemImage") !=null) {
            Picasso.get().load(getIntent().getStringExtra("MemImage")).into(img_provider);
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()){
                    bitmap = loadBitmap(ll_pdf, ll_pdf.getWidth(), ll_pdf.getHeight());
                    createPdf();

                    clMainView.setVisibility(View.GONE);
                    img_provider.setVisibility(View.GONE);
                    ll_pdf.setVisibility(View.VISIBLE);

                }else {
                    requestPermission();
                }
            }
        });

        loadPersonalInfo();

        llViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                nestedScrollView.fullScroll(View.FOCUS_DOWN);
                nestedScrollView.scrollTo(0, rvPublications.getBottom());
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkRecordExists();

        //add my profile details
        llAddSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffMemberActivity.this, AddMemberSalaryScaleActivity.class);
                intent.putExtra("MemId", userID);
                intent.putExtra("MemName",employeeName);
                intent.putExtra("MemEmail",empServiceNo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void checkRecordExists() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        if (userID != null && !userID.equals("")){
            DatabaseReference userNameRef = rootRef.child("SalaryScale").child(userID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        isEdit = true;
                        tvAddSalary.setText("Edit Salary Details");
                    } else {
                        isEdit = false;
                        tvAddSalary.setText("Add Salary Details");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addListenerForSingleValueEvent(eventListener);
        }

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

                        tvAuthor.setText(profile.getFullName());
                        tvContactNo.setText(profile.getPhone());
                        tvFaculty.setText(profile.getFacultyName());
                        tvPosition.setText(profile.getJobTitle());
                    }

                    loadQualifications();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addListenerForSingleValueEvent(eventListener);

            //getserviceNo
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child("Staff").child(userID);

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   if (dataSnapshot.exists()){
                       StaffModel staffModel = dataSnapshot.getValue(StaffModel.class);
                       assert staffModel != null;
                       empServiceNo = staffModel.getServiceNo();
                       tvRefId.setText("Service No: " + empServiceNo);
                       tvEmail.setText("Service No: " + staffModel.getServiceNo());
                   }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    progressDialog.dismiss();
                }
            };
            databaseReference.addValueEventListener(listener);

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

        rvQualificationsPdf.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvQualificationsPdf.setAdapter(addedInfoDetailsRecycleViewAdapter);
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

        rvPublicationsPdf.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPublicationsPdf.setAdapter(infoRecyclerViewAdapter);
    }

    private Bitmap loadBitmap(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void createPdf() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;
        int convertWidth = (int) width;
        int convertHeight = (int) height;

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();

        paint.setColor(Color.BLUE);
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, false);
        canvas.drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        String newDate = currentDate.replace("-", "_").replace(" ", "").replace(":", "_");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), employeeName + "ProfileDetailsReport" + newDate + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Successfully downloaded!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

        //after close the document
        pdfDocument.close();
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}