package com.example.expensetracker.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.database.FriendDao;
import com.example.expensetracker.data.database.SplitExpenseDao;
import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.model.SplitExpense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplitExpenseRepository {
    private final SplitExpenseDao splitExpenseDao;

    private final FriendDao friendDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SplitExpenseRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        splitExpenseDao = db.splitExpenseDao();
        friendDao = db.friendDao();
    }

    public void insert(SplitExpense splitExpense) {
        splitExpense.shareAmount = Math.round(splitExpense.shareAmount * 100.0) / 100.0;
        executor.execute(() -> {
            splitExpenseDao.insert(splitExpense);
            friendDao.recalculateTotalDue(splitExpense.friendId);
        });
    }

    public void update(SplitExpense splitExpense) {
        splitExpense.shareAmount = Math.round(splitExpense.shareAmount * 100.0) / 100.0;
        executor.execute(() -> splitExpenseDao.update(splitExpense));
    }

    public LiveData<List<SplitExpense>> getExpensesForFriend(int friendId) {
        return splitExpenseDao.getExpensesForFriend(friendId);
    }


    public LiveData<Double> getPendingAmountForFriend(int friendId) {
        return splitExpenseDao.getPendingAmountForFriend(friendId);
    }

    public void markAsPaid(int id, int friendId) {
        executor.execute(() -> {
            splitExpenseDao.markAsPaid(id);
            friendDao.recalculateTotalDue(friendId);
        });
    }


    public LiveData<List<SplitExpense>> getSplitsForExpense(int expenseId) {
        return splitExpenseDao.getSplitsForExpense(expenseId);
    }

    public void insertSplit(SplitExpense splitExpense) {
        splitExpense.shareAmount = Math.round(splitExpense.shareAmount * 100.0) / 100.0;
        executor.execute(() -> {
            splitExpenseDao.insert(splitExpense);
            friendDao.updateTotalDue(splitExpense.friendId, splitExpense.shareAmount);
        });
    }

    public void markAsPaid(int splitId) {
        executor.execute(() -> splitExpenseDao.markAsPaid(splitId));
    }

}
