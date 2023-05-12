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
import com.example.sepextremeg.activity.ViewAllEmployeeSalaryDetailsActivity;
import com.example.sepextremeg.model.SalaryScale;

import java.util.ArrayList;

public class AddedSalaryInfoRecyclerViewAdapter extends RecyclerView.Adapter<AddedSalaryInfoRecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<SalaryScale> bulkList;
    String employeeName, employeeServiceNo;

    public AddedSalaryInfoRecyclerViewAdapter(Context activity, ArrayList<SalaryScale> arrayList) {
        this.mContext = activity;
        this.bulkList = arrayList;
    }

    @NonNull
    @Override
    public AddedSalaryInfoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_added_info, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.removeLayout.setVisibility(View.GONE);
        holder.editLayout.setVisibility(View.GONE);
        holder.tvLogo.setVisibility(View.GONE);
//        holder.tvDownload.setVisibility(View.GONE);

        if (!bulkList.get(position).getEmployeeName().equals("") && !bulkList.get(position).getEmployeeServiceNo().equals("")){
            holder.tvDownload.setText("Name: " + bulkList.get(position).getEmployeeName()  + "\n" + "Service No: " + bulkList.get(position).getEmployeeServiceNo());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Salary code - ").append(bulkList.get(position).getSalaryCode()).append("\n");
        sb.append("Basic Salary - ").append(bulkList.get(position).getBasicSalary()).append("\n");
        sb.append("Allowance - ").append(bulkList.get(position).getAllowances()).append("\n");
        sb.append("Research Allowance Percentage - ").append(bulkList.get(position).getResearchAllowancePercentage()).append("\n");
        sb.append("Deduction Rate - ").append(bulkList.get(position).getDeductionRate()).append("\n");
        sb.append("Tax Rate - ").append(bulkList.get(position).getTaxRate()).append("\n");

        holder.tvResult.setText(sb);
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
}
