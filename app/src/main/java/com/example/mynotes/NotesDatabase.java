package com.example.mynotes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 2, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase database;
    private static Object LOCK = new Object();
    private static final String name = "notes.db";

    public static NotesDatabase getInstance(Context context){
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, NotesDatabase.class, name).fallbackToDestructiveMigration().build();
            }
        }
        return database;
    }

    public abstract NoteDao noteDao();
}
