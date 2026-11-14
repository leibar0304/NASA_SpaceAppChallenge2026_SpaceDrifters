package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AlertEntity;

@Dao
public interface AlertDao {

    @Insert
    long insert(AlertEntity item);

    @Update
    void update(AlertEntity item);

    @Delete
    void delete(AlertEntity item);

    @Query("SELECT * FROM alert WHERE astronaut_id = :astronautId ORDER BY generated_at DESC")
    LiveData<List<AlertEntity>> observeByAstronaut(long astronautId);

    @Query("SELECT * FROM alert WHERE astronaut_id = :astronautId AND status = 'ACTIVE' " +
           "ORDER BY generated_at DESC")
    LiveData<List<AlertEntity>> observeActive(long astronautId);

    @Query("SELECT * FROM alert WHERE notified = 0")
    List<AlertEntity> getUnnotified();

    @Query("UPDATE alert SET notified = 1 WHERE id = :id")
    void markNotified(long id);

    @Query("UPDATE alert SET status = 'ACKNOWLEDGED', acknowledged_at = :now, acknowledged_by = :userId " +
           "WHERE id = :id")
    void acknowledge(long id, long now, long userId);
}
