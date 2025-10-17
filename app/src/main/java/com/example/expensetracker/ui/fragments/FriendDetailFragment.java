package com.example.expensetracker.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.SplitExpense;
import com.example.expensetracker.ui.adapter.SplitExpenseAdapter;
import com.example.expensetracker.ui.viewmodel.SplitExpenseViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FriendDetailFragment extends Fragment {

    private SplitExpenseViewModel splitViewModel;
    private SplitExpenseAdapter adapter;
    private int friendId;
    private String friendName;

    private TextView tvTitle, tvPending;
    private EditText etAmount, etNote;

    public FriendDetailFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvTitle = view.findViewById(R.id.tvFriendTitle);
        tvPending = view.findViewById(R.id.tvPending);
        etAmount = view.findViewById(R.id.etSplitAmount);
        etNote = view.findViewById(R.id.etSplitNote);

        RecyclerView recycler = view.findViewById(R.id.recyclerSplits);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SplitExpenseAdapter(split -> {
            // mark as paid callback
            splitViewModel.markAsPaid(split.id,split.friendId);
        });
        recycler.setAdapter(adapter);

        if (getArguments() != null) {
            friendId = getArguments().getInt("friendId", -1);
            friendName = getArguments().getString("friendName", "Friend");
            tvTitle.setText(friendName);
        }

        splitViewModel = new ViewModelProvider(this).get(SplitExpenseViewModel.class);

        // Observe list
        splitViewModel.getSplitsForFriend(friendId).observe(getViewLifecycleOwner(), splits -> {
            adapter.setItems(splits);
            // scroll to bottom
            if (recycler.getAdapter() != null && splits != null)
                recycler.scrollToPosition(Math.max(0, splits.size() - 1));
        });

        // Observe pending amount
        splitViewModel.getPendingAmountForFriend(friendId).observe(getViewLifecycleOwner(), pending -> {
            double p = pending != null ? pending : 0.0;
            tvPending.setText(String.format("Pending: ₹%.2f", p));
        });

        view.findViewById(R.id.btnAddSplit).setOnClickListener(v -> {
            addSplit();
        });
    }

    private void addSplit() {
        String amtStr = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amtStr)) {
            Toast.makeText(getContext(), "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }
        double amt;
        try {
            amt = Double.parseDouble(amtStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        String note = etNote.getText().toString().trim();
        long now = System.currentTimeMillis();

        // create SplitExpense (expenseId 0 if not linked to main expense)
        SplitExpense s = new SplitExpense();
        s.expenseId = 0;               // if you want link: pass actual expenseId
        s.friendId = friendId;
        s.shareAmount = amt;
        s.note = note.isEmpty() ? "Shared" : note;
        s.date = now;
        s.isPaid = false;

        splitViewModel.addSplit(s);
        etAmount.setText("");
        etNote.setText("");
        Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
    }
}
