package com.HabitTracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private final Context context;
    private List<Habit> reminderList;
    private final DatabaseHelper dbHelper;
    private final String userEmail;
    private final Runnable onChanged;

    public ReminderAdapter(Context context, List<Habit> reminderList, DatabaseHelper dbHelper, String userEmail, Runnable onChanged) {
        this.context = context;
        this.reminderList = reminderList;
        this.dbHelper = dbHelper;
        this.userEmail = userEmail;
        this.onChanged = onChanged;
    }

    public void updateData(List<Habit> newData) {
        this.reminderList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Habit habit = reminderList.get(position);

        holder.tvName.setText(habit.getName());
        holder.tvDesc.setText(habit.getDescription());
        holder.tvCategory.setText(habit.getCategory());
        holder.tvMeta.setText("Tap to mark as done");

        holder.card.setCardBackgroundColor(Color.parseColor("#FFFDF8"));

        holder.itemView.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            dbHelper.updateHabitLog(userEmail, habit.getId(), today, true);
            onChanged.run();
            holder.itemView.animate().alpha(0.6f).setDuration(120).withEndAction(() ->
                    holder.itemView.animate().alpha(1f).setDuration(120).start()
            ).start();
        });
    }

    @Override
    public int getItemCount() {
        return reminderList != null ? reminderList.size() : 0;
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvName, tvDesc, tvCategory, tvMeta;

        ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardReminder);
            tvName = itemView.findViewById(R.id.tvReminderName);
            tvDesc = itemView.findViewById(R.id.tvReminderDesc);
            tvCategory = itemView.findViewById(R.id.tvReminderCategory);
            tvMeta = itemView.findViewById(R.id.tvReminderMeta);
        }
    }
}