package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "app_user",
    indices = {@Index(value = {"username"}, unique = true)}
)
public class AppUserEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    @NonNull
    @ColumnInfo(name = "password_hash")
    public String passwordHash;

    @NonNull
    @ColumnInfo(name = "role")
    public String role;

    @ColumnInfo(name = "active")
    public int active = 1;

    @ColumnInfo(name = "created_at")
    public long createdAt;
}
