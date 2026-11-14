package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.BloodPressureEntity;

@Dao
public interface BloodPressureDao {

    @Insert
    long insert(BloodPressureEntity item);

    @Update
    void update(BloodPressureEntity item);

    @Delete
    void delete(BloodPressureEntity item);

    @Query("SELECT * FROM blood_pressure WHERE astronaut_id = :astronautId ORDER BY scheduled_for DESC")
    LiveData<List<BloodPressureEntity>> observeByAstronaut(long astronautId);

    @Query("SELECT * FROM blood_pressure WHERE astronaut_id = :astronautId " +
           "AND status = 'PENDING' AND scheduled_for <= :now ORDER BY scheduled_for")
    List<BloodPressureEntity> getPending(long astronautId, long now);
}
