package com.example.sepextremeg.activity;

import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.Fragments.ProfileFragment.isEdit;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sepextremeg.R;
import com.example.sepextremeg.adapters.AddedInfoRecyclerViewAdapter;
import com.example.sepextremeg.adapters.AddedQualificationInfoRecyclerViewAdapter;
import com.example.sepextremeg.model.Publications;
import com.example.sepextremeg.model.Qualifications;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AddQualificationDetailsActivity extends AppCompatActivity {

    LinearLayout addDetailsView, addOrNextView;
    EditText etQualificationTitle;
    Button addDetailsBtn, addAnotherBtn;
    ImageView backBtn;
    Spinner spinner;
    RecyclerView added_info_recycle_view;
    private TextView tvFileName;
    private LinearLayout llUploadFile;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    Qualifications qualifications;
    String userID,  fileUriAccessToken, selectedFileName;
    Uri selectedFileUri;
    ArrayList<Qualifications> qualificationsArrayList;
    private AddedQualificationInfoRecyclerViewAdapter addedInfoDetailsRecycleViewAdapter;
    private int updateIndex = 0;
    private int selectedPosition = 0;
    String qualificationID = "";

    private final static int PICK_FILE = 1212;
    String[] qualificationTypes = {"Select Type", "PhD", "Masters", "Bachelors", "Higher National Diploma", "Diploma"};
    String qualificationType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qualification_details);

        tvFileName = findViewById(R.id.tvFileName);
        llUploadFile = findViewById(R.id.llUploadFile);
        addDetailsView = findViewById(R.id.addDetailsView);
        addOrNextView = findViewById(R.id.addOrNextView);
        etQualificationTitle = findViewById(R.id.etQualificationTitle);
        addDetailsBtn = findViewById(R.id.addDetailsBtn);
        addAnotherBtn = findViewById(R.id.addAnotherBtn);
        added_info_recycle_view = findViewById(R.id.added_info_recycle_view);
        backBtn = findViewById(R.id.backBtn);
        spinner = findViewById(R.id.spinner);

        qualificationsArrayList = new ArrayList<>();
        qualifications = new Qualifications();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        userID = sharedPreferences.getString(My_ID, "");

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, qualificationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                qualificationType = qualificationTypes[i];
                selectedPosition = adapter.getPosition(qualificationType);
                System.out.println("pos--- " + selectedPosition);
                Toast.makeText(AddQualificationDetailsActivity.this, qualificationTypes[i], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        llUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_FILE);
            }
        });

        if (isEdit){
            readData();
            addOrNextView.setVisibility(View.VISIBLE);
            addDetailsView.setVisibility(View.GONE);
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

        qualificationsArrayList.clear();
        if (qualificationType.equals("Select Type")){
            Toast.makeText(this, "Please select type", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(tvFileName.getText().toString())) {
            Toast.makeText(AddQualificationDetailsActivity.this, "Please choose a file", Toast.LENGTH_SHORT).show();
        } else {
            qualifications.setQualificationTitle(String.valueOf(etQualificationTitle.getText()));
            qualifications.setQualificationType(qualificationType);
            qualifications.setQualificationFile(fileUriAccessToken);
            qualifications.setSelectedITemPosition(selectedPosition);
            System.out.println("pos--- 1" + selectedPosition);

            DatabaseReference databaseReference = mDatabase.child("Qualifications").child(userID);
            qualificationID = databaseReference.push().getKey();
            qualifications.setQualificationId(qualificationID);
            System.out.println("quali id--- " + qualificationID);
            assert qualificationID != null;

            qualificationsArrayList.add(qualifications);
            databaseReference.child(qualificationID).setValue(qualifications)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                etQualificationTitle.setText("");
                                tvFileName.setText("");

                                Toast.makeText(AddQualificationDetailsActivity.this,
                                        "Successfully Added",Toast.LENGTH_SHORT).show();

                                addDetailsView.setVisibility(View.GONE);
                                addOrNextView.setVisibility(View.VISIBLE);

                                onBackPressed();

                            }else {

                                Toast.makeText(AddQualificationDetailsActivity.this,
                                        "Failed to Add Data",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }

    public void getAddedInfoDetails() {

        added_info_recycle_view.setVisibility(View.VISIBLE);

        addedInfoDetailsRecycleViewAdapter = new AddedQualificationInfoRecyclerViewAdapter(this, qualificationsArrayList);
        added_info_recycle_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        added_info_recycle_view.setAdapter(addedInfoDetailsRecycleViewAdapter);
    }

    private void readData() {

        if (userID != null && !userID.equals("")){
            DatabaseReference databaseReference = mDatabase.child("Qualifications").child(userID);
            progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Loading Data...");

            progressDialog.show();

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        qualificationsArrayList.add(ds.getValue(Qualifications.class));
                        getAddedInfoDetails();
                        progressDialog.dismiss();
                    }
                    System.out.println("pub size------- " + qualificationsArrayList.size());

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

    private void updateData(String qualificationId) {

        HashMap<String, Object> qualifications = new HashMap<>();
        qualifications.put("qualificationTitle", String.valueOf(etQualificationTitle.getText()));
        qualifications.put("qualificationType", qualificationType);

        if (fileUriAccessToken != null && !fileUriAccessToken.equals("")){
            qualifications.put("qualificationFile", fileUriAccessToken);
        } else {
            qualifications.put("qualificationFile", tvFileName.getText().toString());
        }


        DatabaseReference databaseReference = mDatabase.child("Qualifications").child(userID);
        databaseReference.child(qualificationId).updateChildren(qualifications)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            etQualificationTitle.setText("");
                            tvFileName.setText("");

                            Toast.makeText(AddQualificationDetailsActivity.this,
                                    "Successfully Updated",Toast.LENGTH_SHORT).show();

                            onBackPressed();
                        }else {

                            Toast.makeText(AddQualificationDetailsActivity.this,
                                    "Failed to Update Data",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void editDetails(int position, ArrayList<Qualifications> bulkList, String qualificationId){
        added_info_recycle_view.setVisibility(View.GONE);
        addDetailsView.setVisibility(View.VISIBLE);
        addOrNextView.setVisibility(View.GONE);
        String value = "Edit Qualification Details";
        addDetailsBtn.setText(value);
        isEdit = true;
        updateIndex = position;

        etQualificationTitle.setText(bulkList.get(updateIndex).getQualificationTitle());
        spinner.setSelection(bulkList.get(updateIndex).getSelectedITemPosition());
        tvFileName.setText(bulkList.get(updateIndex).getQualificationFile());

        addDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(qualificationId);
            }
        });
    }

    public void removeItem(String qualificationId){
        DatabaseReference databaseReference = mDatabase.child("Qualifications").child(userID);
        databaseReference.child(qualificationId).removeValue();

        Toast.makeText(AddQualificationDetailsActivity.this,
                "Successfully Deleted",Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1212:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    selectedFileUri = data.getData();

                    File file = null;
                    try {
                        file = readContentToFile(selectedFileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    selectedFileName = file.getName();
                    System.out.println("file name------- " + selectedFileName);

                    tvFileName.setText(selectedFileName);

                    updateFileToStorage();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File readContentToFile(Uri uri) throws IOException {
        final File file = new File(getCacheDir(), getDisplayName(uri));
        try (
                final InputStream in = getContentResolver().openInputStream(uri);
                final OutputStream out = new FileOutputStream(file, false);
        ) {
            byte[] buffer = new byte[1024];
            for (int len; (len = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, len);
            }
            return file;
        }
    }

    private String getDisplayName(Uri uri) {
        final String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        try (
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        ) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        }
        // If the display name is not found for any reason, use the Uri path as a fallback.
        Log.w(TAG, "Couldn't determine DISPLAY_NAME for Uri.  Falling back to Uri path: " + uri.getPath());
        return uri.getPath();
    }


    private void updateFileToStorage() {

        StorageReference fileRef = storageReference.child(userID).child(selectedFileName);

        UploadTask uploadTask = fileRef.putFile(selectedFileUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fileUriAccessToken = uri.toString();
                        System.out.println("file uri-- " + fileUriAccessToken);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI get Failed", Toast.LENGTH_SHORT).show();
                    }

                });
                Toast.makeText(getApplicationContext(), "File is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "File Not Uploaded", Toast.LENGTH_SHORT).show();

            }
        });
    }
}