package it.bleb.dpi.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.services.DpiDetectorService;
import it.bleb.dpi.utils.DpiFeaturesHandler;

/**
 * Classe richiamata nel metodo onDestroy per far ripartire il Service di monitoraggio
 * anche in caso di kill dell'app
 */
public class DpiScanReceiver extends BroadcastReceiver {

    public static final String RESTART_ACTION = "restartService";
    public static final String ERROR_RECEIVED_ACTION = "it.bleb.dpi.errorReceived";
    public static final String SEND_ERROR_ACTION = "it.bleb.dpi.sendAlert";
    public static final String START_ACTIVITY_ACTION = "it.bleb.dpi.startActivity";
    private static final String TAG = "DpiScanReceiver";
    private DpiFeaturesHandler dpiFeaturesHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case RESTART_ACTION:
                Log.d(TAG, "onReceive: RECEIVER ricevuto. Scan continua a lavorare");
                context.startService(new Intent(context, DpiDetectorService.class));
                break;
            case START_ACTIVITY_ACTION:

                //Gestione Kit DPI tramite Settore
                if (intent.getStringExtra("settoreAttivita") != null && dpiFeaturesHandler != null) {
                    Log.d(TAG, "onReceive: RECEIVER ricevuto. settore attività: " + intent.getStringExtra("settoreAttivita"));
                    dpiFeaturesHandler.setKit(DpiAppApplication.DEBUG_MODE, intent.getStringExtra("settoreAttivita"));
                }

                //Gestione Service
                if (intent.getBooleanExtra("isStarted", false)) {
                    Log.d(TAG, "onReceive: RECEIVER ricevuto. inizio attività: " + intent.getBooleanExtra("isStarted", false));
                    context.startService(new Intent(context, DpiDetectorService.class).putExtra("start", true));
                } else {
                    Log.d(TAG, "onReceive: RECEIVER ricevuto. inizio attività: " + intent.getBooleanExtra("isStarted", false));
                    context.startService(new Intent(context, DpiDetectorService.class).putExtra("start", false));

                    //Chiusura app se App di lavoro si chiude
                    if (intent.getBooleanExtra("isAppClosing", false)) {
                        context.stopService(new Intent(context, DpiDetectorService.class));
                        if (dpiFeaturesHandler != null) {
                            dpiFeaturesHandler.logout();
                        }
                    }
                }
                break;
            case ERROR_RECEIVED_ACTION:
                if (dpiFeaturesHandler != null) {
                    dpiFeaturesHandler.stopMessage(intent.getBooleanExtra("isReceived", false));
                }
                break;
            default:
                break;
        }
    }

    public void setCallbacks(DpiFeaturesHandler dpiHandler) {
        dpiFeaturesHandler = dpiHandler;
    }
}