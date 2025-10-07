package com.example.expensetracker.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.expensetracker.data.model.CategoryTotal;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    // ✅ All expenses with category
    @Transaction
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<ExpenseWithCategory>> getAllExpensesWithCategory();

    // ✅ Single expense by ID
    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    LiveData<ExpenseWithCategory> getExpenseById(int id);

    // ✅ All expenses (just Expense model)
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    // ✅ Filter: by Month (format: "2025-09")
    @Transaction
    @Query("SELECT * FROM expenses WHERE strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth ORDER BY date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByMonth(String yearMonth);

    // ✅ Get all unique months (for dropdown)
    @Query("SELECT DISTINCT strftime('%Y-%m', date/1000, 'unixepoch') AS monthYear FROM expenses ORDER BY monthYear DESC")
    LiveData<List<String>> getAllExpenseMonths();

    // ✅ Filter: by Year only
    @Transaction
    @Query("SELECT * FROM expenses WHERE strftime('%Y', date/1000, 'unixepoch') = :year ORDER BY date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByYear(String year);

    // ✅ Filter: by date range (using timestamps)
    @Transaction
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByDateRange(long startDate, long endDate);

    // Total grouped by category (all time)
    // In getCategoryTotals()
    @Query("SELECT c.name AS categoryName, SUM(e.amount) AS total " + // <-- Changed totalAmount to total
            "FROM expenses e INNER JOIN categories c ON e.categoryId = c.id " +
            "GROUP BY c.id ORDER BY total DESC") // <-- Also changed here
    LiveData<List<CategoryTotal>> getCategoryTotals();

    // In getCategoryTotalsByMonth(String yearMonth)
    @Query("SELECT c.name AS categoryName, SUM(e.amount) AS total " + // <-- Changed totalAmount to total
            "FROM expenses e INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE strftime('%Y-%m', e.date / 1000, 'unixepoch') = :yearMonth " +
            "GROUP BY c.id ORDER BY total DESC") // <-- Also changed here
    LiveData<List<CategoryTotal>> getCategoryTotalsByMonth(String yearMonth);

    // In getCategoryTotalsByYear(String year)
    @Query("SELECT c.name AS categoryName, SUM(e.amount) AS total " + // <-- Changed totalAmount to total
            "FROM expenses e INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE strftime('%Y', e.date / 1000, 'unixepoch') = :year " +
            "GROUP BY c.id ORDER BY total DESC") // <-- Also changed here
    LiveData<List<CategoryTotal>> getCategoryTotalsByYear(String year);

    // In getCategoryTotalsByDateRange(long startDate, long endDate)
    @Query("SELECT c.name AS categoryName, SUM(e.amount) AS total " + // <-- Changed totalAmount to total
            "FROM expenses e INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE e.date BETWEEN :startDate AND :endDate " +
            "GROUP BY c.id ORDER BY total DESC") // <-- Also changed here
    LiveData<List<CategoryTotal>> getCategoryTotalsByDateRange(long startDate, long endDate);


    @Query("SELECT * FROM expenses WHERE merchant = :merchant AND amount = :amount AND date = :date LIMIT 1")
    Expense findByMerchantAmountAndDate(String merchant, double amount, long date);


    @Query("SELECT * FROM expenses WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<List<Expense>> getExpensesByDay(long startOfDay, long endOfDay);

    @Query("UPDATE expenses SET notes = :notes WHERE id = :expenseId")
    void updateNotes(int expenseId, String notes);

    @Query("UPDATE expenses SET categoryId = :newCategoryId WHERE merchant = :merchantName")
    void updateCategoryForMerchant(String merchantName, int newCategoryId);

    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
            "INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE e.categoryId = :categoryId")
    List<ExpenseWithCategory> getExpensesByCategoryIdSync(int categoryId);


    // By category + month
//    @Transaction
//    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
//            "INNER JOIN categories c ON e.categoryId = c.id " +
//            "WHERE c.name = :categoryName " +
//            "AND strftime('%Y-%m', e.date / 1000, 'unixepoch') = :yearMonth " +
//            "ORDER BY e.date DESC")
//    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndMonth(String categoryName, String yearMonth);
//
//    // By category + year
//    @Transaction
//    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
//            "INNER JOIN categories c ON e.categoryId = c.id " +
//            "WHERE c.name = :categoryName " +
//            "AND strftime('%Y', e.date / 1000, 'unixepoch') = :year " +
//            "ORDER BY e.date DESC")
//    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndYear(String categoryName, String year);
//
//    // By category + custom date range
//    @Transaction
//    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
//            "INNER JOIN categories c ON e.categoryId = c.id " +
//            "WHERE c.name = :categoryName " +
//            "AND e.date BETWEEN :startDate AND :endDate " +
//            "ORDER BY e.date DESC")
//    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDateRange(String categoryName, long startDate, long endDate);

    // By category + single day
//    @Transaction
//    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
//            "INNER JOIN categories c ON e.categoryId = c.id " +
//            "WHERE c.name = :categoryName " +
//            "AND e.date BETWEEN :startOfDay AND :endOfDay " +
//            "ORDER BY e.date DESC")
//    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDay(String categoryName, long startOfDay, long endOfDay);
//
//
//    @Query("SELECT e.*, c.name AS categoryName " +
//            "FROM expenses e " +
//            "INNER JOIN categories c ON e.categoryId = c.id " +
//            "WHERE c.name = :categoryName " +
//            "ORDER BY e.date DESC")
//    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryName(String categoryName);

    // inside ExpenseDao
    @Transaction
    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
            "INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE c.name = :categoryName " +
            "ORDER BY e.date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryName(String categoryName);

    @Transaction
    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
            "INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE c.name = :categoryName " +
            "AND strftime('%Y-%m', e.date / 1000, 'unixepoch') = :yearMonth " +
            "ORDER BY e.date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndMonth(String categoryName, String yearMonth);

    @Transaction
    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
            "INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE c.name = :categoryName " +
            "AND strftime('%Y', e.date / 1000, 'unixepoch') = :year " +
            "ORDER BY e.date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndYear(String categoryName, String year);

    @Transaction
    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
            "INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE c.name = :categoryName " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "ORDER BY e.date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDateRange(String categoryName, long startDate, long endDate);

    @Transaction
    @Query("SELECT e.*, c.name AS categoryName FROM expenses e " +
            "INNER JOIN categories c ON e.categoryId = c.id " +
            "WHERE c.name = :categoryName " +
            "AND e.date BETWEEN :startOfDay AND :endOfDay " +
            "ORDER BY e.date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByCategoryAndDay(String categoryName, long startOfDay, long endOfDay);

}
