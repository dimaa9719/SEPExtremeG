package com.example.sepextremeg.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sepextremeg.Fragments.EditUserRoleFragment;
import com.example.sepextremeg.R;
import com.example.sepextremeg.model.StaffModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StaffMemberRecycleViewAdapter extends RecyclerView.Adapter<StaffMemberRecycleViewAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<StaffModel> list;

    public StaffMemberRecycleViewAdapter(Context context, ArrayList<StaffModel> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_mem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        System.out.println("id " + list.get(position).getId());
        holder.name.setText(list.get(position).getName());
        holder.job.setText(list.get(position).getRole());

        Glide.with(mContext)
                .load(list.get(position).getProUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_pic)
                .centerCrop()
                .into(holder.img_provider);

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("userrrid",list.get(position).getId());
                EditUserRoleFragment dialogFragment=new EditUserRoleFragment(mContext, list.get(position).getId());
                dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(), "My  Fragment");
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, job;
        ImageView img_provider;
        Button btnUpdate, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_provider_name);
            job = itemView.findViewById(R.id.tv_provider_job);
            img_provider = itemView.findViewById(R.id.emp_image);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}