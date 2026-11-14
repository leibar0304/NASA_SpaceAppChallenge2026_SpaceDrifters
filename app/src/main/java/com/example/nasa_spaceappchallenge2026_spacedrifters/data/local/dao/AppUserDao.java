package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AppUserEntity;

@Dao
public interface AppUserDao {

    @Insert
    long insert(AppUserEntity item);

    @Update
    void update(AppUserEntity item);

    @Delete
    void delete(AppUserEntity item);

    @Query("SELECT * FROM app_user WHERE username = :username LIMIT 1")
    AppUserEntity findByUsername(String username);

    @Query("SELECT * FROM app_user ORDER BY username")
    LiveData<List<AppUserEntity>> observeAll();
}
