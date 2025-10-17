package com.example.expensetracker.ui.adapter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.SplitExpense;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SplitExpenseAdapter extends RecyclerView.Adapter<SplitExpenseAdapter.ViewHolder> {

    public interface OnMarkPaidListener {
        void onMarkPaid(SplitExpense split);
    }

    private List<SplitExpense> items = new ArrayList<>();
    private OnMarkPaidListener markPaidListener;
    private SimpleDateFormat fmt = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

    public SplitExpenseAdapter(OnMarkPaidListener l) { this.markPaidListener = l; }

    public void setItems(List<SplitExpense> list) {
        items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SplitExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SplitExpenseAdapter.ViewHolder holder, int position) {
        SplitExpense s = items.get(position);
        holder.tvNote.setText(s.note != null ? s.note : "");
        holder.tvAmount.setText(String.format("₹%.2f", s.shareAmount));

        // Show icon if linked to main expense
        if (s.expenseId > 0) {
            holder.ivLinkedExpense.setVisibility(View.VISIBLE);
        } else {
            holder.ivLinkedExpense.setVisibility(View.GONE);
        }
        holder.tvDate.setText(fmt.format(new Date(s.date)));
        if (!s.isPaid) {
            holder.btnMark.setVisibility(View.VISIBLE);
            holder.btnMark.setOnClickListener(v -> {
                if (markPaidListener != null) markPaidListener.onMarkPaid(s);
            });
        } else {
            holder.btnMark.setVisibility(View.GONE);
        }

        holder.ivLinkedExpense.setOnClickListener(v -> {
            // 1️⃣ Animate item shine
            holder.itemView.animate()
                    .alpha(0.7f)
                    .setDuration(150)
                    .withEndAction(() -> holder.itemView.animate()
                            .alpha(1f)
                            .setDuration(150)
                            .start())
                    .start();

            // 2️⃣ Navigate after short delay so animation is visible
            v.postDelayed(() -> {
                Bundle bundle = new Bundle();
                bundle.putString("monthFilter", getMonthFromExpense(s.date));
                bundle.putInt("highlightExpenseId", s.expenseId); // 👈 send expense ID

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.expenseListFragment, bundle);
            }, 160); // slightly longer than animation so it's smooth
        });



    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivLinkedExpense;
        TextView tvNote, tvAmount, tvDate;
        Button btnMark;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNote = itemView.findViewById(R.id.tvSplitNote);
            tvAmount = itemView.findViewById(R.id.tvSplitAmount);
            tvDate = itemView.findViewById(R.id.tvSplitDate);
            ivLinkedExpense = itemView.findViewById(R.id.ivLinkedExpense);
            btnMark = itemView.findViewById(R.id.btnMarkPaid);
        }
    }

    private String getMonthFromExpense(long dateMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(new Date(dateMillis)); // ✅ Matches DAO format
    }



}
