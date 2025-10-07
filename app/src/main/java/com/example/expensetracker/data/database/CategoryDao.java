package com.example.expensetracker.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.Expense;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Category category);
    @Insert
    void insertAll(List<Category> categories);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();
    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategoriesStatic();
    @Query("SELECT id FROM categories WHERE name = :name LIMIT 1")
    Integer getIdByName(String name);
}

