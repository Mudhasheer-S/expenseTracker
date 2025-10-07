package com.example.expensetracker.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.model.CategoryTotal;
import com.example.expensetracker.data.repository.ExpenseRepository;

import java.util.List;

public class ExpenseStatsViewModel extends AndroidViewModel {
    private final ExpenseRepository repository;

    public ExpenseStatsViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotals() {
        return repository.getCategoryTotals();
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByMonth(String yearMonth) {
        return repository.getCategoryTotalsByMonth(yearMonth);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByYear(String year) {
        return repository.getCategoryTotalsByYear(year);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByDateRange(long start, long end) {
        return repository.getCategoryTotalsByDateRange(start, end);
    }

    public void updateNotes(int expenseId, String notes) {
        repository.updateNotes(expenseId, notes);
    }


}
