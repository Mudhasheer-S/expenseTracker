package com.example.expensetracker.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseDialogAdapter extends RecyclerView.Adapter<ExpenseDialogAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private NavController navController;

    private BottomSheetDialog bottomSheetDialog;

    public ExpenseDialogAdapter(List<Expense> expenses, NavController navController, BottomSheetDialog dialog) {
        this.expenseList = expenses;
        this.navController = navController;
        this.bottomSheetDialog = dialog;
    }
    private List<ExpenseWithCategory> expenses = new ArrayList<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


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

        holder.date.setText(dateFormat.format(new Date(e.expense.date)));

        // ✨ Handle click on the entire layout
        holder.itemView.setOnClickListener(v -> {
            // 1️⃣ Small blink animation
            holder.itemView.animate()
                    .alpha(0.7f)
                    .setDuration(150)
                    .withEndAction(() -> holder.itemView.animate()
                            .alpha(1f)
                            .setDuration(150)
                            .start())
                    .start();

            v.postDelayed(() -> {
                Bundle bundle = new Bundle();
                bundle.putString("monthFilter", getMonthFromExpense(e.expense.date));
                bundle.putInt("highlightExpenseId", e.expense.id); // 👈 send expenseId

                navController.navigate(R.id.expenseListFragment, bundle);
            }, 160);

            if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
            }

            // 2️⃣ Navigate after short delay
        });
        // Format date
//        String dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
//                .format(new java.util.Date(e.expense.date));
//        holder.date.setText(dateStr);
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

    private String getMonthFromExpense(long dateMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(new Date(dateMillis));
    }
}

