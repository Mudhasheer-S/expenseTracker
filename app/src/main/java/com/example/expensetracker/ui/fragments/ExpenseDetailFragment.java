package com.example.expensetracker.ui.fragments;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.Friend;
import com.example.expensetracker.data.model.SplitExpense;
import com.example.expensetracker.data.repository.ExpenseRepository;
import com.example.expensetracker.data.repository.FriendRepository;
import com.example.expensetracker.ui.main.ExpenseViewModel;
import com.example.expensetracker.ui.viewmodel.CategoryViewModel;
import com.example.expensetracker.ui.viewmodel.FriendViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class ExpenseDetailFragment extends BottomSheetDialogFragment {
    private Spinner spinnerCategory;
    private List<Category> categoryList;
    private LinearLayout layoutSplitDetails;

    public static ExpenseDetailFragment newInstance(int expenseId) {
        ExpenseDetailFragment fragment = new ExpenseDetailFragment();
        Bundle args = new Bundle();
        args.putInt("expenseId", expenseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_details, container, false);

        NavController navController = NavHostFragment.findNavController(this);

        EditText etMerchant = view.findViewById(R.id.etMerchant);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etDate = view.findViewById(R.id.etDate);
        EditText etNotes = view.findViewById(R.id.editTextNotes);
        spinnerCategory = view.findViewById(R.id.autoCategory);
        MaterialButton btnUpdate = view.findViewById(R.id.btnUpdateExpense);
        MaterialButton btnSplitExpense = view.findViewById(R.id.btnSplitExpense);
        layoutSplitDetails = view.findViewById(R.id.layoutSplitDetails);
        MaterialButton btnChangeCategoryPast = view.findViewById(R.id.btnChangeCategoryPast);

        btnChangeCategoryPast.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Category Update")
                    .setMessage("Do you want to change the category for all past expenses with this merchant?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Get current expense values
                        String merchant = etMerchant.getText().toString().trim();
                        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();

                        if (merchant.isEmpty() || selectedCategory == null) {
                            Toast.makeText(getContext(), "Merchant or category missing", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update all past expenses for this merchant
                        ExpenseRepository repo = new ExpenseRepository(requireActivity().getApplication());
                        repo.updateCategoryForMerchant(merchant, selectedCategory.id);

                        Toast.makeText(getContext(), "Updated all past expenses for " + merchant, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });



        int expenseId = getArguments() != null ? getArguments().getInt("expenseId") : -1;
        ExpenseViewModel expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Load categories asynchronously
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categories = categoryViewModel.getAllCategoriesStatic();
            requireActivity().runOnUiThread(() -> {
                categoryList = categories;
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        categories
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            });
        });

        // Load expense
        expenseViewModel.getExpenseById(expenseId).observe(getViewLifecycleOwner(), expense -> {
            if (expense == null) return;

            etMerchant.setText(expense.expense.merchant);
            etAmount.setText(String.valueOf(expense.expense.amount));
            etDate.setText(DateFormat.getDateInstance().format(new Date(expense.expense.date)));
            etNotes.setText(expense.expense.getNotes());

            Log.d("-----------",""+categoryList);
            // Set category
            if (categoryList != null) {
                for (int i = 0; i < categoryList.size(); i++) {
                    Log.d("check category-------",""+categoryList.get(i).id+" "+expense.expense.categoryId);
                    if (categoryList.get(i).id == expense.expense.categoryId) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }

            // 👇 Check if already split
            if (expense.expense.isSplit) {
                btnSplitExpense.setEnabled(false);
                btnSplitExpense.setText("Already Split");
                layoutSplitDetails.setVisibility(View.VISIBLE);
                loadSplitDetails(expenseViewModel, expenseId);
            } else {
                btnSplitExpense.setEnabled(true);
                btnSplitExpense.setText("Split Expense");
            }

            // Update expense
            btnUpdate.setOnClickListener(v -> {
                expense.expense.merchant = etMerchant.getText().toString();
                try {
                    expense.expense.amount = Double.parseDouble(etAmount.getText().toString());
                } catch (NumberFormatException e) {
                    expense.expense.amount = 0;
                }

                Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
                if (selectedCategory != null) {
                    expense.expense.categoryId = selectedCategory.id;
                }

                expense.expense.setNotes(etNotes.getText().toString());
                expenseViewModel.updateExpense(expense.expense);
                Toast.makeText(getContext(), "Expense updated", Toast.LENGTH_SHORT).show();
            });

            // Split expense navigation
            btnSplitExpense.setOnClickListener(v -> {
                double totalAmount = 0;
                try {
                    totalAmount = Double.parseDouble(etAmount.getText().toString());
                } catch (NumberFormatException ignored) {}

                Bundle args = new Bundle();
                args.putInt("expenseId", expenseId);
                args.putDouble("totalAmount", totalAmount);
                args.putLong("expenseDate", expense.expense.date);

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.splitExpenseFragment, args);

                dismiss();
            });
        });

        return view;
    }

    private void loadSplitDetails(ExpenseViewModel expenseViewModel, int expenseId) {
        layoutSplitDetails.removeAllViews();

        // ViewModel for friends
        FriendViewModel friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);

        // Step 1️⃣ Load all friends once and keep them in a map
        friendViewModel.getAllFriends().observe(getViewLifecycleOwner(), allFriends -> {
            if (allFriends == null) return;

            Map<Integer, String> friendMap = new HashMap<>();
            for (Friend friend : allFriends) {
                friendMap.put(friend.id, friend.name);
            }

            // Step 2️⃣ Observe splits — this won’t duplicate because friends are already cached
            expenseViewModel.getSplitsForExpense(expenseId).observe(getViewLifecycleOwner(), splitList -> {
                layoutSplitDetails.removeAllViews();

                if (splitList == null || splitList.isEmpty()) {
                    TextView tvEmpty = new TextView(requireContext());
                    tvEmpty.setText("No split records found");
                    layoutSplitDetails.addView(tvEmpty);
                    return;
                }

                double total = 0;
                for (SplitExpense split : splitList) {
                    View splitItem = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_split_detail, layoutSplitDetails, false);

                    TextView tvFriend = splitItem.findViewById(R.id.tvFriendName);
                    TextView tvAmount = splitItem.findViewById(R.id.tvAmount);
                    TextView tvNote = splitItem.findViewById(R.id.tvNote);

                    String friendName = friendMap.getOrDefault(split.friendId, "Unknown Friend");
                    tvFriend.setText(friendName);
                    tvAmount.setText("₹ " + split.shareAmount);
                    tvNote.setText(split.note != null && !split.note.isEmpty() ? split.note : "—");

                    total += split.shareAmount;
                    layoutSplitDetails.addView(splitItem);
                }

                // Add total footer
                TextView tvTotal = new TextView(requireContext());
                tvTotal.setText("Total Split: ₹ " + total);
                tvTotal.setTextSize(15);
                tvTotal.setPadding(10, 15, 10, 10);
                layoutSplitDetails.addView(tvTotal);
            });
        });
    }



}
