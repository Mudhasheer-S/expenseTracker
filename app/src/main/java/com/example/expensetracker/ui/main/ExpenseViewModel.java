package com.example.expensetracker.ui.main;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.expensetracker.data.model.CategoryTotal;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;
import com.example.expensetracker.data.repository.ExpenseRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class ExpenseViewModel extends AndroidViewModel {
    private ExpenseRepository repository;
    private final MutableLiveData<String> selectedMonth = new MutableLiveData<>();

    public LiveData<List<ExpenseWithCategory>> expensesThisMonth = Transformations.switchMap(selectedMonth, yearMonth ->
            repository.getExpensesByMonth(yearMonth)
    );
//    private LiveData<List<ExpenseWithCategory>> allExpenses;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
//        allExpenses = repository.getAllExpenses();
   availableMonths = repository.getAllExpenseMonths();
    }

    public LiveData<List<ExpenseWithCategory>> getAllExpenses() {
        return expensesThisMonth;
    }
    public LiveData<ExpenseWithCategory> getExpenseById(int id) {
        return repository.getExpensesById(id);
    }
    public void updateExpense(Expense expense) {
        repository.update(expense);
    }
    public void insert(double amount, String merchant,int categoryId) {
        Expense expense = new Expense(amount, merchant, System.currentTimeMillis(), "",categoryId);
//        repository.updateMerchantPreference(merchant,categoryId);
        repository.insert(expense);
    }
    public void setMonth(String yearMonth) {
        selectedMonth.setValue(yearMonth); // e.g., "2025-05"
    }
    public LiveData<List<String>> availableMonths;
    public LiveData<List<String>> getAvailableMonths()
    {
        return availableMonths;
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByMonth(String yearMonth) {
        return repository.getCategoryTotalsByMonth(yearMonth);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByYear(String year) {
        return repository.getCategoryTotalsByYear(year);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByDateRange(long startDate, long endDate) {
        return repository.getCategoryTotalsByDateRange(startDate, endDate);
    }

    public void updateCategoryForMerchant(String merchantName, int newCategoryId) {
        repository.updateCategoryForMerchant(merchantName, newCategoryId);
    }

//    public List<ExpenseWithCategory> getExpensesByCategoryName(String categoryName) {
//        return repository.getExpensesByCategoryName(categoryName);
//    }

//    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndMonth(String category, String yearMonth) {
//        return repository.getExpensesByCategoryAndMonth(category, yearMonth);
//    }
//
//    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndYear(String category, String year) {
//        return repository.getExpensesByCategoryAndYear(category, year);
//    }
//
//    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDateRange(String category, long start, long end) {
//        return repository.getExpensesByCategoryAndDateRange(category, start, end);
//    }
//
//    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDay(String category, long startOfDay, long endOfDay) {
//        return repository.getExpensesByCategoryAndDay(category, startOfDay, endOfDay);
//    }
//
//    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryName(String categoryName) {
//        return repository.getExpensesByCategoryName(categoryName);
//    }

    // inside ExpenseViewModel
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryName(String categoryName) {
        return repository.getExpensesByCategoryName(categoryName);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndMonth(String category, String yearMonth) {
        return repository.getExpensesByCategoryAndMonth(category, yearMonth);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndYear(String category, String year) {
        return repository.getExpensesByCategoryAndYear(category, year);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDateRange(String category, long start, long end) {
        return repository.getExpensesByCategoryAndDateRange(category, start, end);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDay(String category, long startOfDay, long endOfDay) {
        return repository.getExpensesByCategoryAndDay(category, startOfDay, endOfDay);
    }


}

