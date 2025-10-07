package com.example.expensetracker.ui.fragments;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.CategoryTotal;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryTotal> categories = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryTotal category);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<CategoryTotal> categories, List<Integer> colors) {
        this.categories = categories;
        this.colors = colors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryTotal category = categories.get(position);
        holder.categoryName.setText(category.category);
        holder.categoryAmount.setText("₹" + category.total);

        GradientDrawable bgShape = (GradientDrawable) holder.colorIndicator.getBackground();
        bgShape.setColor(colors.get(position % colors.size()));

        // 🔹 Handle click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View colorIndicator;
        TextView categoryName, categoryAmount;

        ViewHolder(View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryAmount = itemView.findViewById(R.id.categoryAmount);
        }
    }
}
