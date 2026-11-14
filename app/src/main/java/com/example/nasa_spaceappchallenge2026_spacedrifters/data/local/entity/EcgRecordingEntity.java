package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Arrays;

@Entity(
    tableName = "ecg_recording",
    foreignKeys = {
        @ForeignKey(
            entity = AstronautEntity.class,
            parentColumns = {"id"},
            childColumns = {"astronaut_id"},
            onDelete = ForeignKey.CASCADE)},
    indices = {@Index(value = {"astronaut_id", "start_time"})}
)
public class EcgRecordingEntity {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    @ColumnInfo(name = "astronaut_id")
    public long astronautId;

    @ColumnInfo(name = "start_time")
    public long startTime;

    @ColumnInfo(name = "duration_s")
    public int durationS;

    @ColumnInfo(name = "sampling_rate_hz")
    public int samplingRateHz;

    @NonNull
    @ColumnInfo(name = "signal")
    public byte[] signal;

    @ColumnInfo(name = "mean_hr")
    public Double meanHr;

    @ColumnInfo(name = "classification")
    public String classification;

    @ColumnInfo(name = "source")
    public String source;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EcgRecordingEntity that = (EcgRecordingEntity) o;
        return id == that.id && Arrays.equals(signal, that.signal);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Arrays.hashCode(signal);
        return result;
    }
}
