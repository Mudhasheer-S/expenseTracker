package com.example.expensetracker.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<ExpenseWithCategory> expenseList = new ArrayList<>();
    public interface OnExpenseClickListener {
        void onExpenseClick(int expenseId);
    }

    private OnExpenseClickListener listener;

    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    public void setExpenses(List<ExpenseWithCategory> expenses) {
        this.expenseList = expenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseWithCategory expenseWithCategory = expenseList.get(position);
        holder.tvDesc.setText(expenseWithCategory.expense.merchant);
        holder.tvAmount.setText("₹" +expenseWithCategory. expense.amount);
        holder.tvCategory.setText(expenseWithCategory.category.name);
        holder.tvDate.setText(DateFormat.getDateInstance().format(new Date(expenseWithCategory.expense.date)));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExpenseClick(expenseWithCategory.expense.id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc, tvAmount, tvDate,tvCategory;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvExpenseDesc);
            tvCategory = itemView.findViewById(R.id.tvExpenseCategory);

            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
        }
    }
}

