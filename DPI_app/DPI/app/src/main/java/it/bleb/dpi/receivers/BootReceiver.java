package it.bleb.dpi.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import it.bleb.dpi.services.BackgroundScanService;
import it.bleb.dpi.utils.Prefs;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Prefs.IsBackgroundScanEnabled(context)) {
            Intent serviceIntent = new Intent(context, BackgroundScanService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}