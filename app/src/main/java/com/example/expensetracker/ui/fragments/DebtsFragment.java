package com.example.expensetracker.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Friend;
import com.example.expensetracker.ui.adapter.FriendListAdapter;
import com.example.expensetracker.ui.viewmodel.FriendViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import java.util.List;

public class DebtsFragment extends Fragment {

    private FriendViewModel friendViewModel;
    private FriendListAdapter adapter;

    public DebtsFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recycler = view.findViewById(R.id.recyclerFriends);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendListAdapter(friend -> {
            // navigate to friend detail; pass friend id and name via bundle
            Bundle b = new Bundle();
            b.putInt("friendId", friend.id);
            b.putString("friendName", friend.name);
            Navigation.findNavController(view).navigate(R.id.action_debts_to_friendDetail, b);
        });
        recycler.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddFriend);
        fab.setOnClickListener(v -> showAddFriendDialog());

        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        friendViewModel.getAllFriends().observe(getViewLifecycleOwner(), friends -> {
            // ensure friend.totalDue is present — if not, set 0
            if (friends != null) {
                for (Friend f : friends) {
                    if (f.totalDue == 0) f.totalDue = 0;
                }
            }
            adapter.setItems(friends);
        });
    }

    private void showAddFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Friend");
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);
        builder.setPositiveButton("Add", (d, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                Friend f = new Friend();
                f.name = name;
                f.totalDue = 0;
                friendViewModel.insertFriend(f);
                Toast.makeText(getContext(), "Friend added", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (d, w) -> {});
        builder.show();
    }
}
