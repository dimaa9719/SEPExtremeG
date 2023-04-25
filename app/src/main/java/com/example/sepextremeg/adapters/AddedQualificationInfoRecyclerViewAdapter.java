package com.example.sepextremeg.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.AddQualificationDetailsActivity;
import com.example.sepextremeg.model.Qualifications;

import java.util.ArrayList;

public class AddedQualificationInfoRecyclerViewAdapter extends RecyclerView.Adapter<AddedQualificationInfoRecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Qualifications> bulkList;
    private boolean isEditable;
    private String qualificationId;

    public AddedQualificationInfoRecyclerViewAdapter(Context activity, ArrayList<Qualifications> arrayList) {
        this.mContext = activity;
        this.bulkList = arrayList;
    }

    @NonNull
    @Override
    public AddedQualificationInfoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qualification_added_info, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.removeLayout.setVisibility(View.VISIBLE);
        holder.editLayout.setVisibility(View.VISIBLE);

        StringBuilder sb = new StringBuilder();
        sb.append(bulkList.get(position).getQualificationType()).append(" - ");
        sb.append(bulkList.get(position).getQualificationTitle()).append("\n");

        if (!bulkList.get(position).getQualificationFile().equals("")){
            holder.tvView.setVisibility(View.VISIBLE);
            holder.tvView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(bulkList.get(position).getQualificationFile()), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        mContext.startActivity(intent);
                    } catch (Exception e) {

                    }
                }
            });

        }

        holder.tvResult.setText(sb);

        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 qualificationId = bulkList.get(position).getQualificationId();
                                                 ((AddQualificationDetailsActivity) mContext).editDetails(position, bulkList, qualificationId);
                                             }
                                         }

        );

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qualificationId = bulkList.get(position).getQualificationId();
                removeAt(position, qualificationId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bulkList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvResult, tvView, tvEdit, tvDelete;
        private final LinearLayout removeLayout, editLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResult = itemView.findViewById(R.id.tvResult);
            tvView = itemView.findViewById(R.id.tvView);
            editLayout = itemView.findViewById(R.id.editLayout);
            removeLayout = itemView.findViewById(R.id.removeLayout);
            tvEdit = itemView.findViewById(R.id.tvEdit);
            tvDelete = itemView.findViewById(R.id.tvDelete);
        }
    }

    public void removeAt(int position, String publicationId) {
        ((AddQualificationDetailsActivity) mContext).removeItem(publicationId);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, bulkList.size());
    }
}
