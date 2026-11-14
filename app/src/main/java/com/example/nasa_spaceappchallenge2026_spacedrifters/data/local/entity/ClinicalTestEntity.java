package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "clinical_test",
    foreignKeys = {
        @ForeignKey(
            entity = AstronautEntity.class,
            parentColumns = {"id"},
            childColumns = {"astronaut_id"},
            onDelete = ForeignKey.CASCADE)},
    indices = {@Index(value = {"astronaut_id", "performed_at"})}
)
public class ClinicalTestEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "astronaut_id")
    public long astronautId;

    @NonNull
    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "performed_at")
    public long performedAt;

    @ColumnInfo(name = "notes")
    public String notes;
}
