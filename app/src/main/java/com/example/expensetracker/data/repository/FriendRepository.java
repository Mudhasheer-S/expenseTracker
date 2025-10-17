package com.example.expensetracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.database.FriendDao;
import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.model.Friend;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FriendRepository {
    private final FriendDao friendDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FriendRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        friendDao = db.friendDao();
    }

    public LiveData<List<Friend>> getAllFriends() {
        return friendDao.getAllFriends();
    }

    public void insert(Friend friend) {
        friend.totalDue = Math.round(friend.totalDue * 100.0) / 100.0;
        executor.execute(() -> friendDao.insert(friend));
    }

    public void update(Friend friend) {
        friend.totalDue = Math.round(friend.totalDue * 100.0) / 100.0;
        executor.execute(() -> friendDao.update(friend));
    }

    public void updateDueAmount(int friendId, double amount) {
        executor.execute(() -> friendDao.updateDueAmount(friendId, amount));
    }

    public List<Friend> getAllFriendsStatic() {
        return friendDao.getAllFriendsDirect();
    }

    public String getFriendNameById(int friendId) {
        return friendDao.getFriendById(friendId).name;
    }
}
