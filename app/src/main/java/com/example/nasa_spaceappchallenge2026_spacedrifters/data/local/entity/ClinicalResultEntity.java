package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "clinical_result",
    foreignKeys = {
        @ForeignKey(
            entity = ClinicalTestEntity.class,
            parentColumns = {"id"},
            childColumns = {"test_id"},
            onDelete = ForeignKey.CASCADE),
        @ForeignKey(
            entity = ClinicalParameterEntity.class,
            parentColumns = {"id"},
            childColumns = {"parameter_id"})},
    indices = {@Index(value = {"test_id", "parameter_id"}, unique = true)}
)
public class ClinicalResultEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "test_id")
    public long testId;

    @ColumnInfo(name = "parameter_id")
    public long parameterId;

    @ColumnInfo(name = "value")
    public double value;

    @ColumnInfo(name = "out_of_range")
    public int outOfRange = 0;
}
