package com.example.mynotes;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Database;

import java.util.List;


public class MainViewModel extends AndroidViewModel {

    private static NotesDatabase database;
    private LiveData<List<Note>> notes;


    public MainViewModel(@NonNull Application application) {
        super(application);
        database = NotesDatabase.getInstance(getApplication());
        notes = database.noteDao().notes();
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }
    public void insertNote(Note note){
        new InsertTask().execute(note);
    }
    public void deleteNote(Note note){
        new DeleteTask().execute(note);
    }
    public void deleteAllNotes(){
        new DeleteAllNotes().execute();
    }

    private static class InsertTask extends AsyncTask<Note, Void, Void>{
        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0){
                database.noteDao().insertNote(notes[0]);
            }
            return null;
        }
    }
    private static class DeleteTask extends AsyncTask<Note, Void, Void>{
        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0){
                database.noteDao().deleteNote(notes[0]);
            }
            return null;
        }
    }
    private static class DeleteAllNotes extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            database.noteDao().deleteAllNotes();
            return null;
        }
    }
}
