package com.example.expensetracker.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE))
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String merchant;
    public long date;
    public String source;

    @ColumnInfo(index = true)
    public int categoryId;

    // New fields
    public String type;    // Credit or Debit
    public String account; // Account number
    public String transactionId;

    // ✅ Added notes
    @ColumnInfo(name = "notes")
    public String notes;

    public Expense(double amount, String merchant, long date, String source, int categoryId) {
        this.amount = amount;
        this.merchant = merchant;
        this.date = date;
        this.source = source;
        this.categoryId = categoryId;
    }

    // Empty constructor needed for Room and parsers
    @Ignore
    public Expense() {
    }

    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getTransactionId() { return transactionId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
