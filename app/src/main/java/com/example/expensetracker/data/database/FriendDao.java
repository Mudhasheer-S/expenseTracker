package com.example.expensetracker.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.model.Friend;

import java.util.List;

@Dao
public interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Friend friend);

    @Update
    void update(Friend friend);

    @Query("SELECT * FROM friends ORDER BY name ASC")
    LiveData<List<Friend>> getAllFriends();

    @Query("SELECT * FROM friends WHERE id = :id LIMIT 1")
    Friend getFriendById(int id);

    @Query("UPDATE friends SET totalDue = totalDue + :amount WHERE id = :friendId")
    void updateDueAmount(int friendId, double amount);

    @Query("UPDATE friends SET totalDue = (SELECT IFNULL(SUM(shareAmount), 0) FROM split_expense WHERE friendId = :friendId AND isPaid = 0) WHERE id = :friendId")
    void recalculateTotalDue(int friendId);

    @Query("UPDATE friends SET totalDue = totalDue + :amount WHERE id = :friendId")
    void updateTotalDue(int friendId, double amount);

    @Query("SELECT * FROM friends ORDER BY name ASC")
    List<Friend> getAllFriendsDirect();

}
