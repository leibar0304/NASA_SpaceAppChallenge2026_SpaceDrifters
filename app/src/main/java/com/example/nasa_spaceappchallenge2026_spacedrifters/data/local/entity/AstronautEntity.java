package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "astronaut",
    foreignKeys = {
        @ForeignKey(
            entity = AppUserEntity.class,
            parentColumns = {"id"},
            childColumns = {"user_id"},
            onDelete = ForeignKey.CASCADE),
        @ForeignKey(
            entity = MissionEntity.class,
            parentColumns = {"id"},
            childColumns = {"mission_id"},
            onDelete = ForeignKey.SET_NULL)},
    indices = {@Index(value = {"user_id"}), @Index(value = {"mission_id"})}
)
public class AstronautEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "mission_id")
    public Long missionId;

    @NonNull
    @ColumnInfo(name = "first_name")
    public String firstName;

    @NonNull
    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "birth_date")
    public Long birthDate;

    @ColumnInfo(name = "sex")
    public String sex;

    @ColumnInfo(name = "height_cm")
    public Double heightCm;

    @ColumnInfo(name = "base_weight_kg")
    public Double baseWeightKg;
}
