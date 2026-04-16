package com.HabitTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
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
    private RewardManager rewardManager;

    private LinearLayout navHome, navJournal, navAdd, navReminders, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);
        currentUserEmail = prefs.getString("current_user_email", "");
        rewardManager = new RewardManager(this);

        recyclerReminders = findViewById(R.id.recyclerReminders);
        tvReminderCount = findViewById(R.id.tvReminderCount);
        tvReminderSubtitle = findViewById(R.id.tvReminderSubtitle);

        navHome = findViewById(R.id.navHome);
        navJournal = findViewById(R.id.navJournal);
        navAdd = findViewById(R.id.navAdd);
        navReminders = findViewById(R.id.navReminders);
        navProfile = findViewById(R.id.navProfile);

        recyclerReminders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReminderAdapter(
                this,
                new ArrayList<>(),
                dbHelper,
                currentUserEmail,
                this::loadReminders
        );
        recyclerReminders.setAdapter(adapter);

        setupNavigation();
        loadReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserEmail = prefs.getString("current_user_email", "");
        loadReminders();
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });

        navJournal.setOnClickListener(v -> {
            startActivity(new Intent(this, JournalActivity.class));
            finish();
        });

        navAdd.setOnClickListener(v ->
                startActivityForResult(new Intent(this, AddHabitActivity.class), 100));

        navReminders.setOnClickListener(v -> {
            // already on reminders page
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private void loadReminders() {
        if (TextUtils.isEmpty(currentUserEmail)) {
            tvReminderCount.setText("0");
            tvReminderSubtitle.setText("No user signed in.");
            adapter.updateData(new ArrayList<>());
            return;
        }

        List<Habit> habits = dbHelper.getAllHabitsList(currentUserEmail);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        List<Habit> pending = new ArrayList<>();

        if (habits != null) {
            for (Habit h : habits) {
                if (h == null) continue;
                boolean done = dbHelper.isHabitDoneToday(currentUserEmail, h.getId(), today);
                if (!done) {
                    pending.add(h);
                }
            }
        }

        adapter.updateData(pending);
        tvReminderCount.setText(String.valueOf(pending.size()));
        tvReminderSubtitle.setText(
                pending.isEmpty()
                        ? "Everything is completed for today. Nice work!"
                        : "You still have tasks waiting for you today."
        );

        rewardManager.checkAndShowReward(currentUserEmail);
    }
}