package com.example.expensetracker.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.model.Friend;
import com.example.expensetracker.data.repository.FriendRepository;

import java.util.List;

public class FriendViewModel extends AndroidViewModel {
    private final FriendRepository repository;
    private final LiveData<List<Friend>> allFriends;

    public FriendViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendRepository(application);
        allFriends = repository.getAllFriends();
    }

    public LiveData<List<Friend>> getAllFriends() {
        return allFriends;
    }

    public void insertFriend(Friend friend) {
        repository.insert(friend);
    }

    public String getFriendNameById(int friendId) {
        return repository.getFriendNameById(friendId);
    }

    public List<Friend> getAllFriendsStatic() {
        return repository.getAllFriendsStatic();
    }
}
