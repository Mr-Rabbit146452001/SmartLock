package com.example.smartlock.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.smartlock.models.LockHistory;
import com.example.smartlock.models.MasterPin;
import com.example.smartlock.models.User;

@Database(entities = {User.class, MasterPin.class, LockHistory.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract MasterPinDao masterPinDao();
    public abstract LockHistoryDao lockHistoryDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smartlock_database"
                    ).build();
                }
            }
        }
        return instance;
    }
}