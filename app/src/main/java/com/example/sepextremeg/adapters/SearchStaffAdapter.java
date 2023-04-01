package com.example.sepextremeg.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.model.Profile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class SearchStaffAdapter extends FirebaseRecyclerAdapter<Profile, SearchStaffAdapter.Viewholder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SearchStaffAdapter(@NonNull FirebaseRecyclerOptions<Profile> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Profile model) {
        //Getting data from database using model class and assigning
        holder.tvName.setText(model.getFullName());
        holder.tvFaculty.setText(model.getFacultyName() + " Department: ");
        holder.tvJob.setText(model.getJobTitle());
    }

    @NonNull
    @Override
    public SearchStaffAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    static class Viewholder extends RecyclerView.ViewHolder {

        TextView tvName, tvJob, tvFaculty;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            //assigning the address of the materials
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvJob = (TextView) itemView.findViewById(R.id.tvJob);
            tvFaculty = (TextView) itemView.findViewById(R.id.tvFaculty);
        }
    }

}


