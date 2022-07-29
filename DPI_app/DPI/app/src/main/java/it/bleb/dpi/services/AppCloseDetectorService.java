package it.bleb.dpi.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import it.bleb.blebandroid.utils.Logger;
import it.bleb.dpi.utils.Prefs;

public class AppCloseDetectorService extends Service {
    private static AppCloseDetectorService instance = null;
    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Logger.Log(this, "App closed!");
        if (Prefs.IsBackgroundScanEnabled(getApplicationContext()) && !BackgroundScanService.isInstanceCreated()) {
            Intent serviceIntent = new Intent(getApplicationContext(), BackgroundScanService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
        this.stopSelf();
    }
}