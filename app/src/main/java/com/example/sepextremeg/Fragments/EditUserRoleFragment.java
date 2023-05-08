package com.example.sepextremeg.Fragments;

import static com.example.sepextremeg.Fragments.StaffFragment.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.AddMemberSalaryScaleActivity;
import com.example.sepextremeg.activity.AddProfileDetailsActivity;
import com.example.sepextremeg.activity.CreateUserActivity;
import com.example.sepextremeg.activity.StaffMemberActivity;
import com.example.sepextremeg.adapters.StaffMemberRecycleViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditUserRoleFragment extends androidx.fragment.app.DialogFragment {

    Context mcontext;
    String itemId;
    Spinner spinner;
    Button btnUpdate;

    DatabaseReference mDatabase;

    String[] roles = {"Select role", "Dean", "Head", "Lecturer", "Assistant Lecturer", "Instructor"};
    String userRole = "";

    public EditUserRoleFragment(Context mContext, String id) {

        this.mcontext = mContext;
        this.itemId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_edit_role, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        spinner = itemView.findViewById(R.id.spinner);
        btnUpdate = itemView.findViewById(R.id.btnUpdate);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapter = new ArrayAdapter(mcontext, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                userRole = roles[i];
                Toast.makeText(mcontext, roles[i], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userRole.equals("Select role")){
                    Toast.makeText(mcontext, "Please select role", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.child("users").child("Staff").child(itemId).child("role").setValue(userRole)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        ((StaffMemberRecycleViewAdapter) recyclerView.getAdapter()).notifyDataSetChanged();

                                        Toast.makeText(requireActivity(),"User role updated",Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getContext(), AddMemberSalaryScaleActivity.class);
                                        intent.putExtra("MemId", itemId);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }else {
                                        Toast.makeText(requireActivity(),"Failed to update",Toast.LENGTH_SHORT).show();

                                    }
                                    dismiss();
                                }
                            });

                    mDatabase.child("AllUsers").child(itemId).child("role").setValue(userRole)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //suceess
                                    }else {
                                       //failed
                                    }

                                }
                            });

                    mDatabase.child("profile").child(itemId).child("jobTitle").setValue(userRole)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //suceess
                                    }else {
                                        //failed
                                    }

                                }
                            });
                }
            }
        });

        return itemView;
    }
}
