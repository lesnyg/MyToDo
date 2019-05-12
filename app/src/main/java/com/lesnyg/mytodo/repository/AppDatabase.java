package com.lesnyg.mytodo.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Todo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TodoDao todoDao();
    private static AppDatabase db;
    //싱글턴
    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context,
                    AppDatabase.class, "db")
                    .allowMainThreadQueries()   //Main 쓰레드 사용 ok
                    .fallbackToDestructiveMigration()   //스키마 변경 ok
                    .build();
        }
        return db;
    }
}
