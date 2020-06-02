package com.youssefdirani.advertise_admin;

import android.provider.ContactsContract;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                NavHeaderEntity.class,
                NavEntity.class,
                DatabaseInfo.class
        },
        version = 1,
        exportSchema = false )
public abstract class AppDatabase extends RoomDatabase {
    public abstract PermanentDao permanentDao();
}

