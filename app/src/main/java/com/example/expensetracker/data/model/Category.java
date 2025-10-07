package com.example.expensetracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;

//    @ColumnInfo(name = "name")
    public String name;

    public Category(String name) {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
    @Override
    public String toString() {
        return name; // this is what Spinner shows
    }
}
