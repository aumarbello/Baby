package com.bawp.babyneeds;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MyAlarmActivity extends AppCompatActivity {
    TimePicker mypicker;
    AlarmManager alarmManager;
    TextView myupdatetxt;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_alarm);
        this.context = this;
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mypicker = findViewById(R.id.timer);
        myupdatetxt = findViewById(R.id.update_text);

        final Button alarm_on = findViewById(R.id.start_alarm);
        Button alarm_off = findViewById(R.id.end_alarm);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                final Calendar today = Calendar.getInstance();
                final int currentHour = today.get(Calendar.HOUR_OF_DAY);
                final int currentMinute = today.get(Calendar.MINUTE);

                final int currentTimeInMinutes = (currentHour * 60) + currentMinute;
                final int selectedTimeInMinutes = (mypicker.getHour() * 60) + mypicker.getMinute();
                final boolean isCurrentTimePastSelected = currentTimeInMinutes > selectedTimeInMinutes;


                final long offsetInMinutes = isCurrentTimePastSelected ? (24 * 60) - currentTimeInMinutes + selectedTimeInMinutes : selectedTimeInMinutes - currentTimeInMinutes;
                Log.d("WorkerClass", "Offset in mins " + offsetInMinutes);

                new AppPreference(MyAlarmActivity.this).resetWorkerPreferences();

                final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(RingtonePlayingService.class, offsetInMinutes, TimeUnit.MINUTES)
                        .build();
                final ExistingPeriodicWorkPolicy policy = ExistingPeriodicWorkPolicy.REPLACE;
                WorkManager.getInstance().enqueueUniquePeriodicWork("worker", policy, request);

            }


        });


        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppPreference(MyAlarmActivity.this).resetWorkerPreferences();
                WorkManager.getInstance().cancelAllWork();
            }
        });

    }

}

