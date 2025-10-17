package com.example.expensetracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friends")
public class Friend {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;       // Friend's name
    public double totalDue;   // How much they owe you



}
