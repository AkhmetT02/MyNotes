package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.RoomDatabase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView noteRv;
    private TextView taskNot_Exists;
    private FloatingActionButton addNote;
    private List<Note> notes;
    private NotesAdapter adapter;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        noteRv = findViewById(R.id.note_rv);
        addNote = findViewById(R.id.add_notes);
        taskNot_Exists = findViewById(R.id.not_exists_tasks_tv);
        notes = new ArrayList<>();

        adapter = new NotesAdapter(notes);
        noteRv.setLayoutManager(new LinearLayoutManager(this));
        getData();
        noteRv.setAdapter(adapter);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(intent);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(noteRv);
    }

    private void remove(int position){
        Note note = adapter.getNotes().get(position);
        viewModel.deleteNote(note);
    }

    private void getData(){
        LiveData<List<Note>> note = viewModel.getNotes();
        note.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notesFromDB) {
                notes.clear();
                notes.addAll(notesFromDB);
                adapter.notifyDataSetChanged();
                if (notes == null){
                    taskNot_Exists.setVisibility(View.VISIBLE);
                } else {
                    taskNot_Exists.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}