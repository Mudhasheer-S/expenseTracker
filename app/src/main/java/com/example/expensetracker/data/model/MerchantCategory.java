package com.example.expensetracker.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "merchant_categories", primaryKeys = {"merchant"})
public class MerchantCategory {
    @NonNull
    public String merchant;

    public int categoryId;

    public MerchantCategory(@NonNull String merchant, int categoryId) {
        this.merchant = merchant;
        this.categoryId = categoryId;
    }
}

