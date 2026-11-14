package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.NeuroTestEntity;

@Dao
public interface NeuroTestDao {

    @Insert
    long insert(NeuroTestEntity item);

    @Update
    void update(NeuroTestEntity item);

    @Delete
    void delete(NeuroTestEntity item);

    @Query("SELECT * FROM neuro_test WHERE astronaut_id = :astronautId ORDER BY performed_at DESC")
    LiveData<List<NeuroTestEntity>> observeByAstronaut(long astronautId);

    @Query("SELECT * FROM neuro_test WHERE astronaut_id = :astronautId AND type_id = :typeId " +
           "ORDER BY performed_at")
    LiveData<List<NeuroTestEntity>> observeByAstronautAndType(long astronautId, long typeId);
}
