package com.example.expensetracker.data.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sms_hashes")
public class SmsHash {
    @PrimaryKey
    @NonNull
    public String hash;
    public SmsHash(String hash)
    {
        this.hash=hash;
    }
}
