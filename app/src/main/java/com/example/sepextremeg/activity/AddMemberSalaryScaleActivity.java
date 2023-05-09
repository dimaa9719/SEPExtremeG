package com.example.sepextremeg.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.Fragments.ProfileFragment.isEdit;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.sepextremeg.model.SalaryScale;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddMemberSalaryScaleActivity extends AppCompatActivity {

    LinearLayout addDetailsView, ll_invoice_pdf;
    EditText etTaxRate,etDeductionRate, etAllowances,etAResearchAllowancePercentage, etBasicSalary,etSalaryCpde;
    Button addDetailsBtn, generatePaySlipBtn;
    ImageView backBtn;
    TextView tvInvoiceId,tvTodayDate,tvEmployeeName,tvJobTitle,tvNetSalary,tvAllowance,tvRaPercentage,tvDeductionRate,
            tvTaxRate;

    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    SalaryScale salaryScale;
    String userID, employeeName, employeePosition;
    ArrayList<SalaryScale> salaryScaleArrayList;
    private Bitmap bitmap;
    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_salary_scale);

        backBtn = findViewById(R.id.backBtn);
        addDetailsView = findViewById(R.id.addDetailsView);
        addDetailsBtn = findViewById(R.id.addDetailsBtn);
        etTaxRate = findViewById(R.id.etTaxRate);
        etDeductionRate = findViewById(R.id.etDeductionRate);
        etAllowances = findViewById(R.id.etAllowances);
        etAResearchAllowancePercentage = findViewById(R.id.etAResearchAllowancePercentage);
        etBasicSalary = findViewById(R.id.etBasicSalary);
        etSalaryCpde = findViewById(R.id.etSalaryCpde);

        //invoice layout views
        ll_invoice_pdf = findViewById(R.id.ll_invoice_pdf);
        tvInvoiceId = findViewById(R.id.tvInvoiceId);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        tvEmployeeName = findViewById(R.id.tvEmployeeName);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvNetSalary = findViewById(R.id.tvNetSalary);
        tvAllowance = findViewById(R.id.tvAllowance);
        tvRaPercentage = findViewById(R.id.tvRaPercentage);
        tvDeductionRate = findViewById(R.id.tvDeductionRate);
        tvTaxRate = findViewById(R.id.tvTaxRate);
        generatePaySlipBtn = findViewById(R.id.generatePaySlipBtn);

        userID = getIntent().getStringExtra("MemId");
        tvEmployeeName.setText(getIntent().getStringExtra("MemName"));

        salaryScale = new SalaryScale();
        salaryScaleArrayList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (!isEdit){
            readData();
            addDetailsBtn.setText("Update");
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

        if (date.equals("10")){
            generatePaySlipBtn.setVisibility(View.VISIBLE);
            addDetailsView.setVisibility(View.GONE);
            ll_invoice_pdf.setVisibility(View.VISIBLE);
        }

        if (generatePaySlipBtn != null){
            generatePaySlipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generatePaySlip();

                    //send email
                    Intent i = new Intent(Intent.ACTION_SENDTO);
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"abc@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Your Monthly Payment Slip");
                    i.putExtra(Intent.EXTRA_TEXT   , "Hi! Please view your monthly invoice attached here. \n Thank you, Regards");
                    i.setData(Uri.parse("mailto:"));
                    if (i.resolveActivity(getPackageManager()) != null) {
                        startActivity(i);
                    } else {
                        Toast.makeText(AddMemberSalaryScaleActivity.this, "There is no application that support this action",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void insertData(){

        salaryScaleArrayList.clear();
        salaryScale.setSalaryCode(String.valueOf(etSalaryCpde.getText()));
        salaryScale.setBasicSalary(String.valueOf(etBasicSalary.getText()));
        salaryScale.setResearchAllowancePercentage(String.valueOf(etAResearchAllowancePercentage.getText()));
        salaryScale.setAllowances(String.valueOf(etAllowances.getText()));
        salaryScale.setDeductionRate(String.valueOf(etDeductionRate.getText()));
        salaryScale.setTaxRate(String.valueOf(etTaxRate.getText()));

        DatabaseReference databaseReference = mDatabase.child("SalaryScale").child(userID);

        salaryScaleArrayList.add(salaryScale);
        databaseReference.setValue(salaryScale)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            etSalaryCpde.setText("");
                            etBasicSalary.setText("");
                            etAResearchAllowancePercentage.setText("");
                            etAllowances.setText("");
                            etDeductionRate.setText("");
                            etTaxRate.setText("");

                            Toast.makeText(AddMemberSalaryScaleActivity.this,
                                    "Successfully Added",Toast.LENGTH_SHORT).show();

                            onBackPressed();

                        }else {

                            Toast.makeText(AddMemberSalaryScaleActivity.this,
                                    "Failed to Add Data",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void updateData() {

        HashMap<String, Object> salary = new HashMap<>();
        salary.put("salaryCode", String.valueOf(etSalaryCpde.getText()));
        salary.put("basicSalary", String.valueOf(etBasicSalary.getText()));
        salary.put("researchAllowancePercentage", String.valueOf(etAResearchAllowancePercentage.getText()));
        salary.put("allowances", String.valueOf(etAllowances.getText()));
        salary.put("deductionRate", String.valueOf(etDeductionRate.getText()));
        salary.put("taxRate", String.valueOf(etTaxRate.getText()));

        mDatabase.child("SalaryScale").child(userID).updateChildren(salary)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){

                            etSalaryCpde.setText("");
                            etBasicSalary.setText("");
                            etAResearchAllowancePercentage.setText("");
                            etAllowances.setText("");
                            etDeductionRate.setText("");
                            etTaxRate.setText("");

                            Toast.makeText(AddMemberSalaryScaleActivity.this,
                                    "Successfully Updated",Toast.LENGTH_SHORT).show();

                            onBackPressed();
                        }else {

                            Toast.makeText(AddMemberSalaryScaleActivity.this,
                                    "Failed to Update Data",Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void readData() {

        if (userID != null && !userID.equals("")){
            DatabaseReference userNameRef = mDatabase.child("SalaryScale").child(userID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        // Get Post object and use the values to update the UI
                        SalaryScale salaryScale = dataSnapshot.getValue(SalaryScale.class);
                        assert salaryScale != null;
                        etSalaryCpde.setText(salaryScale.getSalaryCode());
                        etBasicSalary.setText(salaryScale.getBasicSalary());
                        etAResearchAllowancePercentage.setText(salaryScale.getResearchAllowancePercentage());
                        etAllowances.setText(salaryScale.getAllowances());
                        etDeductionRate.setText(salaryScale.getDeductionRate());
                        etTaxRate.setText(salaryScale.getTaxRate());

                        //set data to payslip
                        tvInvoiceId.setText(salaryScale.getSalaryCode());
                        tvNetSalary.setText(salaryScale.getBasicSalary());
                        tvAllowance.setText(salaryScale.getResearchAllowancePercentage());
                        tvRaPercentage.setText(salaryScale.getAllowances());
                        tvDeductionRate.setText(salaryScale.getDeductionRate());
                        tvTaxRate.setText(salaryScale.getTaxRate());
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

    public void generatePaySlip(){

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        tvTodayDate.setText("Create At: " + currentDateTimeString);

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
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getIntent().getStringExtra("MemName") + "PaySlip" + newDate + ".pdf");

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