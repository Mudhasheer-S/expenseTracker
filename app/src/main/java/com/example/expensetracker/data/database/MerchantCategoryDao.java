package com.example.expensetracker.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.expensetracker.data.model.MerchantCategory;

@Dao
public interface MerchantCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(MerchantCategory mc);

    @Query("SELECT categoryId FROM merchant_categories WHERE merchant = :merchant LIMIT 1")
    Integer getCategoryIdForMerchant(String merchant);
}

