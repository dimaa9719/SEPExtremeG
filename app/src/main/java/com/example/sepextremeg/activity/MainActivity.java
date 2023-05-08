package com.example.sepextremeg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.sepextremeg.Fragments.ProfileFragment;
import com.example.sepextremeg.Fragments.UserHomeFragment;
import com.example.sepextremeg.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning framelayout resource file to show appropriate fragment using address
        frameLayout = (FrameLayout) findViewById(R.id.TeacherFragmentContainer);
        //Assigining Bottomnavigaiton Menu
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.TeacherBottomNavigationView);
        Menu menuNav = bottomNavigationView.getMenu();
        //Setting the default fragment as HomeFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.TeacherFragmentContainer, new UserHomeFragment()).commit();
        //Calling the bottoNavigationMethod when we click on any menu item
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);

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
                            fragment = new UserHomeFragment();
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