package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "alert",
    foreignKeys = {
        @ForeignKey(
            entity = AstronautEntity.class,
            parentColumns = {"id"},
            childColumns = {"astronaut_id"},
            onDelete = ForeignKey.CASCADE),
        @ForeignKey(
            entity = AlertThresholdEntity.class,
            parentColumns = {"id"},
            childColumns = {"threshold_id"},
            onDelete = ForeignKey.SET_NULL),
        @ForeignKey(
            entity = AppUserEntity.class,
            parentColumns = {"id"},
            childColumns = {"acknowledged_by"},
            onDelete = ForeignKey.SET_NULL)},
    indices = {@Index(value = {"astronaut_id", "status", "generated_at"})}
)
public class AlertEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "astronaut_id")
    public long astronautId;

    @ColumnInfo(name = "threshold_id")
    public Long thresholdId;

    @NonNull
    @ColumnInfo(name = "source")
    public String source;

    @ColumnInfo(name = "source_record_id")
    public Long sourceRecordId;

    @NonNull
    @ColumnInfo(name = "metric")
    public String metric;

    @ColumnInfo(name = "value")
    public Double value;

    @ColumnInfo(name = "message")
    public String message;

    @NonNull
    @ColumnInfo(name = "severity")
    public String severity;

    @ColumnInfo(name = "generated_at")
    public long generatedAt;

    @ColumnInfo(name = "notified")
    public int notified = 0;

    @NonNull
    @ColumnInfo(name = "status")
    public String status = "ACTIVE";

    @ColumnInfo(name = "acknowledged_at")
    public Long acknowledgedAt;

    @ColumnInfo(name = "acknowledged_by")
    public Long acknowledgedBy;

    @ColumnInfo(name = "notes")
    public String notes;
}
