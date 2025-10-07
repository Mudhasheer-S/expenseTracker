package com.example.expensetracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.database.CategoryDao;
import com.example.expensetracker.data.database.ExpenseDao;
import com.example.expensetracker.data.database.MerchantCategoryDao;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.CategoryTotal;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;
import com.example.expensetracker.data.model.MerchantCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private final ExpenseDao expenseDao;
    private final CategoryDao categoryDao;
    private final MerchantCategoryDao merchantCategoryDao;
    private final LiveData<List<ExpenseWithCategory>> allExpenses;

    private final Application application; // ✅ keep reference

    public ExpenseRepository(Application application) {
        this.application = application; // ✅ store it
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDao = db.expenseDao();
        merchantCategoryDao = db.merchantCategoryDao();
        categoryDao = db.categoryDao();
        allExpenses = expenseDao.getAllExpensesWithCategory();
    }

    // ---------------- INSERT / UPDATE ----------------
    public void insert(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() -> {
            updateMerchantPreference(expense.merchant, expense.categoryId);
            expenseDao.insert(expense);
        });
    }

    public void update(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() -> {
            updateMerchantPreference(expense.merchant, expense.categoryId);
            expenseDao.update(expense);
        });
    }

    // ---------------- MERCHANT PREFERENCE ----------------
    public void updateMerchantPreference(String merchant, int categoryId) {
        Executors.newSingleThreadExecutor().execute(() ->
                merchantCategoryDao.insertOrUpdate(new MerchantCategory(merchant, categoryId)));
    }


    public void updateCategoryForMerchant(String merchant, int categoryId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Update merchant→category mapping
            merchantCategoryDao.insertOrUpdate(new MerchantCategory(merchant, categoryId));
            // Update all past expenses
            expenseDao.updateCategoryForMerchant(merchant, categoryId);
        });
    }


    // ---------------- FETCH DATA ----------------
    public LiveData<List<ExpenseWithCategory>> getAllExpenses() {
        return allExpenses;
    }

    public LiveData<ExpenseWithCategory> getExpensesById(int id) {
        return expenseDao.getExpenseById(id);
    }

    public LiveData<List<ExpenseWithCategory>> getExpensesByMonth(String yearMonth) {
        return expenseDao.getExpensesByMonth(yearMonth);
    }

    public LiveData<List<String>> getAllExpenseMonths() {
        return expenseDao.getAllExpenseMonths();
    }

    public int resolveCategoryIdForMerchant(String merchant) {
        Integer categoryId = merchantCategoryDao.getCategoryIdForMerchant(merchant);

        if (categoryId != null) {
            return categoryId;
        } else {
            // Try to get "Uncategorized" category
            Integer defaultId = categoryDao.getIdByName("Uncategorized");

            if (defaultId == null) {
                categoryDao.insert(new Category("Uncategorized"));
                defaultId = categoryDao.getIdByName("Uncategorized");
            }

            // Save this merchant→category mapping
            merchantCategoryDao.insertOrUpdate(new MerchantCategory(merchant, defaultId));

            return defaultId;
        }
    }

    // ---------------- CATEGORY TOTALS ----------------
    public LiveData<List<CategoryTotal>> getCategoryTotals() {
        return expenseDao.getCategoryTotals();
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByMonth(String yearMonth) {
        return expenseDao.getCategoryTotalsByMonth(yearMonth);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByYear(String year) {
        return expenseDao.getCategoryTotalsByYear(year);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByDateRange(long startDate, long endDate) {
        return expenseDao.getCategoryTotalsByDateRange(startDate, endDate);
    }

    // ---------------- HELPER ----------------
    public Expense findByMerchantAmountAndDate(String merchant, double amount, long date) {
        return expenseDao.findByMerchantAmountAndDate(merchant, amount, date);
    }

    public void updateNotes(int expenseId, String notes) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.updateNotes(expenseId, notes);
        });
    }

    // inside ExpenseRepository
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryName(String categoryName) {
        return expenseDao.getExpensesByCategoryName(categoryName);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndMonth(String categoryName, String yearMonth) {
        return expenseDao.getExpensesByCategoryAndMonth(categoryName, yearMonth);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndYear(String categoryName, String year) {
        return expenseDao.getExpensesByCategoryAndYear(categoryName, year);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDateRange(String categoryName, long start, long end) {
        return expenseDao.getExpensesByCategoryAndDateRange(categoryName, start, end);
    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDay(String categoryName, long startOfDay, long endOfDay) {
        return expenseDao.getExpensesByCategoryAndDay(categoryName, startOfDay, endOfDay);
    }


}
