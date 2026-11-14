package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.NeuroResultEntity;

@Dao
public interface NeuroResultDao {

    @Insert
    long insert(NeuroResultEntity item);

    @Update
    void update(NeuroResultEntity item);

    @Delete
    void delete(NeuroResultEntity item);

    @Query("SELECT * FROM neuro_result WHERE test_id = :testId")
    LiveData<List<NeuroResultEntity>> observeByTest(long testId);

    @Query("SELECT * FROM neuro_result WHERE test_id = :testId")
    List<NeuroResultEntity> getByTest(long testId);
}
