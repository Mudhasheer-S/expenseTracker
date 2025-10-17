package com.example.expensetracker.data.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "split_expense")
public class SplitExpense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int expenseId;      // Link to your Expense record
    public int friendId;       // Link to friend table
    public double shareAmount; // Each person’s share
    public String note;        // Description like "Dinner"
    public long date;          // Timestamp
    public boolean isPaid;     // Mark as paid or not
}
