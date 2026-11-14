package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ClinicalResultEntity;

@Dao
public interface ClinicalResultDao {

    @Insert
    long insert(ClinicalResultEntity item);

    @Update
    void update(ClinicalResultEntity item);

    @Delete
    void delete(ClinicalResultEntity item);

    @Query("SELECT * FROM clinical_result WHERE test_id = :testId")
    LiveData<List<ClinicalResultEntity>> observeByTest(long testId);

    @Query("SELECT * FROM clinical_result WHERE test_id = :testId")
    List<ClinicalResultEntity> getByTest(long testId);
}
