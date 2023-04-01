package com.example.sepextremeg.activity;

import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.Fragments.ProfileFragment.isEdit;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sepextremeg.R;
import com.example.sepextremeg.model.Profile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProfileDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private Profile profile;
    private SharedPreferences sharedPreferences;
    private TextInputEditText etName, etDob, etNic, etMobile, etAddress, etHigherQualification,
            etWorkExp, etDatePub, etFaculty, etJobTitle;
    private TextView tvFileName;
    private LinearLayout llUploadFile;
    private RelativeLayout rlImageUpload;
    private CircleImageView profilePic;
    private Button addDetailsBtn;
    private ImageButton btnCam;
    private ProgressDialog dialog;
    String userID, fileUrl, ImageURIacessToken, selectedImgName, selectedImgPath, fileUriAccessToken, selectedFileName;
    Uri selectedImageUri, selectedFileUri;
    private final static int PICK_FILE = 1212;
    private final int GALLERY_RE_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_details);

        etName = findViewById(R.id.etName);
        etDob = findViewById(R.id.etDob);
        etNic = findViewById(R.id.etNic);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etHigherQualification = findViewById(R.id.etHigherQualification);
        etWorkExp = findViewById(R.id.etWorkExp);
        etDatePub = findViewById(R.id.etDatePub);
        etJobTitle = findViewById(R.id.etJobTitle);
        etFaculty = findViewById(R.id.etFaculty);
        tvFileName = findViewById(R.id.tvFileName);
        llUploadFile = findViewById(R.id.llUploadFile);
        addDetailsBtn = findViewById(R.id.addDetailsBtn);
        rlImageUpload = findViewById(R.id.rlImageUpload);
        profilePic = findViewById(R.id.profilePic);
        btnCam = findViewById(R.id.btnCam);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        profile = new Profile();

        sharedPreferences = getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        userID = sharedPreferences.getString(My_ID, "");

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
           addDetailsBtn.setText("Update");
           btnCam.setVisibility(View.GONE);
           addDetailsBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   updateData();
               }
           });

       } else {
           addDetailsBtn.setText("Add");
           addDetailsBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   insertData();
               }
           });

       }

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handlePermission();
            }
        });

    }

    private void handlePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_RE_CODE);
        } else {
            Intent iGallery = new Intent();
            iGallery.setType("image/*");
            iGallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(iGallery, "Select Picture"), GALLERY_RE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_RE_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            //  Show your own message here
                        } else {
                            showSettingsAlert();
                        }
                    } else {
                        Intent iGallery = new Intent();
                        iGallery.setType("image/*");
                        iGallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(iGallery, "Select Picture"), GALLERY_RE_CODE);
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openAppSettings(AddProfileDetailsActivity.this);
                    }
                });
        alertDialog.show();
    }

    public static void openAppSettings(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    private void updateData() {

        HashMap<String, Object> profile = new HashMap<>();
        profile.put("fullName", String.valueOf(etName.getText()));
        profile.put("dateOfBirth", String.valueOf(etDob.getText()));
        profile.put("nic", String.valueOf(etNic.getText()));
        profile.put("phone", String.valueOf(etMobile.getText()));
        profile.put("address", String.valueOf(etAddress.getText()));
        profile.put("qualification", String.valueOf(etHigherQualification.getText()));
        profile.put("workExp", String.valueOf(etWorkExp.getText()));
        profile.put("facultyName", String.valueOf(etFaculty.getText()));
        profile.put("jobTitle", String.valueOf(etJobTitle.getText()));
        profile.put("datePublished", String.valueOf(etDatePub.getText()));
        if (fileUriAccessToken != null && !fileUriAccessToken.equals("")){
            profile.put("publication", fileUriAccessToken);
        } else {
            profile.put("publication", String.valueOf(tvFileName.getText()));
        }

        mDatabase.child("profile").child(userID).updateChildren(profile)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){

                            etName.setText("");
                            etDob.setText("");
                            etDatePub.setText("");
                            etAddress.setText("");
                            etNic.setText("");
                            etMobile.setText("");
                            etWorkExp.setText("");
                            etHigherQualification.setText("");
                            etFaculty.setText("");
                            etJobTitle.setText("");
                            tvFileName.setText("");

                            Toast.makeText(AddProfileDetailsActivity.this,"Successfully Updated",Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(AddProfileDetailsActivity.this,"Failed to Update",Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void readData() {

        if (userID != null && !userID.equals("")){
            DatabaseReference userNameRef = mDatabase.child("profile").child(userID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        // Get Post object and use the values to update the UI
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        assert profile != null;
                        etName.setText(profile.getFullName());
                        etDob.setText(profile.getDateOfBirth());
                        etNic.setText(profile.getNic());
                        etMobile.setText(profile.getPhone());
                        etAddress.setText(profile.getAddress());
                        etWorkExp.setText(profile.getWorkExp());
                        etHigherQualification.setText(profile.getQualification());
                        etFaculty.setText(profile.getFacultyName());
                        etJobTitle.setText(profile.getJobTitle());
                        etDatePub.setText(profile.getDatePublished());
                        tvFileName.setText(profile.getPublication());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addValueEventListener(eventListener);

            DatabaseReference userProfileRef = mDatabase.child("AllUsers").child(userID);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        // Get Post object and use the values to update the UI

                        String image = dataSnapshot.child("profilepic").getValue(
                                String.class);

                        Picasso.get().load(image).into(profilePic);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userProfileRef.addValueEventListener(listener);
        }

    }

    private void insertData(){
        profile.setFullName(String.valueOf(etName.getText()));
        profile.setDateOfBirth(String.valueOf(etDob.getText()));
        profile.setNic(String.valueOf(etNic.getText()));
        profile.setPhone(String.valueOf(etMobile.getText()));
        profile.setAddress(String.valueOf(etAddress.getText()));
        profile.setQualification(String.valueOf(etHigherQualification.getText()));
        profile.setWorkExp(String.valueOf(etWorkExp.getText()));
        profile.setFacultyName(String.valueOf(etFaculty.getText()));
        profile.setJobTitle(String.valueOf(etJobTitle.getText()));
        profile.setPublication(fileUriAccessToken);
        profile.setDatePublished(String.valueOf(etDatePub.getText()));

        mDatabase.child("profile").child(userID).setValue(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            etName.setText("");
                            etDob.setText("");
                            etDatePub.setText("");
                            etAddress.setText("");
                            etNic.setText("");
                            etMobile.setText("");
                            etWorkExp.setText("");
                            etHigherQualification.setText("");
                            etFaculty.setText("");
                            etJobTitle.setText("");
                            tvFileName.setText("");
                            profilePic.setImageURI(null);

                            Toast.makeText(AddProfileDetailsActivity.this,"Successfully Added",Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(AddProfileDetailsActivity.this,"Failed to Add Data",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        mDatabase.child("AllUsers").child(userID).child("profilepic").setValue(ImageURIacessToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(AddProfileDetailsActivity.this,"Profile Picture Added",Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(AddProfileDetailsActivity.this,"Failed to Add Data",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        mDatabase.child("users").child("Staff").child(userID).child("profilepic").setValue(ImageURIacessToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(AddProfileDetailsActivity.this,"Profile Picture Added",Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(AddProfileDetailsActivity.this,"Failed to Add Data",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

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
            case 1000:
                if (resultCode == RESULT_OK) {
                    // Get the Image from data

                    selectedImageUri = data.getData();

                    File file = null;
                    try {
                        file = readContentToFile(selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    selectedImgName = file.getName();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImgPath = cursor.getString(columnIndex);
                    cursor.close();

                    // Set the Image in ImageView after decoding the String
                    profilePic.setImageURI(selectedImageUri);

                    System.out.println("image path------- " + selectedImgPath);
                    System.out.println("image name------- " + selectedImgName);

                    updateImageToStorage();
                }
                break;
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

    private void updateImageToStorage() {

        StorageReference imageref = storageReference.child(userID).child(selectedImgName);

        //Image compresesion
        Bitmap bitmap = null;
        try {
            if (null != selectedImageUri)
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        }
        byte[] data = byteArrayOutputStream.toByteArray();

        ///putting image to storage

        UploadTask uploadTask = imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageURIacessToken = uri.toString();
                        System.out.println("image uri-- " + ImageURIacessToken);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI get Failed", Toast.LENGTH_SHORT).show();
                    }

                });
                Toast.makeText(getApplicationContext(), "Image is Uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image Not Updated", Toast.LENGTH_SHORT).show();
            }
        });
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