package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AlertThresholdEntity;

@Dao
public interface AlertThresholdDao {

    @Insert
    long insert(AlertThresholdEntity item);

    @Update
    void update(AlertThresholdEntity item);

    @Delete
    void delete(AlertThresholdEntity item);

    // Thresholds that apply to an astronaut: their own + the global ones (astronaut_id NULL)
    @Query("SELECT * FROM alert_threshold WHERE active = 1 AND source = :source AND metric = :metric " +
           "AND (astronaut_id = :astronautId OR astronaut_id IS NULL)")
    List<AlertThresholdEntity> getApplicable(long astronautId, String source, String metric);

    @Query("SELECT * FROM alert_threshold ORDER BY source, metric")
    LiveData<List<AlertThresholdEntity>> observeAll();
}
