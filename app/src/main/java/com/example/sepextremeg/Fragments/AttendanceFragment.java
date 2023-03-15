package com.example.sepextremeg.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sepextremeg.R;
import com.google.firebase.database.DatabaseReference;

public class AttendanceFragment extends Fragment {

//    AttendanceClassAdapter adapter;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    EditText searchBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_attendance, container, false);

        //Assigning the Recyclerview to display all classes
//        recyclerView = (RecyclerView) view.findViewById(R.id.ClassesRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.hasFixedSize();
//
//        searchBox=(EditText)view.findViewById(R.id.SearchBox);
//
//        databaseReference= FirebaseDatabase.getInstance().getReference().child("AllClasses");
//        //Firebase Recycler Options to get the data form firebase database using model class and reference
//        FirebaseRecyclerOptions<Model> options =
//                new FirebaseRecyclerOptions.Builder<Model>()
//                        .setQuery(databaseReference, Model.class)
//                        .build();
//
//        adapter = new AttendanceClassAdapter(options);
//
//        //setting the adapter to the recyclerview
//        recyclerView.setAdapter(adapter);
//
//
//        //Implementing the search option
//        searchBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                //Getting the data from EditText
//                String searchtxt = searchBox.getText().toString().trim();
//                FirebaseRecyclerOptions<Model> options =
//                        new FirebaseRecyclerOptions.Builder<Model>()
//                                .setQuery(databaseReference.orderByChild("className").startAt(searchtxt).endAt(searchtxt + "\uf8ff"), Model.class)
//                                .build();
//
//                adapter = new AttendanceClassAdapter(options);
//                adapter.startListening();
//                recyclerView.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String searchtxt = searchBox.getText().toString().trim();
//                FirebaseRecyclerOptions<Model> options =
//                        new FirebaseRecyclerOptions.Builder<Model>()
//                                .setQuery(databaseReference.orderByChild("className").startAt(searchtxt).endAt(searchtxt + "\uf8ff"), Model.class)
//                                .build();
//
//                adapter = new AttendanceClassAdapter(options);
//                adapter.startListening();
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String charSequence = editable.toString();
//                FirebaseRecyclerOptions<Model> options =
//                        new FirebaseRecyclerOptions.Builder<Model>()
//                                .setQuery(databaseReference.orderByChild("className").startAt(charSequence).endAt(charSequence + "\uf8ff"), Model.class)
//                                .build();
//
//                adapter = new AttendanceClassAdapter(options);
//                adapter.startListening();
//                recyclerView.setAdapter(adapter);
//
//            }
//        });


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

//        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
//
//        adapter.startListening();
    }
}