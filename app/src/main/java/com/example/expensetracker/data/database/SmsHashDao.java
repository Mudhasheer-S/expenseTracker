package com.example.expensetracker.data.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.expensetracker.data.model.SmsHash;

import java.util.List;

@Dao
public interface SmsHashDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SmsHash hash);

    @Query("SELECT hash FROM sms_hashes")
    List<String> getAllHashes();
}
