package com.example.sepextremeg.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.adapters.HomeVerticalAdapter;
import com.example.sepextremeg.adapters.StaffMemberRecycleViewAdapter;
import com.example.sepextremeg.model.Category;
import com.example.sepextremeg.model.StaffModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StaffFragment extends Fragment {

    public static RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    DatabaseReference databaseReference;
    ArrayList<StaffModel> staffModelArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflating the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff, container, false);

//        assigning the Recyclerview to display all created classes
        recyclerView = (RecyclerView) view.findViewById(R.id.rvStaffMembers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.hasFixedSize();

        //database path
        databaseReference = FirebaseDatabase.getInstance().getReference();

        shimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        staffModelArrayList = new ArrayList<>();

        loadUsersList();

        return view;
    }

    private void loadUsersList(){
        staffModelArrayList.clear();
        try {
            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference().child("users").child("Staff");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String id = (String)
                                    snapshot.child("id").getValue();
                            String role =  (String)
                                    snapshot.child("role").getValue();
                            String name =  (String)
                                    snapshot.child("name").getValue();
                            String image =  (String)
                                    snapshot.child("profilePic").getValue();

                            staffModelArrayList.add(new StaffModel(id, name, role, image));
                        }

                        System.out.println("staff list " + staffModelArrayList.size());
                        StaffMemberRecycleViewAdapter adapter = new StaffMemberRecycleViewAdapter(getContext(), staffModelArrayList);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addValueEventListener(eventListener);

            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.hideShimmer();

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }
}