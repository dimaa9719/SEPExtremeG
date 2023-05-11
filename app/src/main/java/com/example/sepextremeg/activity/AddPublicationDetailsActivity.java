package com.example.sepextremeg.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.Fragments.ProfileFragment.isEdit;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.MY_SERVICE_NO;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddPublicationDetailsActivity extends AppCompatActivity {

    LinearLayout addDetailsView, addOrNextView, ll_pdf;
    EditText etPubTitle,etPubType, etAuthorName,etOrganisation, etLanguage,etCityCountry,etYearPublished, etPermLink;
    Button addDetailsBtn, addAnotherBtn;
    ImageView backBtn;
    TextView tvtitle, tvRefId, tvPublicationId, tvTodayDate, tvAuthor, tvPubTitle, tvPubType, tvYearPublished;
    RecyclerView added_info_recycle_view;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    Publications publications;
    String userID;
    ArrayList<Publications> publicationsArrayList;
    private AddedInfoRecyclerViewAdapter addedInfoDetailsRecycleViewAdapter;
    private int updateIndex = 0;
    String publicationID = "", employeeServiceNo = "";
    private Bitmap bitmap;
    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_publication_details);

        addDetailsView = findViewById(R.id.addDetailsView);
        addOrNextView = findViewById(R.id.addOrNextView);
        tvtitle = findViewById(R.id.tvtitle);
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
        ll_pdf = findViewById(R.id.ll_pdf);
        tvPublicationId = findViewById(R.id.tvPublicationId);
        tvRefId = findViewById(R.id.tvRefId);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPubTitle = findViewById(R.id.tvPubTitle);
        tvPubType = findViewById(R.id.tvPubType);
        tvYearPublished = findViewById(R.id.tvYearPublished);

        publications = new Publications();
        publicationsArrayList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        userID = sharedPreferences.getString(My_ID, "");
        employeeServiceNo = sharedPreferences.getString(MY_SERVICE_NO, "");
        tvRefId.setText("Service No: " + employeeServiceNo);

        if (checkPermission()){

        }else {
            requestPermission();
        }

        if (isEdit){
            System.out.println("2222");
            readData();
            addOrNextView.setVisibility(View.VISIBLE);
            if (addOrNextView != null){
                addAnotherBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addOrNextView.setVisibility(View.GONE);
                        addDetailsView.setVisibility(View.VISIBLE);

                        addDetailsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                insertData();
                            }
                        });
                    }
                });
            }
        } else {
            System.out.println("3333");
            addDetailsView.setVisibility(View.VISIBLE);
            addDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertData();
                }
            });
        }

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

        DatabaseReference databaseReference = mDatabase.child("Publications").child(userID);
        publicationID = databaseReference.push().getKey();
        publications.setPublicationId(publicationID);
        System.out.println("pub id--- " + publicationID);
        assert publicationID != null;

        publicationsArrayList.add(publications);
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

                            onBackPressed();

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
        addedInfoDetailsRecycleViewAdapter.notifyDataSetChanged();
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

    private void updateData(String publicationId) {

        HashMap<String, Object> publications = new HashMap<>();
        publications.put("pubTitle", String.valueOf(etPubTitle.getText()));
        publications.put("pubType", String.valueOf(etPubType.getText()));
        publications.put("authorName", String.valueOf(etAuthorName.getText()));
        publications.put("organisation", String.valueOf(etOrganisation.getText()));
        publications.put("language", String.valueOf(etLanguage.getText()));
        publications.put("citynCountry", String.valueOf(etCityCountry.getText()));
        publications.put("yearPublished", String.valueOf(etYearPublished.getText()));

        DatabaseReference databaseReference = mDatabase.child("Publications").child(userID);
        databaseReference.child(publicationId).updateChildren(publications)
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
                            "Successfully Updated",Toast.LENGTH_SHORT).show();

                    onBackPressed();

                }else {

                    Toast.makeText(AddPublicationDetailsActivity.this,
                            "Failed to Update Data",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void editDetails(int position, ArrayList<Publications> bulkList, String publicationId){
        added_info_recycle_view.setVisibility(View.GONE);
        addDetailsView.setVisibility(View.VISIBLE);
        addOrNextView.setVisibility(View.GONE);
        String value = "Edit Publication Details";
        addDetailsBtn.setText(value);
        isEdit = true;
        updateIndex = position;

        etPubTitle.setText(bulkList.get(updateIndex).getPubTitle());
        etPubType.setText(bulkList.get(updateIndex).getPubType());
        etAuthorName.setText(bulkList.get(updateIndex).getAuthorName());
        etOrganisation.setText(bulkList.get(updateIndex).getOrganisation());
        etLanguage.setText(bulkList.get(updateIndex).getLanguage());
        etCityCountry.setText(bulkList.get(updateIndex).getCitynCountry());
        etYearPublished.setText(bulkList.get(updateIndex).getYearPublished());
        etPermLink.setText(bulkList.get(updateIndex).getPermLink());

        addDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(publicationId);
            }
        });
    }

    public void removeItem(String publicationId){
        DatabaseReference databaseReference = mDatabase.child("Publications").child(userID);
        databaseReference.child(publicationId).removeValue();
        Toast.makeText(AddPublicationDetailsActivity.this,
                "Successfully Deleted",Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

    public void downloadMyPublication(int position, ArrayList<Publications> bulkList){

        tvPublicationId.setText(bulkList.get(position).getPublicationId());
        tvAuthor.setText(bulkList.get(position).getAuthorName());
        tvtitle.setText(bulkList.get(position).getPubTitle());
        tvPubType.setText(bulkList.get(position).getPubType());
        tvYearPublished.setText(bulkList.get(position).getYearPublished());

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        tvTodayDate.setText("Create At: " + currentDateTimeString);

        bitmap = loadBitmap(ll_pdf, ll_pdf.getWidth(), ll_pdf.getHeight());
        createPdf();

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
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyPublication" + newDate + ".pdf");

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