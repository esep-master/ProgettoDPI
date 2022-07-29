package it.bleb.dpi.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import it.bleb.blebandroid.Blebricks;
import it.bleb.dpi.R;

public class Common {

    public static boolean IsScanning() {
        return Blebricks.IsScanning();
    }

    public static void EnableAdaptersAndStartScan(final String address, final FragmentActivity activity, final Blebricks.OnScanListener listener, final Blebricks.OnDeviceFoundFromScanListener deviceFoundListener, final Blebricks.OnAdvertiseReceivedFromScanListener advertiseReceivedListener) {
        final String message = activity.getResources().getString(R.string.enable_gps);
        Blebricks.AskEnableGps(activity, message, true, new Blebricks.OnAskEnableGpsListener() {
            @Override
            public void OnAskEnableGpsResponse(boolean enabled) {
                if (!enabled)
                    Blebricks.AskEnableGps(activity, message, true, this);
                else {
                    Blebricks.AskEnableBluetooth(activity, new Blebricks.OnAskEnableBluetoothListener() {
                        @Override
                        public void OnAskEnableBluetoothResponse(boolean enabled) {
                            if (!enabled)
                                Blebricks.AskEnableBluetooth(activity, this);
                            else {
                                if (!Blebricks.IsInit())
                                    Blebricks.Init(activity.getApplicationContext());

                                Blebricks.SetOnAdvertiseReceivedFromScanListener(new Blebricks.OnAdvertiseReceivedFromScanListener() {
                                    @Override
                                    public void OnAdvertiseReceived(String address, String name, int rssi, String manufacturerData, int battery, String rawData) {
                                        LogFile.Append(rawData + " (" + rssi + "dBm)");
                                        DebugMessageManager.AddManufacturerData(manufacturerData + " (" + rssi + "dBm)");

                                        if(advertiseReceivedListener != null)
                                            advertiseReceivedListener.OnAdvertiseReceived(address, name, rssi, manufacturerData, battery, rawData);
                                    }
                                });

                                Blebricks.SetOnDeviceFoundFromScanListener(deviceFoundListener);
                                if(address == null)
                                    Blebricks.StartGlobalScan(listener);
                                else
                                    Blebricks.StartSpecificScan(address, listener);
                            }
                        }
                    });
                }
            }
        });
    }

    public interface OnAskConnectionModeListener {
        void OnAskConnectionModeResponse(boolean confirmed);
    }

    public static void AskConnectionMode(@NonNull final Context context, @NonNull final OnAskConnectionModeListener listener) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        listener.OnAskConnectionModeResponse(true);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        listener.OnAskConnectionModeResponse(false);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle("Mode Selection")
                .setMessage("Please confirm to enter in Connection Mode")
                .setPositiveButton("Connect", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    public static void StopScan() {
        Blebricks.StopScan();
    }


    public static void ToggleButton(final View button, final boolean active) {
        button.setAlpha(active ? 1 : .5f);
        button.setEnabled(active);
        button.setClickable(active);
    }
}

