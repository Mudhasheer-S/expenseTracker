package com.example.expensetracker.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.ExpenseWithCategory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseDialogAdapter extends RecyclerView.Adapter<ExpenseDialogAdapter.ViewHolder> {

    private List<ExpenseWithCategory> expenses = new ArrayList<>();

    public void setExpenses(List<ExpenseWithCategory> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseWithCategory e = expenses.get(position);
        holder.merchant.setText(e.expense.merchant);
        holder.amount.setText("₹" + e.expense.amount);

        // Format date
        String dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(new java.util.Date(e.expense.date));
        holder.date.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView merchant, amount, date;

        ViewHolder(View itemView) {
            super(itemView);
            merchant = itemView.findViewById(R.id.merchant);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
        }
    }
}

