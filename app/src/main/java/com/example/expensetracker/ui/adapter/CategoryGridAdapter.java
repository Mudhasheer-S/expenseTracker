package com.example.expensetracker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Category;

import java.util.List;

public class CategoryGridAdapter extends BaseAdapter {
    private final Context context;
    private final List<Category> categories;

    public CategoryGridAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem = convertView;

        if (gridItem == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            gridItem = inflater.inflate(R.layout.item_category, parent, false);
        }

        TextView tvCategoryName = gridItem.findViewById(R.id.categoryName);
        tvCategoryName.setText(categories.get(position).name);

        return gridItem;
    }
}
