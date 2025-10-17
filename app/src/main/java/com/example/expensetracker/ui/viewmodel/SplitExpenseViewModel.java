package com.example.expensetracker.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.model.SplitExpense;
import com.example.expensetracker.data.repository.SplitExpenseRepository;

import java.util.List;

public class SplitExpenseViewModel extends AndroidViewModel {
    private final SplitExpenseRepository repository;

    public SplitExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new SplitExpenseRepository(application);
    }

    public LiveData<List<SplitExpense>> getSplitsForFriend(int friendId) {
        return repository.getExpensesForFriend(friendId);
    }

    public void addSplit(SplitExpense splitExpense) {
        repository.insert(splitExpense);
    }

    public void markAsPaid(int id,int friendId) {
        repository.markAsPaid(id,friendId);
    }

    public LiveData<Double> getPendingAmountForFriend(int friendId) {
        return repository.getPendingAmountForFriend(friendId);
    }

    public LiveData<List<SplitExpense>> getSplitsForExpense(int expenseId) {
        return repository.getSplitsForExpense(expenseId);
    }

    public void insertSplit(SplitExpense splitExpense) {
        repository.insertSplit(splitExpense);
    }

    public void markAsPaid(int splitId) {
        repository.markAsPaid(splitId);
    }

}
