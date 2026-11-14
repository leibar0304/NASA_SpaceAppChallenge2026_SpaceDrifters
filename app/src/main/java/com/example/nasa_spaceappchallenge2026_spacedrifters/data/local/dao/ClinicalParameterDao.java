package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ClinicalParameterEntity;

@Dao
public interface ClinicalParameterDao {

    @Insert
    long insert(ClinicalParameterEntity item);

    @Update
    void update(ClinicalParameterEntity item);

    @Delete
    void delete(ClinicalParameterEntity item);

    @Query("SELECT * FROM clinical_parameter WHERE code = :code LIMIT 1")
    ClinicalParameterEntity findByCode(String code);

    @Query("SELECT * FROM clinical_parameter ORDER BY category, name")
    LiveData<List<ClinicalParameterEntity>> observeAll();
}
