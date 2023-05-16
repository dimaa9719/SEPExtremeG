package com.example.sepextremeg.Fragments;

import static android.content.ContentValues.TAG;
import static com.example.sepextremeg.activity.LoginActivity.AUTHENTICATION;
import static com.example.sepextremeg.activity.LoginActivity.My_Name;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.adapters.HomeHorizontalAdapter;
import com.example.sepextremeg.adapters.HomeUsersGridAdapter;
import com.example.sepextremeg.adapters.HomeVerticalAdapter;
import com.example.sepextremeg.adapters.SearchStaffAdapter;
import com.example.sepextremeg.model.Category;
import com.example.sepextremeg.model.Profile;
import com.example.sepextremeg.model.Qualifications;
import com.example.sepextremeg.model.StaffModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AdminHomeFragment extends Fragment {

    SearchStaffAdapter adapter;
    RecyclerView search_results_recycle_view, home_recycle_view;
    GridView idGvUrses;
    SearchView searchView;
    TextView tvNoResult;
    ProgressDialog progressDialog;
    ShimmerFrameLayout shimmerFrameLayout;
    DatabaseReference databaseReference;
    ArrayList<StaffModel> staffModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // assigning the Recyclerview to display all created classes
        search_results_recycle_view = mView.findViewById(R.id.search_results_recycle_view);
//        idGvUrses = mView.findViewById(R.id.idGvUrses);
        home_recycle_view = mView.findViewById(R.id.home_recycle_view);
        search_results_recycle_view.setLayoutManager(new LinearLayoutManager(getContext()));
        search_results_recycle_view.hasFixedSize();

        home_recycle_view.setLayoutManager(new LinearLayoutManager(getContext()));
        home_recycle_view.hasFixedSize();

        //database path
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //setting the adapter to the recyclerview
        search_results_recycle_view.setAdapter(adapter);

        //assigning the addresses of the android materials
        searchView = mView.findViewById(R.id.searchView);

        shimmerFrameLayout = (ShimmerFrameLayout) mView.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        SharedPreferences prefs = requireActivity().getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE);

        TextView usernameText = mView.findViewById(R.id.tvHeader);
        String userName = "Hi, " + prefs.getString(My_Name, "User");
        usernameText.setText(userName);

        //Implementing onClickListener

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                search_results_recycle_view.setVisibility(View.GONE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("searchhh", "resultt");
                //Firebase Recycler Options to get the data form firebase database using model class and reference

                if (query.length() > 0) {
                    searchUser(query);
                } else {
                    search_results_recycle_view.setVisibility(View.GONE);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUser(newText);
                return false;
            }
        });

        loadHomeCategories();

        return mView;
    }

    private void loadHomeCategories() {
        try {
            System.out.println("loadingggggggggggggg");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child("Staff");
            progressDialog = new ProgressDialog(getContext());

            progressDialog.setMessage("Loading Data...");

            progressDialog.show();

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        staffModelArrayList.add(ds.getValue(StaffModel.class));
                        progressDialog.dismiss();
                    }

                    if (getContext() != null) {
                        HomeHorizontalAdapter adapter = new HomeHorizontalAdapter(getContext(), staffModelArrayList);
                        home_recycle_view.setHasFixedSize(true);
                        home_recycle_view.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        home_recycle_view.setAdapter(adapter);

//                        HomeUsersGridAdapter adapter = new HomeUsersGridAdapter(getContext(), staffModelArrayList);
//                        idGvUrses.setAdapter(adapter);
                        System.out.println("pub size------- " + staffModelArrayList.size());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    progressDialog.dismiss();
                }
            };
            databaseReference.addValueEventListener(eventListener);

            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.hideShimmer();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void searchUser(String str) {

        FirebaseRecyclerOptions<Profile> options =
                new FirebaseRecyclerOptions.Builder<Profile>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("profile").orderByChild("facultyName").startAt(str).endAt(str + "\uf8ff"), Profile.class)
                        .build();

        System.out.println("searchingg " + options);

        search_results_recycle_view.setVisibility(View.VISIBLE);

        adapter = new SearchStaffAdapter(options);
        adapter.startListening();

        //setting the adapter to the recyclerview
        search_results_recycle_view.setAdapter(adapter);
    }
}