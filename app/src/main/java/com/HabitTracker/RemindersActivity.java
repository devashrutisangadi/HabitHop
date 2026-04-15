package com.HabitTracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemindersActivity extends AppCompatActivity {

    private RecyclerView recyclerReminders;
    private TextView tvReminderCount, tvReminderSubtitle;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private String currentUserEmail = "";
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);
        currentUserEmail = prefs.getString("current_user_email", "");

        recyclerReminders = findViewById(R.id.recyclerReminders);
        tvReminderCount = findViewById(R.id.tvReminderCount);
        tvReminderSubtitle = findViewById(R.id.tvReminderSubtitle);

        recyclerReminders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReminderAdapter(this, new ArrayList<>(), dbHelper, currentUserEmail, this::loadReminders);
        recyclerReminders.setAdapter(adapter);

        loadReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    private void loadReminders() {
        List<Habit> habits = dbHelper.getAllHabitsList(currentUserEmail);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        List<Habit> pending = new ArrayList<>();
        for (Habit h : habits) {
            if (!dbHelper.isHabitDoneToday(currentUserEmail, h.getId(), today)) {
                pending.add(h);
            }
        }

        adapter.updateData(pending);
        tvReminderCount.setText(String.valueOf(pending.size()));
        tvReminderSubtitle.setText(
                pending.isEmpty()
                        ? "Everything is completed for today. Nice work!"
                        : "You still have tasks waiting for you today."
        );
    }
}