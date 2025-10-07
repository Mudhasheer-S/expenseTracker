package com.example.expensetracker.ui.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.repository.CategoryRepository;
import com.example.expensetracker.data.repository.ExpenseRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository repository;

    private LiveData<List<Category>> allCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
        allCategories = repository.getAllCategory();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public List<Category> getAllCategoriesStatic() {
        return repository.getAllCategoriesStatic();
    }


    public void insert(Category category) {
        repository.insert(category);
    }
}

