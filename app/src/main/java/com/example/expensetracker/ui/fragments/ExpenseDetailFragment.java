package com.example.expensetracker.ui.fragments;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.ui.main.ExpenseViewModel;
import com.example.expensetracker.ui.viewmodel.CategoryViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class ExpenseDetailFragment extends BottomSheetDialogFragment {
    private Spinner spinnerCategory;
    private List<Category> categoryList;

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

        EditText etMerchant = view.findViewById(R.id.etMerchant);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etDate = view.findViewById(R.id.etDate);
        EditText etNotes = view.findViewById(R.id.editTextNotes); // 👈 NEW field for notes
        spinnerCategory = view.findViewById(R.id.autoCategory);
        MaterialButton btnUpdate = view.findViewById(R.id.btnUpdateExpense);

        int expenseId = getArguments() != null ? getArguments().getInt("expenseId") : -1;
        ExpenseViewModel expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

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

        expenseViewModel.getExpenseById(expenseId).observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                etMerchant.setText(expense.expense.merchant);
                etAmount.setText(String.valueOf(expense.expense.amount));
                etDate.setText(DateFormat.getDateInstance().format(new Date(expense.expense.date)));
                etNotes.setText(expense.expense.getNotes()); // 👈 Load notes

                // Set current category in Spinner
                if (categoryList != null) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).id == expense.expense.categoryId) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                }

                btnUpdate.setOnClickListener(v -> {
                    expense.expense.merchant = etMerchant.getText().toString();
                    try {
                        expense.expense.amount = Double.parseDouble(etAmount.getText().toString());
                    } catch (NumberFormatException e) {
                        expense.expense.amount = 0;
                    }
                    // Date parsing can be added here if editable
                    // Set category
                    Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
                    if (selectedCategory != null) {
                        expense.expense.categoryId = selectedCategory.id;
                    }

                    // 👇 Save notes
                    expense.expense.setNotes(etNotes.getText().toString());

                    expenseViewModel.updateExpense(expense.expense);
                    Toast.makeText(getContext(), "Expense updated", Toast.LENGTH_SHORT).show();
                });

//                MaterialButton btnChangeCategoryPast = view.findViewById(R.id.btnChangeCategoryPast);
//
//                btnChangeCategoryPast.setOnClickListener(v -> {
//                    Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
//                    if (selectedCategory != null) {
//                        Executors.newSingleThreadExecutor().execute(() -> {
//                            expenseViewModel.updateCategoryForMerchant(
//                                    expense.expense.merchant,
//                                    selectedCategory.id
//                            );
//                            requireActivity().runOnUiThread(() -> {
//                                Toast.makeText(getContext(),
//                                        "All past expenses for " + expense.expense.merchant + " updated",
//                                        Toast.LENGTH_SHORT).show();
//                            });
//                        });
//                    } else {
//                        Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
//                    }
//                });
                MaterialButton btnChangeCategoryPast = view.findViewById(R.id.btnChangeCategoryPast);

// Track first click timestamp
                final long[] lastClickTime = {0};
                final long DOUBLE_CLICK_INTERVAL = 2000; // 2 seconds

                btnChangeCategoryPast.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime[0] < DOUBLE_CLICK_INTERVAL) {
                        // Second click detected within 2 seconds → perform action
                        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
                        if (selectedCategory != null) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                expenseViewModel.updateCategoryForMerchant(
                                        expense.expense.merchant,
                                        selectedCategory.id
                                );
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(),
                                            "All past expenses for " + expense.expense.merchant + " updated",
                                            Toast.LENGTH_SHORT).show();
                                });
                            });
                        } else {
                            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // First click → show confirmation toast
                        Toast.makeText(getContext(),
                                "Click again to confirm updating all past expenses for " + expense.expense.merchant,
                                Toast.LENGTH_SHORT).show();
                    }
                    lastClickTime[0] = currentTime;
                });


            }
        });

        return view;
    }
}
