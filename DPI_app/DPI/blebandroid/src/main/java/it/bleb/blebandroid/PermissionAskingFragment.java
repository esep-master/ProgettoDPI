/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;

import it.bleb.blebandroid.utils.Logger;

public class PermissionAskingFragment extends Fragment {
    private static final int REQUEST_BLUETOOTH = 1;
    private static final int REQUEST_BLUETOOTH_ADMIN = 2;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 3;

    public static final String ARGS_BLUETOOTH = "ARGS_BLUETOOTH";
    public static final String ARGS_BLUETOOTH_ADMIN = "ARGS_BLUETOOTH_ADMIN";
    public static final String ARGS_ACCESS_FINE_LOCATION = "ARGS_ACCESS_FINE_LOCATION";

    private boolean mBluetooth = false;
    private boolean mBluetoothAdmin = false;
    private boolean mAccessFineLocation = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        mBluetooth = args != null && args.getBoolean(ARGS_BLUETOOTH, false);
        mBluetoothAdmin = args != null && args.getBoolean(ARGS_BLUETOOTH_ADMIN, false);
        mAccessFineLocation = args != null && args.getBoolean(ARGS_ACCESS_FINE_LOCATION, false);

        if (mBluetooth && requestPermission(context, Manifest.permission.BLUETOOTH, REQUEST_BLUETOOTH))
            mBluetooth = false;
        if (mBluetoothAdmin && requestPermission(context, Manifest.permission.BLUETOOTH_ADMIN, REQUEST_BLUETOOTH_ADMIN))
            mBluetoothAdmin = false;
        if (mAccessFineLocation && requestPermission(context, Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION))
            mAccessFineLocation = false;

        checkIfFinished();
    }

    private boolean requestPermission(@NonNull final Context context, @NonNull final String permission, final int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
            return true;

        requestPermissions(new String[]{permission}, requestCode);
        return false;
    }

    private void checkIfFinished() {
        if (!mBluetooth && !mBluetoothAdmin && !mAccessFineLocation) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(PermissionAskingFragment.this);
                fragmentTransaction.commit();
            }

            if(Blebricks.getOnAskPermissionsListener() != null) {
                Blebricks.getOnAskPermissionsListener().OnAskPermissionsDone();
                Blebricks.setOnAskPermissionsListener(null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.Log(PermissionAskingFragment.class, "Event: onRequestPermissionsResult - resultReturned");

        if(getContext() == null)
            return;

        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            int result = grantResults[i];

            if (mBluetooth && perm.equals(Manifest.permission.BLUETOOTH)) {
                if(result == PackageManager.PERMISSION_GRANTED || requestPermission(getContext(), Manifest.permission.BLUETOOTH, REQUEST_BLUETOOTH)) {
                    mBluetooth = false;
                    checkIfFinished();
                }
            }
            if (mBluetoothAdmin && perm.equals(Manifest.permission.BLUETOOTH_ADMIN)) {
                if(result == PackageManager.PERMISSION_GRANTED || requestPermission(getContext(), Manifest.permission.BLUETOOTH_ADMIN, REQUEST_BLUETOOTH_ADMIN)) {
                    mBluetoothAdmin = false;
                    checkIfFinished();
                }
            }
            if (mAccessFineLocation && perm.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if(result == PackageManager.PERMISSION_GRANTED || requestPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION)) {
                    mAccessFineLocation = false;
                    checkIfFinished();
                }
            }
        }
    }
}
