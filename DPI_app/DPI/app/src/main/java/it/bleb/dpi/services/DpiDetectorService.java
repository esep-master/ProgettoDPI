package it.bleb.dpi.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import it.bleb.dpi.utils.DpiFeaturesHandler;
import it.bleb.dpi.utils.Prefs;

public class DpiDetectorService extends Service {

    private DpiFeaturesHandler dpiFeaturesHandler;

    private final IBinder binder = new LocalBinder();
    private static final String TAG = "DpiDetectorService";
    public static String CHANNEL_ID = null;

    public DpiDetectorService() {
    }

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public DpiDetectorService getService() {
            // Return this instance of DpiDetectorService so clients can call public methods
            return DpiDetectorService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        //startForeground(1, new Notification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            if (intent.getBooleanExtra("start", false)) {
                Log.d(TAG, "onStartCommand: START");
                if (dpiFeaturesHandler != null) {
                    dpiFeaturesHandler.onScan();
                }
            } else {
                Log.d(TAG, "onStartCommand: STOP");
                if (dpiFeaturesHandler != null) {
                    dpiFeaturesHandler.stopScan();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        /*Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RESTART_ACTION);
        broadcastIntent.setClass(this, DpiScanReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();*/
    }

    private void clearNotification() {
        //NotificationCompat.getChannelId(notification).
    }

    public void setCallbacks(DpiFeaturesHandler dpiHandler) {
        dpiFeaturesHandler = dpiHandler;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(CHANNEL_ID == null) {
                if(Prefs.GetIsCTrace(getApplicationContext()))
                    CHANNEL_ID =  "MyProtector";
                else
                    CHANNEL_ID =  "MakeApp";
            }

            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Background Scan", NotificationManager.IMPORTANCE_MIN);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }
}