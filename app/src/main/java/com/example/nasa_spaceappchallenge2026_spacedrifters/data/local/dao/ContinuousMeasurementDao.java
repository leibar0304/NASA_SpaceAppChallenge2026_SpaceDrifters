package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ContinuousMeasurementEntity;

@Dao
public interface ContinuousMeasurementDao {

    @Insert
    long insert(ContinuousMeasurementEntity item);

    @Update
    void update(ContinuousMeasurementEntity item);

    @Delete
    void delete(ContinuousMeasurementEntity item);

    @Query("SELECT * FROM continuous_measurement WHERE astronaut_id = :astronautId AND type = :type " +
           "AND timestamp BETWEEN :from AND :to ORDER BY timestamp")
    LiveData<List<ContinuousMeasurementEntity>> observeRange(long astronautId, String type, long from, long to);

    @Query("SELECT * FROM continuous_measurement WHERE astronaut_id = :astronautId AND type = :type " +
           "ORDER BY timestamp DESC LIMIT 1")
    LiveData<ContinuousMeasurementEntity> observeLatest(long astronautId, String type);

    @Query("SELECT * FROM continuous_measurement WHERE astronaut_id = :astronautId AND type = :type " +
           "AND timestamp BETWEEN :from AND :to ORDER BY timestamp")
    List<ContinuousMeasurementEntity> getRange(long astronautId, String type, long from, long to);

    @Query("DELETE FROM continuous_measurement WHERE timestamp < :limit")
    int deleteOlderThan(long limit);
}
