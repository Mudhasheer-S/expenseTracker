package com.example.expensetracker.ui.fragments;

import android.app.DatePickerDialog;
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
import java.util.Locale;

public class ExpenseListFragment extends Fragment {
    private ExpenseViewModel expenseViewModel; // ViewModel for DB communication
    private TextView tvTotal;                  // Shows total expense
    private TextView tvSelectedMonth;          // Shows current/selected month
    private ExpenseAdapter adapter;            // RecyclerView adapter

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

        // Bind UI elements
        tvTotal = view.findViewById(R.id.tvTotalExpense);
        tvSelectedMonth = view.findViewById(R.id.tvSelectedMonth);

        // Setup Calendar + formatters
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault()); // For DB queries
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()); // For UI display

        // Initialize ViewModel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Show current month by default
        tvSelectedMonth.setText(displayFormat.format(calendar.getTime()));
        expenseViewModel.setMonth(apiFormat.format(calendar.getTime())); // Notify ViewModel

        // Setup RecyclerView + Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter();

        // Click listener for each expense item
        adapter.setOnExpenseClickListener(expenseId -> {
            ExpenseDetailFragment sheet = ExpenseDetailFragment.newInstance(expenseId);
            sheet.show(getParentFragmentManager(), "ExpenseDetail");
        });
        recyclerView.setAdapter(adapter);

        // Button: Add new expense
        Button btnAddExpense = view.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_expenseList_to_addExpense);
        });

        // Button: Manage categories
        Button btnCategories = view.findViewById(R.id.btnAddCategory);
        btnCategories.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_expenseList_to_categoryManager);
        });

        /**
         * 🚀 THIS is the function that updates your list when DB changes.
         * - `getAllExpenses()` returns a LiveData<List<ExpenseWithCategory>>
         * - Room observes DB changes automatically
         * - When you add/update/delete an expense, this observer is triggered
         * - Adapter updates the RecyclerView with new data
         */
        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            // Update RecyclerView with new data
            adapter.setExpenses(expenses);

            // Recalculate total amount
            double total = 0;
            for (ExpenseWithCategory e : expenses) {
                total += e.expense.amount;
            }

            // Show total
            tvTotal.setText("Total Spent: ₹" + String.format("%.2f", total));
        });

        // Setup month selector (chips + date picker)
        addMonth(view);

        return view;
    }

    /**
     * 📅 Handles month filter UI
     * - Shows chips for available months
     * - Lets user pick a month
     * - Updates ViewModel, which updates list automatically
     */
    public void addMonth(View view) {
        ChipGroup chipContainer = view.findViewById(R.id.monthChipContainer);
        View btnCalendar = view.findViewById(R.id.btnPickMonth);
        View monthChipSection = view.findViewById(R.id.monthChipSection);

        // Toggle month filter section
        btnCalendar.setOnClickListener(v -> {
            if (monthChipSection.getVisibility() == View.GONE) {
                monthChipSection.setVisibility(View.VISIBLE);
                monthChipSection.setAlpha(0f);
                monthChipSection.animate().alpha(1f).setDuration(200).start();
            } else {
                monthChipSection.animate().alpha(0f).setDuration(200).withEndAction(() ->
                        monthChipSection.setVisibility(View.GONE)).start();
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

                // Create chip for each month
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

                    // Update selected month
                    String selectedYM = (String) v.getTag();
                    tvSelectedMonth.setText(((Chip) v).getText());
                    expenseViewModel.setMonth(selectedYM);
                });

                chipContainer.addView(chip);

                // Auto-select first chip
                if (i == 0) {
                    chip.performClick();
                }
            }
        });
    }
}
