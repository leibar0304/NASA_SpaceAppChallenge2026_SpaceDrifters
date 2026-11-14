package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
    tableName = "hourly_summary",
    foreignKeys = {
        @ForeignKey(
            entity = AstronautEntity.class,
            parentColumns = {"id"},
            childColumns = {"astronaut_id"},
            onDelete = ForeignKey.CASCADE)},
    primaryKeys = {"astronaut_id", "type", "hour_start"}
)
public class HourlySummaryEntity {

    @ColumnInfo(name = "astronaut_id")
    public long astronautId;

    @NonNull
    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "hour_start")
    public long hourStart;

    @ColumnInfo(name = "avg_value")
    public double avgValue;

    @ColumnInfo(name = "min_value")
    public double minValue;

    @ColumnInfo(name = "max_value")
    public double maxValue;

    @ColumnInfo(name = "sample_count")
    public int sampleCount;
}
