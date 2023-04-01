package com.example.sepextremeg.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sepextremeg.R;
import com.example.sepextremeg.model.Category;
import com.example.sepextremeg.model.StaffModel;

import java.util.ArrayList;

public class HomeVerticalAdapter extends RecyclerView.Adapter<HomeVerticalAdapter.ViewHolder> {

    private final Activity context;
    private final ArrayList<Category> arrayList;
    private int page = -1;
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    View view;

    public HomeVerticalAdapter(Activity context, ArrayList<Category> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Category categoriesAdapterModel = (Category) arrayList.get(position);
        String title = categoriesAdapterModel.getUserRole();

        if (!categoriesAdapterModel.getStaffModels().isEmpty()) {
            if (holder.tvName != null) {
                holder.tvName.setText(title);
                holder.llTitle.setVisibility(View.VISIBLE);
            }
        } else {
            holder.llTitle.setVisibility(View.GONE);
        }
        ArrayList<StaffModel> singleItem = categoriesAdapterModel.getStaffModels();

        if (singleItem != null && singleItem.size() > 0) {

            HomeHorizontalAdapter horizontalRecycleViewAdapter
                    = new HomeHorizontalAdapter(context
                    , singleItem, title);

            if (holder.recyclerViewBouncy != null) {

                System.out.println("notifying adapter 1");
                holder.recyclerViewBouncy.setHasFixedSize(true);
                holder.recyclerViewBouncy.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.recyclerViewBouncy.setAdapter(horizontalRecycleViewAdapter);
                holder.recyclerViewBouncy.getAdapter().notifyDataSetChanged();

                holder.recyclerViewBouncy.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager mLayoutManager = (LinearLayoutManager) holder.recyclerViewBouncy.getLayoutManager();

                        assert mLayoutManager != null;
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems + 6) >= totalItemCount) {
                                loading = false;

                                page = horizontalRecycleViewAdapter.getPage() + 1;
                                horizontalRecycleViewAdapter.setPage(page);

                                loading = true;
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout, llTitle;
        TextView tvName;
        RecyclerView recyclerViewBouncy;
        ImageView btnFilter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.category_linear_layout);
            llTitle = itemView.findViewById(R.id.llTitle);
            tvName = itemView.findViewById(R.id.category_title);
            recyclerViewBouncy = itemView.findViewById(R.id.listmovie);
        }
    }
}