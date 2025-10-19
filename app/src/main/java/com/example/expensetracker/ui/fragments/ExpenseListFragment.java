package com.example.expensetracker.ui.fragments;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.data.model.ExpenseWithCategory;
import com.example.expensetracker.ui.main.ExpenseAdapter;
import com.example.expensetracker.ui.main.ExpenseViewModel;
import com.example.expensetracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseListFragment extends Fragment {
    private ExpenseViewModel expenseViewModel;
    private TextView tvTotal;
    private TextView tvSelectedMonth;
    private ExpenseAdapter adapter;

    private String monthFilter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

        // Bind UI elements
        tvTotal = view.findViewById(R.id.tvTotalExpense);
        tvSelectedMonth = view.findViewById(R.id.tvSelectedMonth);



        // ✅ UPDATED: Consistent date formats
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault()); // for DB query
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()); // for UI display

        // ViewModel init
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        Bundle args = getArguments();
        int highlightId_nav = -1;
        monthFilter = null;

        if (args != null) {
            highlightId_nav = args.getInt("highlightExpenseId", -1);
            monthFilter = args.getString("monthFilter", null);
        }

// ✅ Set the month in ViewModel to trigger the observer
        if (monthFilter != null) {
            expenseViewModel.loadMonth(monthFilter);
        }

        // ✅ UPDATED: Read "monthFilter" argument from navigation (if coming from SplitExpenseAdapter)
        String monthArg = getArguments() != null ? getArguments().getString("monthFilter") : null;

        // ✅ UPDATED: Default to current month if null
        if (monthArg == null) {
            monthArg = apiFormat.format(Calendar.getInstance().getTime());
        }

        // ✅ UPDATED: Convert yyyy-MM → MMMM yyyy for showing in UI
        try {
            Date parsedDate = apiFormat.parse(monthArg);
            tvSelectedMonth.setText(displayFormat.format(parsedDate));
        } catch (Exception e) {
            tvSelectedMonth.setText(monthArg);
        }

        // 🔹 Highlight if coming from SplitExpenseAdapter
        int highlightId = getArguments() != null ? getArguments().getInt("highlightExpenseId", -1) : -1;


        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

// 🔹 Combined click listener
        adapter.setOnExpenseClickListener(expenseId -> {
            ExpenseDetailFragment sheet = ExpenseDetailFragment.newInstance(expenseId);
            sheet.show(getParentFragmentManager(), "ExpenseDetail");

            int pos = getExpensePosition(expenseId);
            if (pos != -1) {
                recyclerView.smoothScrollToPosition(pos);
                adapter.highlightExpense(expenseId);
            }
        });



        // Add Expense button
        Button btnAddExpense = view.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_expenseList_to_addExpense);
        });

        // Manage Categories button
        Button btnCategories = view.findViewById(R.id.btnAddCategory);
        btnCategories.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_expenseList_to_categoryManager);
        });

        // ✅ UPDATED: Observe filtered expenses by month
//        expenseViewModel.getExpensesByMonth(monthArg).observe(getViewLifecycleOwner(), expenses -> {
//            adapter.setExpenses(expenses);
//
//            double total = 0;
//            for (ExpenseWithCategory e : expenses) {
//                total += e.expense.amount;
//            }
//            tvTotal.setText("Total Spent: ₹" + String.format("%.2f", total));
//
//            // 🔹 Scroll and highlight if coming from SplitExpenseAdapter
//            if (highlightId != -1) {
//                int pos = adapter.getPositionById(highlightId);
//                if (pos != -1) {
//                    recyclerView.scrollToPosition(pos);
//                    adapter.highlightExpense(highlightId);
//                }
//            }
//        });

//        expenseViewModel.setMonth(monthArg);


        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            adapter.setExpenses(expenses);

            double total = 0;
            for (ExpenseWithCategory e : expenses) total += e.expense.amount;
            tvTotal.setText("Total Spent: ₹" + String.format("%.2f", total));


            if (getArguments() != null && getArguments().containsKey("highlightExpenseId")) {
                int checkhighlightid = getArguments().getInt("highlightExpenseId", -1);
                // Scroll & highlight
                if (checkhighlightid != -1) {
                    for (int i = 0; i < expenses.size(); i++) {
                        if (expenses.get(i).expense.id == checkhighlightid) {
                            recyclerView.scrollToPosition(i);
                            adapter.highlightExpense(checkhighlightid);
                            break;
                        }
                    }
                }
                getArguments().remove("highlightExpenseId");

            }
        });

        // Month selector UI
        addMonth(view);

        return view;
    }

    private int getExpensePosition(int expenseId) {
        List<ExpenseWithCategory> list = adapter.getExpenseList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).expense.id == expenseId) return i;
        }
        return -1;
    }


    /**
     * 📅 Handles month filter UI (chips + date picker)
     */
    public void addMonth(View view) {
        ChipGroup chipContainer = view.findViewById(R.id.monthChipContainer);
        View btnCalendar = view.findViewById(R.id.btnPickMonth);
        View monthChipSection = view.findViewById(R.id.monthChipSection);

        btnCalendar.setOnClickListener(v -> {
            if (monthChipSection.getVisibility() == View.GONE) {
                monthChipSection.setVisibility(View.VISIBLE);
                monthChipSection.setAlpha(0f);
                monthChipSection.animate().alpha(1f).setDuration(200).start();
            } else {
                monthChipSection.animate().alpha(0f).setDuration(200)
                        .withEndAction(() -> monthChipSection.setVisibility(View.GONE))
                        .start();
            }
        });

        // Observe available months from DB
        expenseViewModel.getAvailableMonths().observe(getViewLifecycleOwner(), monthList -> {
            chipContainer.removeAllViews();
            if (monthList == null || monthList.isEmpty()) return;

            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            SimpleDateFormat outFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

            for (int i = 0; i < monthList.size(); i++) {
                String ym = monthList.get(i);
                String display;
                try {
                    Date date = inFormat.parse(ym);
                    display = outFormat.format(date);
                } catch (ParseException e) {
                    display = ym;
                }

                Chip chip = new Chip(getContext());
                chip.setText(display);
                chip.setTag(ym);
                chip.setCheckable(true);

                chip.setOnClickListener(v -> {
                    // Deselect all other chips
                    for (int j = 0; j < chipContainer.getChildCount(); j++) {
                        Chip child = (Chip) chipContainer.getChildAt(j);
                        child.setChecked(false);
                    }
                    chip.setChecked(true);

                    // ✅ UPDATED: Use same yyyy-MM format for ViewModel
                    String selectedYM = (String) v.getTag();
                    tvSelectedMonth.setText(((Chip) v).getText());
                    expenseViewModel.setMonth(selectedYM);
                });

                chipContainer.addView(chip);

                // Auto-select first chip only if no monthFilter passed
                if (i == 0 && monthFilter == null) {
                    chip.performClick();
                } else if (monthFilter != null && ym.equals(monthFilter)) {
                    chip.performClick(); // pre-select the correct month chip
                }

            }
        });
    }
}
