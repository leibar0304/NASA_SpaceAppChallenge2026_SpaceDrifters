package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.AppUserDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.MissionDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.AstronautDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.ContinuousMeasurementDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.EcgRecordingDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.HourlySummaryDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.BloodPressureDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.ClinicalParameterDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.ClinicalTestDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.ClinicalResultDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.NeuroTestTypeDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.NeuroTestDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.NeuroResultDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.AlertThresholdDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.dao.AlertDao;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AppUserEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.MissionEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AstronautEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ContinuousMeasurementEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.EcgRecordingEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.HourlySummaryEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.BloodPressureEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ClinicalParameterEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ClinicalTestEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.ClinicalResultEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.NeuroTestTypeEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.NeuroTestEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.NeuroResultEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AlertThresholdEntity;
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.entity.AlertEntity;
import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
@Database(
    entities = {
        AppUserEntity.class,
        MissionEntity.class,
        AstronautEntity.class,
        ContinuousMeasurementEntity.class,
        EcgRecordingEntity.class,
        HourlySummaryEntity.class,
        BloodPressureEntity.class,
        ClinicalParameterEntity.class,
        ClinicalTestEntity.class,
        ClinicalResultEntity.class,
        NeuroTestTypeEntity.class,
        NeuroTestEntity.class,
        NeuroResultEntity.class,
        AlertThresholdEntity.class,
        AlertEntity.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppUserDao appUserDao();
    public abstract MissionDao missionDao();
    public abstract AstronautDao astronautDao();
    public abstract ContinuousMeasurementDao continuousMeasurementDao();
    public abstract EcgRecordingDao ecgRecordingDao();
    public abstract HourlySummaryDao hourlySummaryDao();
    public abstract BloodPressureDao bloodPressureDao();
    public abstract ClinicalParameterDao clinicalParameterDao();
    public abstract ClinicalTestDao clinicalTestDao();
    public abstract ClinicalResultDao clinicalResultDao();
    public abstract NeuroTestTypeDao neuroTestTypeDao();
    public abstract NeuroTestDao neuroTestDao();
    public abstract NeuroResultDao neuroResultDao();
    public abstract AlertThresholdDao alertThresholdDao();
    public abstract AlertDao alertDao();

    private static volatile AppDatabase INSTANCE;


    public static final ExecutorService io = Executors.newFixedThreadPool(2);

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(
                                context.getApplicationContext(),
                                AppDatabase.class,
                                "astronaut_health.db")
                        // Development only: if the schema version changes, wipe and recreate the DB.
                        .fallbackToDestructiveMigration()
                        .addCallback(new RoomDatabase.Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                io.execute(() -> seed(INSTANCE));
                            }
                        })
                        .build();
            }
        }
        return INSTANCE;
    }

    /** Datos test */
    private static void seed(AppDatabase db) {
        long now = System.currentTimeMillis();

        AppUserEntity admin = new AppUserEntity();
        admin.username = "admin";
        admin.passwordHash = "admin123";   // testing only; store a real hash later
        admin.role = "ADMIN";
        admin.createdAt = now;
        db.appUserDao().insert(admin);

        AppUserEntity user = new AppUserEntity();
        user.username = "astro1";
        user.passwordHash = "astro123";
        user.role = "ASTRONAUT";
        user.createdAt = now;
        long userId = db.appUserDao().insert(user);

        AstronautEntity astronaut = new AstronautEntity();
        astronaut.userId = userId;
        astronaut.firstName = "Ana";
        astronaut.lastName = "García";
        db.astronautDao().insert(astronaut);

        // Esto se les va a aplicar a todos
        AlertThresholdEntity threshold = new AlertThresholdEntity();
        threshold.source = "CONTINUOUS";
        threshold.metric = "HR";
        threshold.maxValue = 120.0;
        threshold.severity = "WARNING";
        db.alertThresholdDao().insert(threshold);
    }
}
