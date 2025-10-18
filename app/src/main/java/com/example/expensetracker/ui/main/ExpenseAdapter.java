package com.example.expensetracker.ui.main;

import android.graphics.Color;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckedTextView;
import android.widget.ImageView;
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
import android.os.Handler;


public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<ExpenseWithCategory> expenseList = new ArrayList<>();


    // To track the highlighted expense ID
    private int highlightedExpenseId = -1;


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

    // 🔹 call this when you want to highlight a specific expense
    public void highlightExpense(int expenseId) {
        highlightedExpenseId = expenseId;
        notifyDataSetChanged();

        // remove highlight after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            highlightedExpenseId = -1;
            notifyDataSetChanged();
        }, 2000);
    }

    public int getPositionById(int expenseId) {
        for (int i = 0; i < expenseList.size(); i++) {
            if (expenseList.get(i).expense.id == expenseId)
                return i;
        }
        return -1;
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
        if(expenseWithCategory.expense.source.equals("") || expenseWithCategory.expense.source == null){
            holder.ivBankLogo.setVisibility(View.GONE);
        }
        else{
            holder.ivBankLogo.setVisibility(View.VISIBLE);
            selectBankLogo(expenseWithCategory.expense.source,holder);
        }
        holder.tvAmount.setText("₹" +expenseWithCategory. expense.amount);
        holder.tvCategory.setText(expenseWithCategory.category.name);
        holder.ivSplitIndicator.setVisibility(expenseWithCategory.expense.isSplit ? View.VISIBLE : View.GONE);
        holder.tvDate.setText(DateFormat.getDateInstance().format(new Date(expenseWithCategory.expense.date)));
        // 🔹 Click listener

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExpenseClick(expenseWithCategory.expense.id);
            }
        });


        // 🔹 Highlight effect for the selected expense
        if (expenseWithCategory.expense.id == highlightedExpenseId) {
            holder.itemView.setBackgroundColor(Color.parseColor("#333333")); // subtle grey shine for dark mode
            animateShine(holder.itemView);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }


    }

    private void selectBankLogo(String source, ExpenseViewHolder holder) {
        if(source.equals("INDIAN_BANK")){
            holder.ivBankLogo.setImageResource(R.mipmap.ind_round);
        }
        else if(source.equals("KVB_BANK")) {
            holder.ivBankLogo.setImageResource(R.mipmap.kvb_round);
        }
        else if(source.equals("BOB_BANK")) {
            holder.ivBankLogo.setImageResource(R.mipmap.bob);
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSplitIndicator,ivBankLogo;
        TextView tvDesc, tvAmount, tvDate,tvCategory;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvExpenseDesc);
            tvCategory = itemView.findViewById(R.id.tvExpenseCategory);
            ivSplitIndicator = itemView.findViewById(R.id.ivSplitIndicator);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
            ivBankLogo = itemView.findViewById(R.id.ivBankLogo);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
        }
    }

    // ✨ Improved shine animation (fade & pulse)
    private void animateShine(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.5f, 1.0f);
        anim.setDuration(600);
        anim.setRepeatMode(AlphaAnimation.REVERSE);
        anim.setRepeatCount(2);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    public List<ExpenseWithCategory> getExpenseList() {
        return expenseList;
    }

}

