package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "alert_threshold",
    foreignKeys = {
        @ForeignKey(
            entity = AstronautEntity.class,
            parentColumns = {"id"},
            childColumns = {"astronaut_id"},
            onDelete = ForeignKey.CASCADE)},
    indices = {@Index(value = {"astronaut_id"})}
)
public class AlertThresholdEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "astronaut_id")
    public Long astronautId;

    @NonNull
    @ColumnInfo(name = "source")
    public String source;

    @NonNull
    @ColumnInfo(name = "metric")
    public String metric;

    @ColumnInfo(name = "min_value")
    public Double minValue;

    @ColumnInfo(name = "max_value")
    public Double maxValue;

    @NonNull
    @ColumnInfo(name = "severity")
    public String severity;

    @ColumnInfo(name = "active")
    public int active = 1;
}
