package com.example.expensetracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.database.CategoryDao;
import com.example.expensetracker.data.database.ExpenseDao;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.Expense;

import java.util.List;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategory;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
        allCategory = categoryDao.getAllCategories();
    }

    public void insert(Category category) {
        Executors.newSingleThreadExecutor().execute(() -> categoryDao.insert(category));
    }

    public LiveData<List<Category>> getAllCategory() {
        return allCategory;
    }
    public List<Category> getAllCategoriesStatic() {
        return categoryDao.getAllCategoriesStatic();
    }
}
