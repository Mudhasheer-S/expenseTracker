package com.example.expensetracker.data.model;

import androidx.room.ColumnInfo;

public class CategoryTotal {
    @ColumnInfo(name = "categoryName")
    public String category;
    public double total;

    public CategoryTotal(String category, double total) {
        this.category = category;
        this.total = Math.round(total*100.0)/100.0;
    }
}

