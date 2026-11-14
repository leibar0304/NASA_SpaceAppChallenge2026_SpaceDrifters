package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.HourlySummaryEntity;

@Dao
public interface HourlySummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HourlySummaryEntity item);

    @Update
    void update(HourlySummaryEntity item);

    @Delete
    void delete(HourlySummaryEntity item);

    @Query("SELECT * FROM hourly_summary WHERE astronaut_id = :astronautId AND type = :type " +
           "AND hour_start BETWEEN :from AND :to ORDER BY hour_start")
    LiveData<List<HourlySummaryEntity>> observeRange(long astronautId, String type, long from, long to);
}
