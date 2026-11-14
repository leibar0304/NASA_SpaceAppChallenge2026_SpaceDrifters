package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "mission"
)
public class MissionEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "start_date")
    public long startDate;

    @ColumnInfo(name = "planned_end_date")
    public Long plannedEndDate;
}
