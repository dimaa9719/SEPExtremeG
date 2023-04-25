package com.example.sepextremeg.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.activity.AddPublicationDetailsActivity;
import com.example.sepextremeg.model.Publications;

import java.util.ArrayList;

public class AddedInfoRecyclerViewAdapter extends RecyclerView.Adapter<AddedInfoRecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Publications> bulkList;
    private boolean isEditable;
    private String publicationId;

    public AddedInfoRecyclerViewAdapter(Context activity, ArrayList<Publications> arrayList) {
        this.mContext = activity;
        this.bulkList = arrayList;
    }

    @NonNull
    @Override
    public AddedInfoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_added_info, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.removeLayout.setVisibility(View.VISIBLE);
        holder.editLayout.setVisibility(View.VISIBLE);

        StringBuilder sb = new StringBuilder();
        sb.append(bulkList.get(position).getPubTitle()).append("\n");
        sb.append(bulkList.get(position).getPubType()).append("\n");
        sb.append(bulkList.get(position).getAuthorName()).append("\n");
        sb.append(bulkList.get(position).getOrganisation()).append("\n");
        sb.append(bulkList.get(position).getLanguage()).append("\n");
        sb.append(bulkList.get(position).getCitynCountry()).append("\n");
        sb.append(bulkList.get(position).getYearPublished()).append("\n");

        holder.tvResult.setText("Citation : " + sb);

        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 publicationId = bulkList.get(position).getPublicationId();
                                                 ((AddPublicationDetailsActivity) mContext).editDetails(position, bulkList, publicationId);
                                             }
                                         }

        );

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicationId = bulkList.get(position).getPublicationId();
                removeAt(position, publicationId);
            }
        });

        holder.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddPublicationDetailsActivity) mContext).downloadMyPublication(position, bulkList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bulkList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvResult, tvDownload, tvLogo, tvEdit, tvDelete;
        private final LinearLayout removeLayout, editLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResult = itemView.findViewById(R.id.tvResult);
            tvDownload = itemView.findViewById(R.id.tvDownload);
            tvLogo = itemView.findViewById(R.id.tvLogo);
            editLayout = itemView.findViewById(R.id.editLayout);
            removeLayout = itemView.findViewById(R.id.removeLayout);
            tvEdit = itemView.findViewById(R.id.tvEdit);
            tvDelete = itemView.findViewById(R.id.tvDelete);
        }
    }

    public void removeAt(int position, String publicationId) {
        ((AddPublicationDetailsActivity) mContext).removeItem(publicationId);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, bulkList.size());
    }
}
