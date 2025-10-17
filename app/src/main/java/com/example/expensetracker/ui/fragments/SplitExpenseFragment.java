package com.example.expensetracker.ui.fragments;

import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.Friend;
import com.example.expensetracker.data.model.SplitExpense;
import com.example.expensetracker.data.repository.ExpenseRepository;
import com.example.expensetracker.data.repository.FriendRepository;
import com.example.expensetracker.ui.viewmodel.SplitExpenseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SplitExpenseFragment extends Fragment {
    private SplitExpenseViewModel splitViewModel;
    private int expenseId;
    private double totalAmount;
    private long expenseDate;
    private List<Friend> friendList;
    private ListView listFriends;
    private EditText etNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_split_expense, container, false);
        splitViewModel = new ViewModelProvider(this).get(SplitExpenseViewModel.class);

        listFriends = view.findViewById(R.id.listFriends);
        etNote = view.findViewById(R.id.etSplitNote);
        RadioGroup rgSplitType = view.findViewById(R.id.rgSplitType);
        Button btnProceed = view.findViewById(R.id.btnProceedSplit);

        expenseId = getArguments().getInt("expenseId");
        totalAmount = getArguments().getDouble("totalAmount");
        expenseDate = getArguments().getLong("expenseDate");

        FriendRepository friendRepo = new FriendRepository(requireActivity().getApplication());
        friendRepo.getAllFriends().observe(getViewLifecycleOwner(), friends -> {
            friendList = friends;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_multiple_choice,
                    friends.stream().map(f -> f.name).collect(Collectors.toList()));
            listFriends.setAdapter(adapter);
            listFriends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        });

        btnProceed.setOnClickListener(v -> {
            SparseBooleanArray checked = listFriends.getCheckedItemPositions();
            List<Friend> selectedFriends = new ArrayList<>();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    selectedFriends.add(friendList.get(checked.keyAt(i)));
                }
            }

            if (selectedFriends.isEmpty()) {
                Toast.makeText(requireContext(), "Select at least one friend", Toast.LENGTH_SHORT).show();
                return;
            }

            String noteText = etNote.getText().toString().trim();
            if (noteText.isEmpty()) noteText = "Shared expense";

            boolean isEqual = rgSplitType.getCheckedRadioButtonId() == R.id.rbEqual;

            if (isEqual) {
                double share = totalAmount / (selectedFriends.size() + 1);

                for (Friend f : selectedFriends) {
                    SplitExpense s = new SplitExpense();
                    s.expenseId = expenseId;
                    s.friendId = f.id;
                    s.shareAmount = share;
                    s.note = noteText;
                    s.date = expenseDate;
                    s.isPaid = false;
                    splitViewModel.insertSplit(s);
                }

                // Update main expense amount to user's share
                ExpenseRepository expRepo = new ExpenseRepository(requireActivity().getApplication());
                expRepo.updateAmountAndSplitFlag(expenseId, share, true);

                Toast.makeText(requireContext(), "Expense split equally", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).popBackStack(); // Go back
            } else {
                // Go to manual split page
                Bundle args = new Bundle();
                args.putInt("expenseId", expenseId);
                args.putDouble("totalAmount", totalAmount);
                args.putLong("expenseDate", expenseDate);
                args.putString("note", noteText);

// Pass selected friend IDs
                ArrayList<Integer> selectedFriendIds = new ArrayList<>();
                for (Friend f : selectedFriends) selectedFriendIds.add(f.id);
                args.putIntegerArrayList("selectedFriendIds", selectedFriendIds);

                Navigation.findNavController(v).navigate(R.id.manualSplitFragment, args);

            }
        });

        return view;
    }
}
