package com.example.expensetracker.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.model.SplitExpense;

import java.util.List;

@Dao
public interface SplitExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SplitExpense splitExpense);

    @Update
    void update(SplitExpense splitExpense);

    @Query("SELECT * FROM split_expense WHERE friendId = :friendId ORDER BY date DESC")
    LiveData<List<SplitExpense>> getExpensesForFriend(int friendId);

    @Query("UPDATE split_expense SET isPaid = 1 WHERE id = :id")
    void markAsPaid(int id);

    @Query("SELECT SUM(shareAmount) FROM split_expense WHERE friendId = :friendId AND isPaid = 0")
    LiveData<Double> getPendingAmountForFriend(int friendId);


    @Query("SELECT * FROM split_expense WHERE expenseId = :expenseId")
    LiveData<List<SplitExpense>> getSplitsForExpense(int expenseId);


    @Query("SELECT SUM(shareAmount) FROM split_expense WHERE expenseId = :expenseId")
    double getTotalSplitAmount(int expenseId);

}
