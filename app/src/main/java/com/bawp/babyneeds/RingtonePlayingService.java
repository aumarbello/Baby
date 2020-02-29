package com.bawp.babyneeds;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RingtonePlayingService extends Worker {
    private MediaPlayer mediaPlayer;
    private boolean isRunning;

    public RingtonePlayingService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        // git remote set-url [--push] origin https://github.com/aumarbello/Baby [https://github.com/salicode/MyBabyAndI]
    }

    @NonNull
    @Override
    public Result doWork() {
        final AppPreference preference = new AppPreference(getApplicationContext());
        if (preference.isInitialRun()) {
            preference.setInitialRun(false);
            return Result.success();
        }
        Log.d("WorkerClass", "Doing work");

        String state = preference.getAlarmState();
        assert state != null;
        Log.e("Ringtone State :", state);
        int startId;
        if (state.equals("Alarm on")) {
            startId = 1;
        } else if (state.equals("Alarm off")) {
            startId = 0;
        } else {
            startId = 0;
        }

        if (!this.isRunning && startId == 1) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.myalarm);
            mediaPlayer.start();
            this.isRunning = true;
            startId = 0;

        } else if (this.isRunning && startId == 0) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            this.isRunning = false;
            startId = 0;

        } else if (!this.isRunning && startId == 0) {
            this.isRunning = false;
            startId = 0;


        } else if (this.isRunning && startId == 1) {
            this.isRunning = true;
            startId = 1;
        }

        if (!preference.isIntervalReset()) {
            preference.setInitialRun(true);
            preference.setIntervalReset();

            final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(RingtonePlayingService.class, 1, TimeUnit.DAYS)
                    .build();
            final ExistingPeriodicWorkPolicy policy = ExistingPeriodicWorkPolicy.REPLACE;
            WorkManager.getInstance().enqueueUniquePeriodicWork("worker", policy, request);
        }

        return Result.success();
    }
}