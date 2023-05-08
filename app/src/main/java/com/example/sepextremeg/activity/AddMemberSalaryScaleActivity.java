package com.example.sepextremeg.activity;

import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.Fragments.ProfileFragment.isEdit;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.HashMap;

public class AddMemberSalaryScaleActivity extends AppCompatActivity {

    LinearLayout addDetailsView;
    EditText etTaxRate,etDeductionRate, etAllowances,etAResearchAllowancePercentage, etBasicSalary,etSalaryCpde;
    Button addDetailsBtn;
    ImageView backBtn;

    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    SalaryScale salaryScale;
    String userID;
    ArrayList<SalaryScale> salaryScaleArrayList;

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

        userID = getIntent().getStringExtra("MemId");

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
}