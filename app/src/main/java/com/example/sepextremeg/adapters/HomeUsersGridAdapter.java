package com.example.sepextremeg.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.AddQualificationDetailsActivity;
import com.example.sepextremeg.activity.StaffMemberActivity;
import com.example.sepextremeg.model.StaffModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeUsersGridAdapter extends ArrayAdapter<StaffModel> {

    public HomeUsersGridAdapter(@NonNull Context context, @NonNull List<StaffModel> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View  itemView = convertView;
        if ( itemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_home, parent, false);
        }

        StaffModel staffModel = getItem(position);
        TextView name, tv_provider_job, btnCreate;
        ImageView image;
        CardView card_view_type;
        LinearLayout llRatingView;

        name = itemView.findViewById(R.id.tv_provider_name);
        tv_provider_job = itemView.findViewById(R.id.tv_provider_job);
        btnCreate = itemView.findViewById(R.id.btnCreate);
        image = itemView.findViewById(R.id.emp_image);
        card_view_type = itemView.findViewById(R.id.card_view_type);

        name.setText(staffModel.getName());
        tv_provider_job.setText(staffModel.getRole());

        if (staffModel.getProUrl() != null && !staffModel.getProUrl().equals("")){
            Picasso.get().load(staffModel.getProUrl()).into(image);
            System.out.println("url--------- " + staffModel.getProUrl());
        } else {
            //load default pic
        }


//        Glide.with(getContext())
//                .load(staffModel.getProUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.default_pic)
//                .circleCrop()
//                .into(image);

        card_view_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StaffMemberActivity.class);
                intent.putExtra("MemId", staffModel.getId());
                intent.putExtra("MemName", staffModel.getName());
                intent.putExtra("MemImage", staffModel.getProUrl());
                intent.putExtra("MemEmail", staffModel.getEmail());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });

        return  itemView;
    }
}
