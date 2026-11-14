package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "neuro_test_type",
    indices = {@Index(value = {"code"}, unique = true)}
)
public class NeuroTestTypeEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @NonNull
    @ColumnInfo(name = "code")
    public String code;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "domain")
    public String domain;
}
