package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "clinical_parameter",
    indices = {@Index(value = {"code"}, unique = true)}
)
public class ClinicalParameterEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @NonNull
    @ColumnInfo(name = "code")
    public String code;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "unit")
    public String unit;

    @ColumnInfo(name = "ref_min")
    public Double refMin;

    @ColumnInfo(name = "ref_max")
    public Double refMax;
}
