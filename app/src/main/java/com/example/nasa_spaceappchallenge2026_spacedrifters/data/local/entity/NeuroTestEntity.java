package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "neuro_test",
    foreignKeys = {
        @ForeignKey(
            entity = AstronautEntity.class,
            parentColumns = {"id"},
            childColumns = {"astronaut_id"},
            onDelete = ForeignKey.CASCADE),
        @ForeignKey(
            entity = NeuroTestTypeEntity.class,
            parentColumns = {"id"},
            childColumns = {"type_id"})},
    indices = {@Index(value = {"astronaut_id", "type_id", "performed_at"})}
)
public class NeuroTestEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "astronaut_id")
    public long astronautId;

    @ColumnInfo(name = "type_id")
    public long typeId;

    @ColumnInfo(name = "performed_at")
    public long performedAt;

    @ColumnInfo(name = "duration_s")
    public Integer durationS;

    @ColumnInfo(name = "score")
    public Double score;

    @ColumnInfo(name = "notes")
    public String notes;
}
