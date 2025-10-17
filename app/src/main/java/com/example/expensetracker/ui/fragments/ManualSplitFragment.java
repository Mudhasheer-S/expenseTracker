package com.example.expensetracker.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.SplitExpense;
import com.example.expensetracker.data.model.Friend;
import com.example.expensetracker.data.repository.ExpenseRepository;
import com.example.expensetracker.data.repository.FriendRepository;
import com.example.expensetracker.ui.viewmodel.SplitExpenseViewModel;

import java.util.ArrayList;
import java.util.List;

public class ManualSplitFragment extends Fragment {

    private LinearLayout layoutContainer;
    private TextView tvRemaining;
    private EditText etNote;
    private SplitExpenseViewModel splitViewModel;
    private int expenseId;
    private double totalAmount;
    private long expenseDate;
    private String noteText;
    private double remaining;

    private final List<Friend> selectedFriends = new ArrayList<>();
    private final List<EditText> friendInputs = new ArrayList<>();
    private EditText etMyShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_split, container, false);

        layoutContainer = view.findViewById(R.id.layoutFriendInputs);
        tvRemaining = view.findViewById(R.id.tvRemainingManual);
        etNote = view.findViewById(R.id.etNoteManual);
        Button btnConfirm = view.findViewById(R.id.btnConfirmSplit);

        splitViewModel = new ViewModelProvider(this).get(SplitExpenseViewModel.class);

        // Get arguments from previous fragment
        Bundle args = getArguments();
        if (args != null) {
            expenseId = args.getInt("expenseId");
            totalAmount = args.getDouble("totalAmount");
            expenseDate = args.getLong("expenseDate");
            noteText = args.getString("note", "Manual Split");
        }

        // Get selected friend IDs passed from SplitExpenseFragment
        ArrayList<Integer> selectedFriendIds = args != null ? args.getIntegerArrayList("selectedFriendIds") : new ArrayList<>();

        // Load friends from repository
        FriendRepository friendRepo = new FriendRepository(requireActivity().getApplication());
        friendRepo.getAllFriends().observe(getViewLifecycleOwner(), friends -> {

            selectedFriends.clear();
            friendInputs.clear();

            // Filter friends to only include selected IDs
            for (Friend f : friends) {
                if (selectedFriendIds.contains(f.id)) {
                    selectedFriends.add(f);
                }
            }

            layoutContainer.removeAllViews();

            // Add EditText rows for each selected friend
            for (Friend f : selectedFriends) {
                View row = getLayoutInflater().inflate(R.layout.item_friend_input, layoutContainer, false);
                TextView tvName = row.findViewById(R.id.tvFriendName);
                EditText etAmount = row.findViewById(R.id.etFriendAmount);
                tvName.setText(f.name);
                layoutContainer.addView(row);
                friendInputs.add(etAmount);
                etAmount.addTextChangedListener(watcher);
            }

            // Add “My Share” row
            View myRow = getLayoutInflater().inflate(R.layout.item_friend_input, layoutContainer, false);
            TextView tvName = myRow.findViewById(R.id.tvFriendName);
            etMyShare = myRow.findViewById(R.id.etFriendAmount);
            tvName.setText("My Share");
            layoutContainer.addView(myRow);
            etMyShare.addTextChangedListener(watcher);

            updateRemaining();
        });

        btnConfirm.setOnClickListener(v -> {
            if (remaining != 0) {
                Toast.makeText(requireContext(), "Amounts do not add up to total!", Toast.LENGTH_SHORT).show();
                return;
            }

            double myShare = 0;
            try {
                myShare = Double.parseDouble(etMyShare.getText().toString());
            } catch (Exception ignored) {}

            for (int i = 0; i < selectedFriends.size(); i++) {
                double share;
                try {
                    share = Double.parseDouble(friendInputs.get(i).getText().toString());
                } catch (Exception e) {
                    share = 0;
                }

                SplitExpense s = new SplitExpense();
                s.expenseId = expenseId;
                s.friendId = selectedFriends.get(i).id;
                s.shareAmount = share;
                s.note = etNote.getText().toString().isEmpty() ? noteText : etNote.getText().toString();
                s.date = expenseDate;
                s.isPaid = false;
                splitViewModel.insertSplit(s);
            }

            new ExpenseRepository(requireActivity().getApplication())
                    .updateAmountAndSplitFlag(expenseId, myShare, true);

            Toast.makeText(requireContext(), "Manual split saved", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).popBackStack(R.id.expenseListFragment,false);
        });

        return view;
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateRemaining();
        }
    };

    private void updateRemaining() {
        double sum = 0;
        for (EditText e : friendInputs) {
            try { sum += Double.parseDouble(e.getText().toString()); } catch (Exception ignored) {}
        }
        try { sum += Double.parseDouble(etMyShare.getText().toString()); } catch (Exception ignored) {}

        remaining = totalAmount - sum;
        tvRemaining.setText("Remaining: ₹" + remaining);
        tvRemaining.setTextColor(remaining == 0 ? getResources().getColor(android.R.color.holo_green_dark)
                : getResources().getColor(android.R.color.holo_red_dark));
    }
}
