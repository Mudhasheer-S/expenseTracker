package com.example.expensetracker.ui.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.CategoryTotal;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;
import com.example.expensetracker.ui.main.ExpenseViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private PieChart pieChart;
    private TextView totalAmount;
    private Spinner filterSpinner;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

    private Calendar customStartDate;
    private Calendar customEndDate;

    private String currentFilter = "Monthly"; // default
    private String selectedYearMonth;
    private String selectedYear;
    private long customStartMillis, customEndMillis;
    private long startOfDay, endOfDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        filterSpinner = view.findViewById(R.id.spinnerFilterType);
        pieChart = view.findViewById(R.id.pieChart);
        totalAmount = view.findViewById(R.id.totalAmount);
        recyclerView = view.findViewById(R.id.recyclerViewCategories);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        setupPieChart();
        setupRecyclerView();
        setupFilter();

        return view;
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextSize(16f);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false); // we show custom list instead
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter();
        recyclerView.setAdapter(categoryAdapter);

        // 🔹 Category click respects filter
        categoryAdapter.setOnCategoryClickListener(categoryTotal -> {
            Log.d("StatsFragment", "Clicked category: " + categoryTotal.category);

            switch (currentFilter) {
                case "Monthly":
                    expenseViewModel.getExpensesByCategoryAndMonth(categoryTotal.category, selectedYearMonth)
                            .observe(getViewLifecycleOwner(), expenses -> showExpensesDialogIfNotEmpty(expenses, categoryTotal.category));
                    break;
                case "Yearly":
                    expenseViewModel.getExpensesByCategoryAndYear(categoryTotal.category, selectedYear)
                            .observe(getViewLifecycleOwner(), expenses -> showExpensesDialogIfNotEmpty(expenses, categoryTotal.category));
                    break;
                case "Custom Date":
                    expenseViewModel.getExpensesByCategoryAndDateRange(categoryTotal.category, customStartMillis, customEndMillis)
                            .observe(getViewLifecycleOwner(), expenses -> showExpensesDialogIfNotEmpty(expenses, categoryTotal.category));
                    break;
                case "Daily":
                    expenseViewModel.getExpensesByCategoryAndDateRange(categoryTotal.category, startOfDay, endOfDay)
                            .observe(getViewLifecycleOwner(), expenses -> showExpensesDialogIfNotEmpty(expenses, categoryTotal.category));
                    break;
            }
        });
    }

    private void setupFilter() {
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFilter = parent.getItemAtPosition(position).toString();

                if (currentFilter.equals("Monthly")) {
                    selectedYearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(System.currentTimeMillis());
                    expenseViewModel.getCategoryTotalsByMonth(selectedYearMonth)
                            .observe(getViewLifecycleOwner(), StatsFragment.this::updateChart);
                } else if (currentFilter.equals("Yearly")) {
                    selectedYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(System.currentTimeMillis());
                    expenseViewModel.getCategoryTotalsByYear(selectedYear)
                            .observe(getViewLifecycleOwner(), StatsFragment.this::updateChart);
                } else if (currentFilter.equals("Custom Date")) {
                    pickCustomDateRange();
                } else if (currentFilter.equals("Daily")) {
                    loadDailyExpenses();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

//    private void pickCustomDateRange() {
//        customStartDate = Calendar.getInstance();
//        customEndDate = Calendar.getInstance();
//
//        DatePickerDialog startPicker = new DatePickerDialog(getContext(),
//                (view, year, month, dayOfMonth) -> {
//                    customStartDate.set(year, month, dayOfMonth);
//
//                    DatePickerDialog endPicker = new DatePickerDialog(getContext(),
//                            (view2, year2, month2, day2) -> {
//                                customEndDate.set(year2, month2, day2);
//
//                                customStartMillis = customStartDate.getTimeInMillis();
//                                customEndMillis = customEndDate.getTimeInMillis();
//
//                                expenseViewModel.getCategoryTotalsByDateRange(customStartMillis, customEndMillis)
//                                        .observe(getViewLifecycleOwner(), StatsFragment.this::updateChart);
//                            },
//                            customEndDate.get(Calendar.YEAR),
//                            customEndDate.get(Calendar.MONTH),
//                            customEndDate.get(Calendar.DAY_OF_MONTH));
//                    endPicker.show();
//                },
//                customStartDate.get(Calendar.YEAR),
//                customStartDate.get(Calendar.MONTH),
//                customStartDate.get(Calendar.DAY_OF_MONTH));
//        startPicker.show();
//    }
    private void pickCustomDateRange() {
        customStartDate = Calendar.getInstance();
        customEndDate = Calendar.getInstance();

        DatePickerDialog startPicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    customStartDate.set(year, month, dayOfMonth);

                    DatePickerDialog endPicker = new DatePickerDialog(getContext(),
                            (view2, year2, month2, day2) -> {
                                customEndDate.set(year2, month2, day2);

                                // ✅ Ensure full-day coverage
                                customStartDate.set(Calendar.HOUR_OF_DAY, 0);
                                customStartDate.set(Calendar.MINUTE, 0);
                                customStartDate.set(Calendar.SECOND, 0);
                                customStartDate.set(Calendar.MILLISECOND, 0);

                                customEndDate.set(Calendar.HOUR_OF_DAY, 23);
                                customEndDate.set(Calendar.MINUTE, 59);
                                customEndDate.set(Calendar.SECOND, 59);
                                customEndDate.set(Calendar.MILLISECOND, 999);

                                customStartMillis = customStartDate.getTimeInMillis();
                                customEndMillis = customEndDate.getTimeInMillis();

                                // ✅ Now the last day’s transactions will be included
                                expenseViewModel.getCategoryTotalsByDateRange(customStartMillis, customEndMillis)
                                        .observe(getViewLifecycleOwner(), StatsFragment.this::updateChart);
                            },
                            customEndDate.get(Calendar.YEAR),
                            customEndDate.get(Calendar.MONTH),
                            customEndDate.get(Calendar.DAY_OF_MONTH));
                    endPicker.show();
                },
                customStartDate.get(Calendar.YEAR),
                customStartDate.get(Calendar.MONTH),
                customStartDate.get(Calendar.DAY_OF_MONTH));
        startPicker.show();
    }


    private void updateChart(List<CategoryTotal> categoryTotals) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        float sum = 0;

        int[] chartColors = {

                // 🔵 Blues (Start cool)
                Color.parseColor("#2196F3"), // Blue (Main)
                Color.parseColor("#F44336"), // Red
                Color.parseColor("#00FF7F"), // Spring Green
                Color.parseColor("#FF9800"), // Orange
                Color.parseColor("#FFEB3B"), // Yellow
                Color.parseColor("#9C27B0"), // Purple
                Color.parseColor("#00BCD4"), // Cyan
                Color.parseColor("#FFC107"), // Amber
                Color.parseColor("#673AB7"), // Deep Purple
                Color.parseColor("#FF5722"), // Deep Orange

                // 🟢 Greens
                Color.parseColor("#4CAF50"), // Green

                // 🟡 Yellows
                Color.parseColor("#CDDC39"), // Lime

                // 🟠 Oranges
                Color.parseColor("#8BC34A"), // Light Green
                Color.parseColor("#FF7043"), // Soft Orange
                Color.parseColor("#03A9F4"), // Light Blue

                // 🔴 Reds / Pinks

                // 🟣 Purples / Violets

                // 🔵 Loop back to Main
                Color.parseColor("#2196F3"), // Blue (Main again)

        };

        int colorIndex = 0;

        for (CategoryTotal ct : categoryTotals) {
            entries.add(new PieEntry((float) ct.total, ct.category));
            colors.add(chartColors[colorIndex % chartColors.length]);
            sum += ct.total;
            colorIndex++;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

        totalAmount.setText("Total: ₹" + sum);

        // update category list
        categoryAdapter.setData(categoryTotals, colors);
    }

    private void loadDailyExpenses() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        startOfDay = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        endOfDay = end.getTimeInMillis();

        expenseViewModel.getCategoryTotalsByDateRange(startOfDay, endOfDay)
                .observe(getViewLifecycleOwner(), this::updateChart);
    }

    private void showExpensesDialogIfNotEmpty(List<ExpenseWithCategory> expenses, String categoryName) {
        if (expenses != null && !expenses.isEmpty()) {
            showExpensesDialog(expenses, categoryName);
        }
    }

    private void showExpensesDialog(List<ExpenseWithCategory> expenses, String categoryName) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_expenses, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Expense> expenseList = new ArrayList<>();
        NavController navController = NavHostFragment.findNavController(this);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

        ExpenseDialogAdapter adapter = new ExpenseDialogAdapter(expenseList,navController,dialog);
        adapter.setExpenses(expenses);
        recyclerView.setAdapter(adapter);

        dialog.setContentView(dialogView);
        dialog.show();
    }
}
