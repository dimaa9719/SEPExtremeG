package com.example.sepextremeg.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sepextremeg.R;
import com.example.sepextremeg.model.StaffModel;

import java.util.List;

public class HomeHorizontalAdapter extends RecyclerView.Adapter<HomeHorizontalAdapter.HorizontalVieHolder> {

    private final Context context;
    private final List<StaffModel> arrayList;
    private int page = 0;
    private String title;
    View view;
    public static final String TV = "DeviceTypeRuntimeCheck";

    public HomeHorizontalAdapter(Context context, List<StaffModel> arrayList, String title) {
        this.context = context;
        this.arrayList = arrayList;
        this.title = title;
    }

    @NonNull
    @Override
    public HorizontalVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new HorizontalVieHolder(view);
    }

    @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull HorizontalVieHolder holder, int position) {

        holder.name.setText(arrayList.get(position).getName());
        holder.tv_provider_job.setText(arrayList.get(position).getRole());

        Glide.with(context)
                .load(arrayList.get(position).getProUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_pic)
                .circleCrop()
                .into(holder.image);

//        holder.btnCreate.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ServiceProviderProfileActivity.class);
//            intent.putExtra("providerId", String.valueOf(arrayList.get(position).getId()));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    static class HorizontalVieHolder extends RecyclerView.ViewHolder {
        TextView name, tv_provider_job, btnCreate;
        ImageView image;
        CardView card_view_type;
        LinearLayout llRatingView;

        HorizontalVieHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_provider_name);
            tv_provider_job = itemView.findViewById(R.id.tv_provider_job);
            btnCreate = itemView.findViewById(R.id.btnCreate);
            image = itemView.findViewById(R.id.emp_image);
            card_view_type = itemView.findViewById(R.id.card_view_type);
            llRatingView = itemView.findViewById(R.id.llRatingView);
        }
    }
}
