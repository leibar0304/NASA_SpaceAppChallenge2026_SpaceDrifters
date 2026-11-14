package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ClinicalTestEntity;

@Dao
public interface ClinicalTestDao {

    @Insert
    long insert(ClinicalTestEntity item);

    @Update
    void update(ClinicalTestEntity item);

    @Delete
    void delete(ClinicalTestEntity item);

    @Query("SELECT * FROM clinical_test WHERE astronaut_id = :astronautId ORDER BY performed_at DESC")
    LiveData<List<ClinicalTestEntity>> observeByAstronaut(long astronautId);
}
