package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "neuro_result",
    foreignKeys = {
        @ForeignKey(
            entity = NeuroTestEntity.class,
            parentColumns = {"id"},
            childColumns = {"test_id"},
            onDelete = ForeignKey.CASCADE)}
)
public class NeuroResultEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "test_id")
    public long testId;

    @NonNull
    @ColumnInfo(name = "metric")
    public String metric;

    @ColumnInfo(name = "value")
    public double value;

    @ColumnInfo(name = "unit")
    public String unit;
}
