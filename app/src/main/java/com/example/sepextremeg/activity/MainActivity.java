package com.example.sepextremeg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sepextremeg.Fragments.AttendanceFragment;
import com.example.sepextremeg.Fragments.CreateClassFragment;
import com.example.sepextremeg.Fragments.ProfileFragment;
import com.example.sepextremeg.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {

                    //Assigining Fragment as Null
                    Fragment fragment = null;
                    switch (item.getItemId()) {

                        //Shows the Appropriate Fragment by using id as address
                        case R.id.HomeMenu:
                            fragment = new CreateClassFragment();
                            break;

                        case R.id.InvoiceMenu:
                            fragment=new AttendanceFragment();
                            break;

                        case R.id.ProfileMenu:
                            fragment = new ProfileFragment();
                            break;

                    }
                    //Sets the selected Fragment into the Framelayout
                    getSupportFragmentManager().beginTransaction().replace(R.id.TeacherFragmentContainer, fragment).commit();
                    return true;
                }
            };
}