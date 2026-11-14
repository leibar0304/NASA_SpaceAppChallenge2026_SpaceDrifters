package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.MissionEntity;

@Dao
public interface MissionDao {

    @Insert
    long insert(MissionEntity item);

    @Update
    void update(MissionEntity item);

    @Delete
    void delete(MissionEntity item);

    @Query("SELECT * FROM mission ORDER BY start_date DESC")
    LiveData<List<MissionEntity>> observeAll();
}
