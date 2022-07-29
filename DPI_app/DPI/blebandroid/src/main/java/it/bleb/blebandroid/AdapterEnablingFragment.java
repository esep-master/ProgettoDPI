/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import it.bleb.blebandroid.utils.Logger;

import static android.content.Context.LOCATION_SERVICE;

public class AdapterEnablingFragment extends Fragment {
    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    private static final int REQUEST_GPS_ENABLE = 2;

    public static final String ARGS_BLUETOOTH_ENABLE = "ARGS_BLUETOOTH_ENABLE";
    public static final String ARGS_GPS_ENABLE = "ARGS_GPS_ENABLE";

    private boolean mBluetooth = false;
    private boolean mGps = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        mBluetooth = args != null && args.getBoolean(ARGS_BLUETOOTH_ENABLE, false);
        mGps = args != null && args.getBoolean(ARGS_GPS_ENABLE, false);

        if (mBluetooth) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
        }
        if (mGps) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_GPS_ENABLE);
        }

        checkIfFinished();
    }

    private void checkIfFinished() {
        if (!mBluetooth && !mGps) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(AdapterEnablingFragment.this);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.Log(Blebricks.class, "Event: AdapterEnablingFragment - resultReturned");

        if (mBluetooth && requestCode == REQUEST_BLUETOOTH_ENABLE) {
            mBluetooth = false;

            final FragmentActivity activity = getActivity();
            if (activity == null) {
                Logger.Err(this, "getActivity is null inside this fragment. Ignoring the result..");
                return;
            }

            final boolean enabled = (resultCode == Activity.RESULT_OK);
            if (Blebricks.getOnAskEnableBluetoothListener() != null) {
                Blebricks.getOnAskEnableBluetoothListener().OnAskEnableBluetoothResponse(enabled);
                Blebricks.setOnAskEnableBluetoothListener(null);
            }

            checkIfFinished();
        }
        if (mGps && requestCode == REQUEST_GPS_ENABLE) {
            mGps = false;

            final FragmentActivity activity = getActivity();
            if (activity == null) {
                Logger.Err(this, "getActivity is null inside this fragment. Ignoring the result..");
                return;
            }

            LocationManager mLocationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
            if (Blebricks.getOnAskEnableGpsListener() != null) {
                Blebricks.getOnAskEnableGpsListener().OnAskEnableGpsResponse(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                Blebricks.setOnAskEnableGpsListener(null);
            }

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && Blebricks.hasToRenable())
                Blebricks.ListenToGPS();

            checkIfFinished();
        }
    }
}
