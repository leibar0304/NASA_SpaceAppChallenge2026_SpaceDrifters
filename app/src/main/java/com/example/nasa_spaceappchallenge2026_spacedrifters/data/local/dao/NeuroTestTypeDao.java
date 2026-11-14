package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.NeuroTestTypeEntity;

@Dao
public interface NeuroTestTypeDao {

    @Insert
    long insert(NeuroTestTypeEntity item);

    @Update
    void update(NeuroTestTypeEntity item);

    @Delete
    void delete(NeuroTestTypeEntity item);

    @Query("SELECT * FROM neuro_test_type WHERE code = :code LIMIT 1")
    NeuroTestTypeEntity findByCode(String code);

    @Query("SELECT * FROM neuro_test_type ORDER BY name")
    LiveData<List<NeuroTestTypeEntity>> observeAll();
}
