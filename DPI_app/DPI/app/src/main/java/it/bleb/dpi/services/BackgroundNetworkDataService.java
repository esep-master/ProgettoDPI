package it.bleb.dpi.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.Nullable;

import it.bleb.dpi.database.entity.Alert;
import it.bleb.dpi.database.entity.AzioneOperatore;
import it.bleb.dpi.utils.DateUtil;
import it.bleb.dpi.utils.DpiFeaturesHandler;
import it.bleb.dpi.utils.NetworkState;
import it.bleb.dpi.utils.TipoAzioneOperatoreEnum;

public class BackgroundNetworkDataService extends Service {
    private String TAG = "BackgroundNetworkDataService";
    private DpiFeaturesHandler dpiFeaturesHandler;
    private final IBinder binder = new BackgroundNetworkDataService.LocalBinder();
    private Handler myHandler;
    private Runnable myRunnable;
    private Alert alert;

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public BackgroundNetworkDataService getService() {
            // Return this instance of DpiDetectorService so clients can call public methods
            return BackgroundNetworkDataService.this;
        }
    }

    public BackgroundNetworkDataService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            alert = (Alert) intent.getSerializableExtra("alert");
            if (myHandler == null) {
                myHandler = new Handler();
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (NetworkState.getConnectivityStatus(getBaseContext())) {
                                //send to Portale
                                dpiFeaturesHandler.sendToPortale();
                                Log.d(TAG, "THREAD ALERT: INVIO");
                                stopSelf();
                                myHandler = null;
                            } else {
                                Log.d(TAG, "THREAD ALERT: NON PRENDE");
                                if (alert != null) {
                                    //save to DB
                                    dpiFeaturesHandler.saveAlertInDB(alert);
                                    AzioneOperatore azione = new AzioneOperatore(alert.getIdAppIntervento(), TipoAzioneOperatoreEnum.NUOVO_ALLARME.getValue(), getDataNow());
                                    dpiFeaturesHandler.saveAzioneOperatore(azione);
                                    alert = null;
                                }
                            }
                            if (myHandler != null) {
                                myHandler.postDelayed(this, 10000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                myHandler.post(myRunnable);
            }
        }
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(DpiFeaturesHandler dpiHandler) {
        dpiFeaturesHandler = dpiHandler;
    }

    private String getDataNow() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter  = DateUtil.getDateFormatter();
        return formatter.format(c.getTime());
    }
}