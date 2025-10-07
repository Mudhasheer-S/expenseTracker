package com.example.expensetracker.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ExpenseWithCategory {
    @Embedded
    public Expense expense;

    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    public Category category;
}
