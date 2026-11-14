package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.EcgRecordingEntity;

@Dao
public interface EcgRecordingDao {

    @Insert
    long insert(EcgRecordingEntity item);

    @Update
    void update(EcgRecordingEntity item);

    @Delete
    void delete(EcgRecordingEntity item);

    @Query("SELECT * FROM ecg_recording WHERE astronaut_id = :astronautId ORDER BY start_time DESC")
    LiveData<List<EcgRecordingEntity>> observeByAstronaut(long astronautId);

    @Query("SELECT * FROM ecg_recording WHERE id = :id")
    EcgRecordingEntity findById(long id);
}
