package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "blood_pressure",
    foreignKeys = {
        @ForeignKey(
            entity = AstronautEntity.class,
            parentColumns = {"id"},
            childColumns = {"astronaut_id"},
            onDelete = ForeignKey.CASCADE)},
    indices = {@Index(value = {"astronaut_id", "scheduled_for"})}
)
public class BloodPressureEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "astronaut_id")
    public long astronautId;

    @ColumnInfo(name = "scheduled_for")
    public Long scheduledFor;

    @ColumnInfo(name = "taken_at")
    public Long takenAt;

    @ColumnInfo(name = "systolic")
    public Integer systolic;

    @ColumnInfo(name = "diastolic")
    public Integer diastolic;

    @ColumnInfo(name = "pulse")
    public Integer pulse;

    @NonNull
    @ColumnInfo(name = "status")
    public String status = "PENDING";
}
