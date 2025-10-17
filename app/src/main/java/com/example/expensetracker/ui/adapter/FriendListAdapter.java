package com.example.expensetracker.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Friend;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    private List<Friend> items = new ArrayList<>();
    private OnFriendClickListener listener;

    // You might be computing totals externally and binding text for due; here Friend.totalDue used
    public FriendListAdapter(OnFriendClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Friend> list) {
        items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_debt, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        Friend f = items.get(position);
        holder.tvName.setText(f.name);
        holder.tvDue.setText(String.format("₹%.2f", f.totalDue));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onFriendClick(f);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDue;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFriendName);
            tvDue = itemView.findViewById(R.id.tvFriendDue);
        }
    }
}
