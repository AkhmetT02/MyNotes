package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText noteTitle;
    private EditText noteDescription;
    private Spinner daysOfWeek;
    private RadioGroup priorityRg;
    private Button createNoteBtn;
    private Button setAlarmButton;

    private TimePickerDialog timePickerDialog;
    private Calendar calSet;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);


        noteTitle = findViewById(R.id.new_note_title_et);
        noteDescription = findViewById(R.id.new_note_description_et);
        daysOfWeek = findViewById(R.id.days_spinner);
        priorityRg = findViewById(R.id.priority_rg);
        createNoteBtn = findViewById(R.id.create_note_btn);
        setAlarmButton = findViewById(R.id.set_alarm_btn);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        createNotificationChannel();

        createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noteTitle.getText().toString();
                String description = noteDescription.getText().toString();
                String dayOfWeek = daysOfWeek.getSelectedItem().toString();
                int rbId = priorityRg.getCheckedRadioButtonId();
                RadioButton rb = findViewById(rbId);
                int priority = Integer.parseInt(rb.getText().toString());

                if (isFilled(title, description)){
                    Note note = new Note(title, description, dayOfWeek, priority, calSet.getTime().getHours() + ":" + calSet.getTime().getMinutes());
                    viewModel.insertNote(note);

                    Intent intent = new Intent(CreateNoteActivity.this, MainActivity.class);
                    setAlarm(calSet, title, description);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateNoteActivity.this, "Please, fill the all lines...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
    }
    public void openTimePicker(){
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(CreateNoteActivity.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.DAY_OF_WEEK, daysOfWeek.getSelectedItemPosition() + 2);
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }
            Toast.makeText(CreateNoteActivity.this, calSet.getTime().toString(), Toast.LENGTH_SHORT).show();

        }
    };

    private void setAlarm(Calendar target, String title, String description){
        Intent intent = new Intent(getBaseContext(), ReminderBroadcast.class);
        intent.putExtra("Title", title);
        intent.putExtra("Description", description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), pendingIntent);
    }

    private boolean isFilled(String title, String description){
        return !title.isEmpty() && !description.isEmpty();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notifyAlarm", "NotificationFromMyNotes", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("DESCRIP");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}