package com.example.sepextremeg.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

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
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sepextremeg.R;
import com.example.sepextremeg.adapters.AddedSalaryInfoRecyclerViewAdapter;
import com.example.sepextremeg.adapters.HomeUsersGridAdapter;
import com.example.sepextremeg.model.Profile;
import com.example.sepextremeg.model.Publications;
import com.example.sepextremeg.model.SalaryScale;
import com.example.sepextremeg.model.StaffModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewAllEmployeeSalaryDetailsActivity extends AppCompatActivity {

    LinearLayout ll_invoice_pdf;
    RecyclerView added_info_recycle_view, added_info_recycle_view_one;
    Button downloadPaySlip;
    ImageView backBtn;
    TextView  tvDate;

    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private AddedSalaryInfoRecyclerViewAdapter addedSalaryInfoRecyclerViewAdapter;
    SalaryScale salaryScale;
    String userID, employeeName, employeeServiceNo;
    ArrayList<SalaryScale> salaryScaleArrayList;
    ArrayList<StaffModel> staffModelArrayList = new ArrayList<>();
    private Bitmap bitmap;
    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_employee_salary_details);

        //invoice layout views
        backBtn = findViewById(R.id.backBtn);
        ll_invoice_pdf = findViewById(R.id.ll_invoice_pdf);
        tvDate = findViewById(R.id.tvDate);
        downloadPaySlip = findViewById(R.id.downloadPaySlip);
        added_info_recycle_view = findViewById(R.id.added_info_recycle_view);
        added_info_recycle_view_one = findViewById(R.id.added_info_recycle_view_one);

        salaryScale = new SalaryScale();
        salaryScaleArrayList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        readData();

        if (checkPermission()){

        }else {
            requestPermission();
        }

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        System.out.println("formatted date => " + formattedDate);

        String[] result = formattedDate.split("-");
        String date = result[0];


        downloadPaySlip.setVisibility(View.VISIBLE);

        if (downloadPaySlip != null){
            downloadPaySlip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generatePaySlip();
                }
            });
        }
    }

    private void readData() {

            DatabaseReference userNameRef = mDatabase.child("SalaryScale");

            progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Loading Data...");

            progressDialog.show();

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        SalaryScale salary = dataSnapshot.getValue(SalaryScale.class);
                        assert salary != null;

                        salaryScaleArrayList.add(ds.getValue(SalaryScale.class));
                        progressDialog.dismiss();
                        getAddedInfoDetails();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addValueEventListener(eventListener);

    }

    public void getAddedInfoDetails() {

        added_info_recycle_view.setVisibility(View.VISIBLE);

        addedSalaryInfoRecyclerViewAdapter = new AddedSalaryInfoRecyclerViewAdapter(this, salaryScaleArrayList);
        added_info_recycle_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        added_info_recycle_view.setAdapter(addedSalaryInfoRecyclerViewAdapter);

        added_info_recycle_view_one.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        added_info_recycle_view_one.setAdapter(addedSalaryInfoRecyclerViewAdapter);

    }

    public void generatePaySlip(){

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        tvDate.setText(currentDateTimeString);

        bitmap = loadBitmap(ll_invoice_pdf, ll_invoice_pdf.getWidth(), ll_invoice_pdf.getHeight());
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
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "OverallMonthlyPaySlip" + newDate + ".pdf");

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