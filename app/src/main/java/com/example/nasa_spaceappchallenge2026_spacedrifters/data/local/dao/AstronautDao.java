package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AstronautEntity;

@Dao
public interface AstronautDao {

    @Insert
    long insert(AstronautEntity item);

    @Update
    void update(AstronautEntity item);

    @Delete
    void delete(AstronautEntity item);

    @Query("SELECT * FROM astronaut WHERE user_id = :userId LIMIT 1")
    AstronautEntity findByUserId(long userId);

    @Query("SELECT * FROM astronaut WHERE user_id = :userId LIMIT 1")
    LiveData<AstronautEntity> observeByUserId(long userId);

    @Query("SELECT * FROM astronaut ORDER BY last_name, first_name")
    LiveData<List<AstronautEntity>> observeAll();
}
