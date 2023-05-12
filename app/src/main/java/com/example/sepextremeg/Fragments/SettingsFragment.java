package com.example.sepextremeg.Fragments;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_ID;
import static com.example.sepextremeg.activity.LoginActivity.My_Name;
import static com.example.sepextremeg.activity.LoginActivity.My_ProfilePic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.AddProfileDetailsActivity;
import com.example.sepextremeg.activity.CreateUserActivity;
import com.example.sepextremeg.activity.LoginActivity;
import com.example.sepextremeg.activity.ViewAllEmployeeSalaryDetailsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    CircleImageView imageView;
    TextView userName;
    Button signOutBtn, createUserBtn, viewMonthlySalaryReportBtn;
    public static boolean isEdit = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        imageView = (CircleImageView) view.findViewById(R.id.ProfileImageView);
        userName = (TextView) view.findViewById(R.id.UserNameTxt);
        signOutBtn = (Button) view.findViewById(R.id.SignOutBtn);
        createUserBtn = (Button) view.findViewById(R.id.createUserBtn);
        viewMonthlySalaryReportBtn = (Button) view.findViewById(R.id.viewMonthlySalaryReportBtn);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(AUTHENTICATION, MODE_PRIVATE);
        String un = sharedPreferences.getString(My_Name, "");
        userName.setText(un);

        String image = sharedPreferences.getString(My_ProfilePic, "");
        Picasso.get().load(image).into(imageView);

        //implementing onClickListener to make the user signOut
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //User Signout
                FirebaseAuth.getInstance().signOut();

                //Redirecting to starting Activity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), CreateUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        viewMonthlySalaryReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ViewAllEmployeeSalaryDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

}