/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 30/04/2019 11:47
 */

package it.bleb.blebandroid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.bleb.blebandroid.callback.BLEGattCallback;
import it.bleb.blebandroid.callback.BLEScanCallback;
import it.bleb.blebandroid.listener.OnGattListener;
import it.bleb.blebandroid.listener.OnScanAddressResultListener;
import it.bleb.blebandroid.listener.OnScanResultListener;
import it.bleb.blebandroid.utils.BLEDevice;
import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.Constants;
import it.bleb.blebandroid.utils.Logger;
import it.bleb.blebandroid.utils.Property;

import static android.content.Context.LOCATION_SERVICE;
import static android.os.Build.VERSION_CODES.O;

public final class Blebricks {
    private static Context mApplicationContext;

    private static boolean mIsBLECompatible;

    private static final Handler mDelayedActionHandler = new Handler();
    private static final Handler mConnectionTimeoutHandler = new Handler();
    private static final Handler mOneShotCommandTimeoutHandler = new Handler();
    private static final Handler mAdvertiseTimeoutHandler = new Handler();

    private static BluetoothManager mBluetoothManager;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mBluetoothScanner;

    private static BluetoothGatt mBluetoothGatt;
    private static BluetoothLeAdvertiser mBluetoothAdvertiser;

    private static final ArrayList<BLEDevice> mDevicesFound = new ArrayList<>();

    private static final BLEScanCallback mScanCallback = new BLEScanCallback();
    private static final BLEGattCallback mGattCallback = new BLEGattCallback();

    private static State mCurrentState = State.NONE;

    private enum State {
        NONE, ADVERTISING, SCANNING, CONNECT_SCANNING, CONNECT_SERVICES, CONNECT_CONNECTED, ONESHOT_SCANNING, ONESHOT_SERVICES, ONESHOT_CONNECTED
    }

    private static String mEasyAddress;
    private static String mConnectingAddress;
    private static Command mOneShotCommand;
    private static String mAdvertisingAddress;
    private static Command mAdvertisingCommand;

    private static final ArrayList<Property> mNotificationProperties = new ArrayList<>();
    private static final ArrayList<Property> mReadingProperties = new ArrayList<>();
    private static final ArrayList<Command> mWritingCommands = new ArrayList<>();

    private static boolean mIsInit = false;
    private static boolean mHasToAnalyze = false;

    public static boolean getHasToAnalyze() {
        return mHasToAnalyze;
    }

    public static void setHasToAnalyze(boolean hasToAnalyze) {
        mHasToAnalyze = hasToAnalyze;
    }

    //region Event Listeners
    private static OnAskPermissionsListener mOnAskPermissionsListener;
    private static OnAskEnableBluetoothListener mOnAskEnableBluetoothListener;
    private static OnAskEnableGpsListener mOnAskEnableGpsListener;
    private static OnScanListener mOnScanListener;
    private static OnConnectionListener mOnConnectionListener;
    private static OnDisconnectionListener mOnDisconnectionListener;
    private static OnAddressedAdvertisingListener mOnAddressedAdvertisingListener;
    private static OnBroadcastAdvertisingListener mOnBroadcastAdvertisingListener;
    private static OnOneShotListener mOnOneShotListener;
    private static OnStartNotificationFromConnectionListener mOnStartNotificationFromConnectionListener;
    private static OnStopNotificationFromConnectionListener mOnStopNotificationFromConnectionListener;
    private static OnNotificationFromConnectionListener mOnNotificationFromConnectionListener;
    private static OnCommandOnConnectionListener mOnCommandOnConnectionListener;
    private static OnReadFromConnectionListener mOnReadFromConnectionListener;
    private static OnReadRSSIFromConnectionListener mOnReadRSSIFromConnectionListener;
    private static OnAdvertiseReceivedFromScanListener mOnAdvertiseReceivedFromScanListener;
    private static OnDeviceFoundFromScanListener mOnDeviceFoundFromScanListener;

    public static OnScanListener GetOnScanListener() {
        return mOnScanListener;
    }

    public static void SetOnScanListener(@Nullable final OnScanListener listener) {
        mOnScanListener = listener;
    }

    public static OnConnectionListener GetOnConnectionListener() {
        return mOnConnectionListener;
    }

    public static void SetOnConnectionListener(@Nullable final OnConnectionListener listener) {
        mOnConnectionListener = listener;
    }

    public static OnAddressedAdvertisingListener GetOnAddressedAdvertisingListener() {
        return mOnAddressedAdvertisingListener;
    }

    public static void SetOnAddressedAdvertisingListener(@Nullable final OnAddressedAdvertisingListener listener) {
        mOnAddressedAdvertisingListener = listener;
    }

    public static OnBroadcastAdvertisingListener GetOnBroadcastAdvertisingListener() {
        return mOnBroadcastAdvertisingListener;
    }

    public static void SetOnBroadcastAdvertisingListener(@Nullable final OnBroadcastAdvertisingListener listener) {
        mOnBroadcastAdvertisingListener = listener;
    }

    public static OnOneShotListener GetOnOneShotListener() {
        return mOnOneShotListener;
    }

    public static void SetOnOneShotListener(@Nullable final OnOneShotListener listener) {
        mOnOneShotListener = listener;
    }

    public static OnStartNotificationFromConnectionListener GetOnStartNotificationFromConnectionListener() {
        return mOnStartNotificationFromConnectionListener;
    }

    public static void SetOnStartNotificationFromConnectionListener(@Nullable final OnStartNotificationFromConnectionListener listener) {
        mOnStartNotificationFromConnectionListener = listener;
    }

    public static OnStopNotificationFromConnectionListener GetOnStopNotificationFromConnectionListener() {
        return mOnStopNotificationFromConnectionListener;
    }

    public static void SetOnStopNotificationFromConnectionListener(@Nullable final OnStopNotificationFromConnectionListener listener) {
        mOnStopNotificationFromConnectionListener = listener;
    }

    public static OnCommandOnConnectionListener GetOnCommandOnConnectionListener() {
        return mOnCommandOnConnectionListener;
    }

    public static void SetOnCommandOnConnectionListener(@Nullable final OnCommandOnConnectionListener listener) {
        mOnCommandOnConnectionListener = listener;
    }

    public static OnReadFromConnectionListener GetOnReadFromConnectionListener() {
        return mOnReadFromConnectionListener;
    }

    public static void SetOnReadFromConnectionListener(@Nullable final OnReadFromConnectionListener listener) {
        mOnReadFromConnectionListener = listener;
    }

    public static OnReadRSSIFromConnectionListener GetOnReadRSSIFromConnectionListener() {
        return mOnReadRSSIFromConnectionListener;
    }

    public static void SetOnReadRSSIFromConnectionListener(@Nullable final OnReadRSSIFromConnectionListener listener) {
        mOnReadRSSIFromConnectionListener = listener;
    }

    public static OnNotificationFromConnectionListener GetOnNotificationFromConnectionListener() {
        return mOnNotificationFromConnectionListener;
    }

    /**
     * Set a listener for when you receive a notification from a
     * connection (after calling StartNotificationFromConnection)
     *
     * @param listener The listener
     */
    public static void SetOnNotificationFromConnectionListener(@Nullable final OnNotificationFromConnectionListener listener) {
        mOnNotificationFromConnectionListener = listener;
    }

    public static OnDisconnectionListener GetOnDisconnectionListener() {
        return mOnDisconnectionListener;
    }

    /**
     * Set a listener for when the device get disconnected (going
     * out of bt range or shutdown)
     *
     * @param listener The listener
     */
    public static void SetOnDisconnectionListener(@Nullable final OnDisconnectionListener listener) {
        mOnDisconnectionListener = listener;
    }

    public static OnDeviceFoundFromScanListener GetOnDeviceFoundFromScanListener() {
        return mOnDeviceFoundFromScanListener;
    }

    /**
     * Set a listener for when a new device is found from a scan
     * (after calling one of the StartScan methods)
     *
     * @param listener The listener
     */
    public static void SetOnDeviceFoundFromScanListener(@Nullable final OnDeviceFoundFromScanListener listener) {
        mOnDeviceFoundFromScanListener = listener;
    }

    public static OnAdvertiseReceivedFromScanListener GetOnAdvertiseReceivedFromScanListener() {
        return mOnAdvertiseReceivedFromScanListener;
    }

    /**
     * Set a listener for when a new advertise is received from
     * a scan (after calling one of the StartScan methods)
     *
     * @param listener The listener
     */
    public static void SetOnAdvertiseReceivedFromScanListener(@Nullable final OnAdvertiseReceivedFromScanListener listener) {
        mOnAdvertiseReceivedFromScanListener = listener;
    }

    //endregion

    /**
     * Check if is already initialized
     *
     * @return true if initialized, false otherwise
     */
    public static boolean IsInit() {
        return mIsInit;
    }

    /**
     * Initialize the Blebricks library. Has to be called at first, possibly with Bluetooth adapter already turned on and permissions already checked.
     *
     * @param applicationContext The application context
     */
    public static void Init(@NonNull final Context applicationContext) {
        Logger.Log(Blebricks.class, "Called: Init (current state of mIsInit: " + mIsInit + ")");

        if (!mIsInit) {
            mIsInit = true;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mAdvertiseSetCallback = new AdvertisingSetCallback() {
                    @Override
                    public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                        Logger.Log(Blebricks.class, "Event: AdvertiseSetCallback - onAdvertisingSetStarted");

                        final String address = mAdvertisingAddress;
                        final Command command = mAdvertisingCommand;

                        if (address != null && !address.trim().toUpperCase().equals(Constants.ADV_BROADCAST_ADDRESS)) {
                            if (mOnAddressedAdvertisingListener != null)
                                mOnAddressedAdvertisingListener.OnAddressedAdvertisingStarted(address, command);
                        } else {
                            if (mOnBroadcastAdvertisingListener != null)
                                mOnBroadcastAdvertisingListener.OnBroadcastAdvertisingStarted(command);
                        }
                    }

                    @Override
                    public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                        Logger.Log(Blebricks.class, "Event: AdvertiseSetCallback - onAdvertisingSetStopped");

                        final String address = mAdvertisingAddress;
                        final Command command = mAdvertisingCommand;

                        if (address != null && !address.trim().toUpperCase().equals(Constants.ADV_BROADCAST_ADDRESS)) {
                            if (mOnAddressedAdvertisingListener != null)
                                mOnAddressedAdvertisingListener.OnAddressedAdvertisingStopped(address, command);
                        } else {
                            if (mOnBroadcastAdvertisingListener != null)
                                mOnBroadcastAdvertisingListener.OnBroadcastAdvertisingStopped(command);
                        }
                    }
                };
            }

            Logger.Log(Blebricks.class, "Init for the first time");

            mApplicationContext = applicationContext;

            mIsBLECompatible = BLEUtils.IsBLECompatible(applicationContext.getPackageManager());
            if (!mIsBLECompatible) {
                mIsInit = false;
                return;
            }

            mBluetoothManager = (BluetoothManager) applicationContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Logger.Err(BLEB.class, "No bluetooth manager available found on the device.");

                mIsInit = false;
                mIsBLECompatible = false;
                return;
            }

            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Logger.Err(BLEB.class, "No bluetooth adapter available found on the device.");

                mIsInit = false;
                mIsBLECompatible = false;
                return;
            }

            if (!mBluetoothAdapter.isEnabled())
                Logger.Warn(BLEB.class, "You should first turn on bluetooth adapter before initialization.");


            mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (mBluetoothAdvertiser == null) {
                Logger.Err(BLEB.class, "Device not compatible with bluetooth advertisement.");

                mIsInit = false;
                mIsBLECompatible = false;
                return;
            }

            mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (mBluetoothScanner == null) {
                Logger.Err(BLEB.class, "Device not compatible with bluetooth low energy scanning.");

                mIsInit = false;
                mIsBLECompatible = false;
                return;
            }
        }
    }

    /**
     * To be called at every OnResume event of the activities where the Blebricks library is used.
     *
     * @param activity The activity in OnResume event
     */
    public static void OnResume(@NonNull final FragmentActivity activity) {
        Logger.Log(Blebricks.class, "Event: OnResume");

        if (mHasToRenable && mIsInit) {
            if (IsGpsEnabled())
                ListenToGPS();
            else {
                AskEnableGps(activity, mHasToRenableMessage, true, new OnAskEnableGpsListener() {
                    @Override
                    public void OnAskEnableGpsResponse(boolean enabled) {
                        if (enabled)
                            ListenToGPS();
                        else
                            AskEnableGps(activity, mHasToRenableMessage, true, this);
                    }
                });
            }
        }
    }

    /**
     * To be called at every OnPause event of the activities where the Blebricks library is used.
     *
     * @param activity The activity in OnResume event
     */
    public static void OnPause(@NonNull final FragmentActivity activity) {
        Logger.Log(Blebricks.class, "Event: OnPause");
        if (mIsInit)
            UnlistenToGPS();
    }

    //region Permissions
    static OnAskPermissionsListener getOnAskPermissionsListener() {
        return mOnAskPermissionsListener;
    }

    static void setOnAskPermissionsListener(OnAskPermissionsListener onAskPermissionsListener) {
        mOnAskPermissionsListener = onAskPermissionsListener;
    }

    /**
     * Ask for the permissions (BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION)
     *
     * @param activity The activity
     * @param listener The listener to check the status
     */
    public static void AskPermissions(@NonNull final FragmentActivity activity, @Nullable final OnAskPermissionsListener listener) {
        Logger.Log(Blebricks.class, "Asking permissions");
        mOnAskPermissionsListener = listener;

        Bundle b = new Bundle();
        b.putBoolean(PermissionAskingFragment.ARGS_BLUETOOTH, true);
        b.putBoolean(PermissionAskingFragment.ARGS_BLUETOOTH_ADMIN, true);
        b.putBoolean(PermissionAskingFragment.ARGS_ACCESS_FINE_LOCATION, true);

        Fragment f = new PermissionAskingFragment();
        f.setArguments(b);

        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(f, "AskPermissions");
        fragmentTransaction.commit();
    }
    //endregion

    //region GPS
    private static LocationManager mLocationManager;
    private static boolean mLocationIsListening = false;
    private static boolean mHasToRenable = false;
    private static String mHasToRenableMessage = null;

    private static final class GpsDisabledListener implements LocationListener {
        private WeakReference<FragmentActivity> mActivity;
        private String mMessage;
        private OnAskEnableGpsListener mListener;

        public void setActivity(FragmentActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        public void setMessage(String message) {
            mMessage = message != null ? message : "Please, turn on your GPS.";
        }

        public void setListener(OnAskEnableGpsListener listener) {
            mListener = listener;
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            if (mHasToRenable && mLocationIsListening && provider.equals(LocationManager.GPS_PROVIDER) && mActivity.get() != null)
                AskEnableGps(mActivity.get(), mMessage, mHasToRenable, mListener);
        }
    }

    private static final GpsDisabledListener mGpsDisabledLister = new GpsDisabledListener();

    static void ListenToGPS() {
        if (ActivityCompat.checkSelfPermission(mApplicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        if (mLocationManager == null)
            mLocationManager = (LocationManager) mApplicationContext.getSystemService(LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return;

        if (mLocationIsListening)
            UnlistenToGPS();

        mLocationIsListening = true;
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGpsDisabledLister);
    }

    private static void UnlistenToGPS() {
        if (mLocationManager == null)
            mLocationManager = (LocationManager) mApplicationContext.getSystemService(LOCATION_SERVICE);

        if (mLocationIsListening) {
            mLocationIsListening = false;
            mLocationManager.removeUpdates(mGpsDisabledLister);
        }
    }
    //endregion

    //region Adapter
    static OnAskEnableBluetoothListener getOnAskEnableBluetoothListener() {
        return mOnAskEnableBluetoothListener;
    }

    static OnAskEnableGpsListener getOnAskEnableGpsListener() {
        return mOnAskEnableGpsListener;
    }

    static void setOnAskEnableBluetoothListener(OnAskEnableBluetoothListener onAskEnableBluetoothListener) {
        mOnAskEnableBluetoothListener = onAskEnableBluetoothListener;
    }

    static void setOnAskEnableGpsListener(OnAskEnableGpsListener onAskEnableGpsListener) {
        mOnAskEnableGpsListener = onAskEnableGpsListener;
    }

    public static boolean hasToRenable() {
        return mHasToRenable;
    }

    /**
     * Enable the bluetooth adapter on the device.
     */
    public static void EnableBluetooth() {
        Logger.Log(Blebricks.class, "Called: EnableBluetooth");
        if (mBluetoothAdapter == null) {
            Logger.Err(BLEB.class, "First of all, you have to call the Init method to initialize the library");
            return;
        }

        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
    }

    /**
     * Disable the bluetooth adapter on the device.
     */
    public static void DisableBluetooth() {
        Logger.Log(Blebricks.class, "Called: DisableBluetooth");
        if (mBluetoothAdapter == null) {
            Logger.Err(BLEB.class, "First of all, you have to call the Init method to initialize the library");
            return;
        }

        clearState();

        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
    }

    /**
     * Ask to the user if he wants to enable or not the bluetooth adapter on the device.
     *
     * @param activity The current activity
     * @param listener The (nullable) listener to get the response from the user
     */
    public static void AskEnableBluetooth(@NonNull final FragmentActivity activity, @Nullable OnAskEnableBluetoothListener listener) {
        Logger.Log(Blebricks.class, "Called: AskEnableBluetooth");

        if (IsBluetoothEnabled()) {
            if (listener != null)
                listener.OnAskEnableBluetoothResponse(true);
            return;
        }

        mOnAskEnableBluetoothListener = listener;

        Bundle b = new Bundle();
        b.putBoolean(AdapterEnablingFragment.ARGS_BLUETOOTH_ENABLE, true);

        Fragment f = new AdapterEnablingFragment();
        f.setArguments(b);

        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(f, "EnableBluetooth");
        fragmentTransaction.commit();
    }

    /**
     * Ask to the user if he wants to enable or not the GPS adapter on the device.
     *
     * @param activity                The current activity
     * @param message                 The message used when asking for the enabling of the GPS (if null will be used a default message)
     * @param autoEnableWhenTurnedOff Enable again GPS if the user turn it off
     * @param listener                The (nullable) listener to get the response from the user
     */
    public static void AskEnableGps(@NonNull final FragmentActivity activity, @Nullable final String message, final boolean autoEnableWhenTurnedOff, @Nullable final OnAskEnableGpsListener listener) {
        Logger.Log(Blebricks.class, "Called: AskEnableGps");

        mHasToRenable = autoEnableWhenTurnedOff;
        mHasToRenableMessage = message;

        mGpsDisabledLister.setActivity(activity);
        mGpsDisabledLister.setMessage(mHasToRenableMessage);
        mGpsDisabledLister.setListener(listener);

        if (IsGpsEnabled()) {
            if (autoEnableWhenTurnedOff) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListenToGPS();
                    }
                });
            }

            if (listener != null)
                listener.OnAskEnableGpsResponse(true);

            return;
        }

        mOnAskEnableGpsListener = listener;

        Toast.makeText(activity, mHasToRenableMessage, Toast.LENGTH_LONG).show();

        Bundle b = new Bundle();
        b.putBoolean(AdapterEnablingFragment.ARGS_GPS_ENABLE, true);

        Fragment f = new AdapterEnablingFragment();
        f.setArguments(b);

        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(f, "EnableGps");
        fragmentTransaction.commit();
    }

    /**
     * Disable the auto turning on of the GPS (enabled with AskEnableGps)
     */
    public static void DisableAutoEnableGpsWhenTurnedOff() {
        mHasToRenable = false;
        mHasToRenableMessage = null;
        UnlistenToGPS();
    }

    /**
     * Check if bluetooth is enabled or not
     *
     * @return true if enabled, false otherwise
     */
    public static boolean IsBluetoothEnabled() {
        Logger.Log(Blebricks.class, "Called: IsBluetoothEnabled (is: " + (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) + ")");
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * Check if gps is enabled or not
     *
     * @return true if enabled, false otherwise
     */
    public static boolean IsGpsEnabled() {
        if (mLocationManager == null)
            mLocationManager = (LocationManager) mApplicationContext.getSystemService(LOCATION_SERVICE);

        Logger.Log(Blebricks.class, "Called: IsGpsEnabled (is: " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) + ")");
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    //endregion

    //region ScanEvents

    /**
     * Start scanning for advertise. Listen only the nearest BLE-B found.
     * Use SetOnDeviceFoundFromScanListener and SetOnAdvertiseReceivedFromScanListener
     * to check when a new device is found or a new advertise is received
     *
     * @param listener The listener to check the results
     */
    public static void StartScan(@Nullable final OnScanListener listener) {
        Logger.Log(Blebricks.class, "Called: StartScan");

        mOnScanListener = listener;

        if (StartScanRaw(mScanResultListener)) {
            mCurrentState = State.SCANNING;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    Logger.Warn(Blebricks.class, "Timeout without advertise, restarting scan..");
                    StartScan(listener);
                }
            }, Constants.NO_ADVERTISE_SCAN_TIMEOUT_MS);
        } else {
            mScanResultListener.clear();

            if (mOnScanListener != null) {
                OnScanListener l = mOnScanListener;
                mOnScanListener = null;
                l.OnScanFailed(-1);
            }
        }
    }

    /**
     * Start scanning for advertise. Listen all the BLE-B found.
     * Use SetOnDeviceFoundFromScanListener and SetOnAdvertiseReceivedFromScanListener
     * to check when a new device is found or a new advertise is received
     *
     * @param listener The listener to check the results
     */
    public static void StartGlobalScan(@Nullable final OnScanListener listener) {
        StartGlobalScan(listener, false);
    }

    /**
     * Start scanning for advertise. Listen all the BLE-B found.
     * Use SetOnDeviceFoundFromScanListener and SetOnAdvertiseReceivedFromScanListener
     * to check when a new device is found or a new advertise is received
     *
     * @param listener  The listener to check the results
     * @param lowEnergy Low energy mode
     */
    public static void StartGlobalScan(@Nullable final OnScanListener listener, final boolean lowEnergy) {
        Logger.Log(Blebricks.class, "Called: StartGlobalScan");

        mOnScanListener = listener;

        if (StartScanRaw(mScanGlobalResultListener, lowEnergy)) {
            mCurrentState = State.SCANNING;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    Logger.Warn(Blebricks.class, "Timeout without advertise, restarting global scan..");
                    StartGlobalScan(listener);
                }
            }, Constants.NO_ADVERTISE_SCAN_TIMEOUT_MS);
        } else {
            mScanGlobalResultListener.clear();

            if (mOnScanListener != null) {
                OnScanListener l = mOnScanListener;
                mOnScanListener = null;
                l.OnScanFailed(-1);
            }
        }
    }

    /**
     * Start scanning for advertise from a specific MAC address.
     * Use SetOnDeviceFoundFromScanListener and SetOnAdvertiseReceivedFromScanListener
     * to check when a new device is found or a new advertise is received
     *
     * @param address  The address to scan
     * @param listener The listener to check the results
     */
    public static void StartSpecificScan(@NonNull final String address, @Nullable final OnScanListener listener) {
        Logger.Log(Blebricks.class, "Called: StartSpecificScan with address " + address);

        mOnScanListener = listener;

        mScanSpecificResultListener.setAddress(address);
        if (StartScanRaw(mScanSpecificResultListener)) {
            mCurrentState = State.SCANNING;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    Logger.Warn(Blebricks.class, "Timeout without advertise, restarting specific scan..");
                    StartSpecificScan(address, listener);
                }
            }, Constants.NO_ADVERTISE_SCAN_TIMEOUT_MS);
        } else {
            mScanSpecificResultListener.clear();

            if (mOnScanListener != null) {
                OnScanListener l = mOnScanListener;
                mOnScanListener = null;
                l.OnScanFailed(-1);
            }
        }
    }

    /**
     * Stop the scan (started with StartScan(), StartGlobalScan() or StartSpecificScan())
     */
    public static void StopScan() {
        Logger.Log(Blebricks.class, "Called: StopScan");

        if (IsScanning()) {
            clearState(false);

            if (mOnScanListener != null) {
                OnScanListener l = mOnScanListener;
                mOnScanListener = null;
                l.OnScanStopped();
            }
        }
    }

    /**
     * Check if is scanning (after StartScan(), StartGlobalScan() or StartSpecificScan())
     *
     * @return True if is scanning, false otherwise
     */
    public static boolean IsScanning() {
        Logger.Log(Blebricks.class, "Called: IsScanning");
        if (!mIsBLECompatible || !IsBluetoothEnabled())
            return false;

        return mCurrentState == State.SCANNING;
    }

    private static boolean StartScanRaw(final OnScanResultListener listener) {
        return StartScanRaw(listener, false);
    }

    private static boolean StartScanRaw(final OnScanResultListener listener, final boolean lowEnergy) {
        Logger.Log(Blebricks.class, "Called: StartScanRaw");

        if (!mIsBLECompatible || !IsBluetoothEnabled())
            return false;

        clearState();
        mDevicesFound.clear();

        mDelayedActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScanSettings scanSettings;
                if (android.os.Build.VERSION.SDK_INT >= O) {
                    scanSettings = new ScanSettings.Builder()
                            .setScanMode(lowEnergy ? ScanSettings.SCAN_MODE_LOW_POWER : ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                            .setLegacy(false)
                            .build();
                } else {
                    scanSettings = new ScanSettings.Builder()
                            .setScanMode(lowEnergy ? ScanSettings.SCAN_MODE_LOW_POWER : ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build();
                }

                List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
                ScanFilter filter = new ScanFilter.Builder().build();
                scanFilters.add(filter);

                mScanCallback.setOnScanResultListener(listener);
                mBluetoothScanner.startScan(scanFilters, scanSettings, mScanCallback);

                if (mOnScanListener != null)
                    mOnScanListener.OnScanStarted();

            }
        }, 1000);

        return true;
    }

    private static void AnalyzeScanResult(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
        boolean alreadyFound = false;
        for (int i = 0; i < mDevicesFound.size() && !alreadyFound; i++)
            if (mDevicesFound.get(i).getAddress().trim().toUpperCase().equals(address))
                alreadyFound = true;

        if (!alreadyFound) {
            mDevicesFound.add(new BLEDevice(name, address));

            if (mOnDeviceFoundFromScanListener != null)
                mOnDeviceFoundFromScanListener.OnDeviceFound(address, name, rssi, manufacturerData, battery, rawData);
        }

        if (mOnAdvertiseReceivedFromScanListener != null)
            mOnAdvertiseReceivedFromScanListener.OnAdvertiseReceived(address, name, rssi, manufacturerData, battery, rawData);

        final byte[] manufacturerBytes = BLEUtils.HexToBytes(manufacturerData);

        final byte pcb = manufacturerBytes[0];
        final int pcbInt = BLEUtils.UnsignedBytesToInt(pcb);

        if (pcbInt < 255) {
            final int txPower = (BLEUtils.GetBit(pcb, 7) ? 4 : 0) + (BLEUtils.GetBit(pcb, 6) ? 2 : 0) + (BLEUtils.GetBit(pcb, 5) ? 1 : 0);
            final int buttonState = (BLEUtils.GetBit(pcb, 4) ? 2 : 0) + (BLEUtils.GetBit(pcb, 3) ? 1 : 0);
            final boolean scanIsActive = BLEUtils.GetBit(pcb, 2);
            final boolean hasDirectInteractions = BLEUtils.GetBit(pcb, 1);

            final int txPowerDBM;
            switch (txPower) {
                case 0:
                    txPowerDBM = 0;
                    break;
                case 1:
                    txPowerDBM = -40;
                    break;
                case 2:
                    txPowerDBM = -20;
                    break;
                case 3:
                    txPowerDBM = -12;
                    break;
                case 4:
                    txPowerDBM = -8;
                    break;
                case 5:
                    txPowerDBM = -4;
                    break;
                case 6:
                    txPowerDBM = +3;
                    break;
                case 7:
                    txPowerDBM = +4;
                    break;
                default:
                    txPowerDBM = Integer.MIN_VALUE;
                    break;
            }
            if (Components.BLEB.ScanEvents.getOnNameListener() != null)
                Components.BLEB.ScanEvents.getOnNameListener().OnNameReceived(address, name);
            if (Components.BLEB.ScanEvents.getOnBatteryListener() != null)
                Components.BLEB.ScanEvents.getOnBatteryListener().OnBatteryReceived(address, battery);
            if (Components.BLEB.ScanEvents.getOnRSSIListener() != null)
                Components.BLEB.ScanEvents.getOnRSSIListener().OnRSSIReceived(address, rssi);
            if (Components.BLEB.ScanEvents.getOnScanStatusListener() != null)
                Components.BLEB.ScanEvents.getOnScanStatusListener().OnScanStatusReceived(address, scanIsActive);
            if (Components.BLEB.ScanEvents.getOnHasDirectInteractionsListener() != null)
                Components.BLEB.ScanEvents.getOnHasDirectInteractionsListener().OnHasDirectInteractionsReceived(address, hasDirectInteractions);
            if (Components.BLEB.ScanEvents.getOnButtonStateListener() != null) {
                BLEB.ButtonState state;
                switch (buttonState) {
                    default:
                        state = BLEB.ButtonState.UNTOUCHED;
                        break;
                    case 1:
                        state = BLEB.ButtonState.RELEASED;
                        break;
                    case 2:
                        state = BLEB.ButtonState.PRESSED;
                        break;
                    case 3:
                        state = BLEB.ButtonState.LONGPRESS;
                        break;
                }
                Components.BLEB.ScanEvents.getOnButtonStateListener().OnButtonStateReceived(address, state);
            }
            if (Components.BLEB.ScanEvents.getOnTXPowerListener() != null) {
                BLEB.TxPower power;
                switch (txPowerDBM) {
                    case -40:
                        power = BLEB.TxPower.MINUS_40_DBM;
                        break;
                    case -20:
                        power = BLEB.TxPower.MINUS_20_DBM;
                        break;
                    case -16:
                        power = BLEB.TxPower.MINUS_16_DBM;
                        break;
                    case -12:
                        power = BLEB.TxPower.MINUS_12_DBM;
                        break;
                    case -8:
                        power = BLEB.TxPower.MINUS_8_DBM;
                        break;
                    case -4:
                        power = BLEB.TxPower.MINUS_4_DBM;
                        break;
                    case 3:
                        power = BLEB.TxPower.PLUS_3_DBM;
                        break;
                    case 4:
                        power = BLEB.TxPower.PLUS_4_DBM;
                        break;
                    case 8:
                        power = BLEB.TxPower.PLUS_8_DBM;
                        break;
                    default:
                        power = BLEB.TxPower.ZERO_DBM;
                        break;
                }
                Components.BLEB.ScanEvents.getOnTXPowerListener().OnTXPowerReceived(address, power);
            }

            Components.BLEB.setBLE5(phyCoded);

            if (getHasToAnalyze())
                AnalyzeManufacturerData(address, battery, rssi, manufacturerBytes, 1);
        } else {
            final byte[] targetBytes = Arrays.copyOfRange(manufacturerBytes, 1, 7);
            final byte[] messageBytes = Arrays.copyOfRange(manufacturerBytes, 7, manufacturerBytes.length);

            final String target = BLEUtils.BytesToMacAddress(targetBytes);

            if (getHasToAnalyze())
                AnalyzeDirectInteractionMessage(address, target, messageBytes);
        }
    }

    private static final OnScanResultListener mScanResultListener = new OnScanResultListener() {
        private Handler mFirstAddressFoundHandler = new Handler();
        private boolean mIsHandlerActive = false;
        private String mTempMaxRSSIAddress = null;
        private int mTempMaxRSSIValue = -1;

        @Override
        public void onScanFailed(final int errorCode) {
            super.onScanFailed(errorCode);
            clearState();
            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnScanListener != null) {
                        OnScanListener l = mOnScanListener;
                        mOnScanListener = null;
                        l.OnScanFailed(errorCode);
                    }
                }
            }, 500);
        }

        @Override
        public void clear() {
            super.clear();
            mFirstAddressFoundHandler.removeCallbacksAndMessages(null);
            mIsHandlerActive = false;
            mTempMaxRSSIAddress = null;
            mTempMaxRSSIValue = -1;
        }

        @Override
        public void onAnyResultReceived() {
            mDelayedActionHandler.removeCallbacksAndMessages(null); // remove "Timeout after no advertise scan restart"
        }

        @Override
        public void onResult(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            if (mEasyAddress == null || mEasyAddress.length() == 0) {
                if (mIsHandlerActive) {
                    if (mTempMaxRSSIValue < rssi) {
                        mTempMaxRSSIAddress = address.trim().toUpperCase();
                        mTempMaxRSSIValue = rssi;
                    }
                } else { // first run
                    mTempMaxRSSIAddress = address.trim().toUpperCase();
                    mTempMaxRSSIValue = rssi;

                    mIsHandlerActive = true;
                    mFirstAddressFoundHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEasyAddress = mTempMaxRSSIAddress;
                            clear();
                        }
                    }, 1000);
                }
            } else
                AnalyzeScanResult(scanResult, name, address, rssi, manufacturerData, battery, rawData, phyCoded);
        }

        @Override
        public boolean filter(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            return mEasyAddress == null || mEasyAddress.length() == 0 || address.trim().toUpperCase().equals(mEasyAddress);
        }
    };

    private static final OnScanAddressResultListener mScanSpecificResultListener = new OnScanAddressResultListener() {
        @Override
        public void onScanFailed(final int errorCode) {
            super.onScanFailed(errorCode);
            clearState();
            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnScanListener != null) {
                        OnScanListener l = mOnScanListener;
                        mOnScanListener = null;
                        l.OnScanFailed(errorCode);
                    }
                }
            }, 500);
        }

        @Override
        public void onAnyResultReceived() {
            mDelayedActionHandler.removeCallbacksAndMessages(null); // remove "Timeout after no advertise scan restart"
        }

        @Override
        public void onResult(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            AnalyzeScanResult(scanResult, name, address, rssi, manufacturerData, battery, rawData, phyCoded);
        }

        @Override
        public boolean filter(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            return address.trim().toUpperCase().equals(getAddress());
        }
    };

    private static final OnScanResultListener mScanGlobalResultListener = new OnScanResultListener() {
        @Override
        public void onScanFailed(final int errorCode) {
            super.onScanFailed(errorCode);
            clearState();
            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnScanListener != null) {
                        OnScanListener l = mOnScanListener;
                        mOnScanListener = null;
                        l.OnScanFailed(errorCode);
                    }
                }
            }, 500);
        }

        @Override
        public void onAnyResultReceived() {
            mDelayedActionHandler.removeCallbacksAndMessages(null); // remove "Timeout after no advertise scan restart"
        }

        @Override
        public void onResult(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            AnalyzeScanResult(scanResult, name, address, rssi, manufacturerData, battery, rawData, phyCoded);
        }

        @Override
        public boolean filter(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            return true;
        }
    };
    //endregion

    //region Analysis

    /**
     * Get the specific bytes from the manufacturer data of the specified data type (or an empty string if not found).
     *
     * @param manufacturerData The manufacturer data hex-formatted string
     * @param dataType         The data type you're searching for
     * @return The hex-formatted string with the data associated to that data type
     */
    public static String GetDataFromManufacturerData(@NonNull final String manufacturerData, @NonNull final String dataType) {
        Logger.Log(Blebricks.class, "Called: GetDataFromManufacturerData with " + manufacturerData + " searching for " + dataType);
        byte[] m = BLEUtils.HexToBytes(manufacturerData);
        byte d = BLEUtils.HexToBytes(dataType)[0];

        int i = 1;
        while (i < m.length) {
            int length = BLEUtils.GetLengthByDataType(m[i]);
            if (m[i] == d) {
                byte[] res = new byte[length];
                for (int j = 0; j < length; j++)
                    res[j] = m[i + 1 + j];
                return BLEUtils.BytesToHex(res);
            }
            i += 1 + length;
        }

        return "";
    }

    /**
     * Check if the manufacturer data contains a specific data type.
     *
     * @param manufacturerData The manufacturer data hex-formatted string
     * @param dataType         The data type you're searching for
     * @return True if is contained, false otherwise
     */
    public static boolean ManufacturerDataContainsDataType(@NonNull final String manufacturerData, @NonNull final String dataType) {
        Logger.Log(Blebricks.class, "Called: ManufacturerDataContainsDataType with " + manufacturerData + " searching for " + dataType);
        return GetDataFromManufacturerData(manufacturerData, dataType).trim().length() > 0;
    }

    /**
     * Analyze a manufacturer data and call the appropriate listeners after the analysis of the datatypes contained in it.
     *
     * @param address           The address of the device
     * @param manufacturerBytes Manufacturer data bytes
     */
    public static void AnalyzeManufacturerData(final String address, final int battery, final int rssi, final byte[] manufacturerBytes) {
        AnalyzeManufacturerData(address, battery, rssi, manufacturerBytes, 0);
    }

    /**
     * Analyze a manufacturer data and call the appropriate listeners after the analysis of the datatypes contained in it.
     *
     * @param address           The address of the device
     * @param manufacturerBytes Manufacturer data bytes
     * @param startIndex        Start analyzing from a specific index
     */
    public static void AnalyzeManufacturerData(final String address, final int battery, final int rssi, final byte[] manufacturerBytes, final int startIndex) {
        Logger.Log(Blebricks.class, "Called: AnalyzeManufacturerData with " + BLEUtils.BytesToHex(manufacturerBytes) + " (Start index: " + startIndex + ")");

        int i = startIndex;
        while (i < manufacturerBytes.length) {
            final byte dataType = manufacturerBytes[i];

            final int length = BLEUtils.GetLengthByDataType(dataType);
            if (i + length <= manufacturerBytes.length) {
                final byte[] data = new byte[length];

                for (int j = 0; j < length; j++)
                    data[j] = manufacturerBytes[i + 1 + j];

                for (Component c : Components.Array)
                    c.AnalyzeManufacturerData(address, BLEUtils.UnsignedBytesToInt(dataType), data, manufacturerBytes, battery, rssi);
            }

            i += 1 + length;
        }
    }

    /**
     * Analyze the bytes of a direct interaction message and call the appropriate listeners
     *
     * @param address The address of the device
     * @param target  The address of the target device
     * @param command The bytes of the command
     */
    public static void AnalyzeDirectInteractionMessage(final String address, final String target, final byte[] command) {
        for (Component c : Components.Array)
            c.AnalyzeDirectInteractionMessage(address, target, command);
    }
    //endregion

    //region ConnectionEvents

    /**
     * Connect to a specific device.
     * Use SetOnDisconnectionListener to check
     * when a device gets disconnected
     *
     * @param address  The MAC address of the device
     * @param listener The listener to check the status of the connection
     */
    public static void ConnectTo(@NonNull final String address, @Nullable final OnConnectionListener listener) {
        Logger.Log(Blebricks.class, "Called: ConnectTo with address " + address);
        mOnConnectionListener = listener;

        if (!mIsBLECompatible || !IsBluetoothEnabled() || address == null || address.trim().length() == 0) {
            if (mOnConnectionListener != null) {
                OnConnectionListener l = mOnConnectionListener;
                mOnConnectionListener = null;
                l.OnConnectionFailed(address != null ? address.trim().toUpperCase() : "00:00:00:00:00:00");
            }
            return;
        }

        mConnectingAddress = address.trim().toUpperCase();
        mScanConnectResultListener.setAddress(address.trim().toUpperCase());
        if (StartScanRaw(mScanConnectResultListener)) {
            mConnectionTimeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    mConnectingAddress = null;
                    if (mOnConnectionListener != null) {
                        OnConnectionListener l = mOnConnectionListener;
                        mOnConnectionListener = null;
                        l.OnConnectionFailed(address != null ? address.trim().toUpperCase() : "00:00:00:00:00:00");
                    }
                }
            }, 15000);
            mCurrentState = State.CONNECT_SCANNING;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    Logger.Warn(Blebricks.class, "Timeout without advertise, restarting connect to..");
                    mScanConnectResultListener.clear();
                    mConnectingAddress = null;
                    ConnectTo(address, listener);
                }
            }, Constants.NO_ADVERTISE_SCAN_TIMEOUT_MS);
        } else {
            mScanConnectResultListener.clear();
            mConnectingAddress = null;
            if (mOnConnectionListener != null) {
                OnConnectionListener l = mOnConnectionListener;
                mOnConnectionListener = null;
                l.OnConnectionFailed(address != null ? address.trim().toUpperCase() : "00:00:00:00:00:00");
            }
        }
    }

    /**
     * Stop trying to connect to a specific device (after calling ConnectTo())
     */
    public static void StopConnectTo() {
        Logger.Log(Blebricks.class, "Called: StopConnectTo");

        if (IsConnecting()) {
            clearState(false);

            if (mOnConnectionListener != null) {
                OnConnectionListener l = mOnConnectionListener;
                mOnConnectionListener = null;
                l.OnConnectionStopped(mConnectingAddress != null ? mConnectingAddress.trim().toUpperCase() : "00:00:00:00:00:00");
            }
        }
    }

    /**
     * Disconnect from a connected device (when connected via ConnectTo())
     */
    public static void Disconnect() {
        Logger.Log(Blebricks.class, "Called: Disconnect");
        if (IsConnected())
            clearState();
    }

    /**
     * Check if is actually connected to a device
     *
     * @return True if is connected, false otherwise
     */
    public static boolean IsConnected() {
        Logger.Log(Blebricks.class, "Called: IsConnected");
        if (!mIsBLECompatible || !IsBluetoothEnabled())
            return false;

        return mCurrentState == State.CONNECT_CONNECTED || mCurrentState == State.ONESHOT_CONNECTED;
    }

    /**
     * Check if is actually trying to connect to a device
     *
     * @return True if is currently trying to connect to a device, false otherwise
     */
    public static boolean IsConnecting() {
        Logger.Log(Blebricks.class, "Called: IsConnecting");
        if (!mIsBLECompatible || !IsBluetoothEnabled())
            return false;

        return mCurrentState == State.CONNECT_SCANNING || mCurrentState == State.CONNECT_SERVICES || mCurrentState == State.ONESHOT_SCANNING || mCurrentState == State.ONESHOT_SERVICES;
    }

    private static final OnScanAddressResultListener mScanConnectResultListener = new OnScanAddressResultListener() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            clearState();
            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnConnectionListener != null) {
                        OnConnectionListener l = mOnConnectionListener;
                        mOnConnectionListener = null;
                        l.OnConnectionFailed(mConnectingAddress != null ? mConnectingAddress : "00:00:00:00:00:00");
                    }
                }
            }, 500);
        }

        @Override
        public void onAnyResultReceived() {
            mDelayedActionHandler.removeCallbacksAndMessages(null); // remove "Timeout after no advertise scan restart"
        }

        @Override
        public void onResult(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            clearState();

            mCurrentState = State.CONNECT_SERVICES;
            mGattCallback.setOnGattListener(mConnectGattListener);

            if (Build.VERSION.SDK_INT >= O && phyCoded)
                mBluetoothGatt = scanResult.getDevice().connectGatt(mApplicationContext, false, mGattCallback, BluetoothDevice.TRANSPORT_AUTO, BluetoothDevice.PHY_LE_CODED_MASK);
            else
                mBluetoothGatt = scanResult.getDevice().connectGatt(mApplicationContext, false, mGattCallback);
        }

        @Override
        public boolean filter(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            return (address.trim().toUpperCase()).equals(getAddress());
        }
    };

    private static class OnConnectGattListener implements OnGattListener {
        @Override
        public void onConnected() {
            Logger.Log(Blebricks.class, "Event: OnConnectGattListener - onConnected");
            mCurrentState = State.CONNECT_CONNECTED;

            final String address = mConnectingAddress;
            final String name = mBluetoothGatt.getDevice().getName();

            if (mOnConnectionListener != null) {
                mDelayedActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OnConnectionListener l = mOnConnectionListener;
                        mOnConnectionListener = null;
                        l.OnConnectionDone(address, name);
                    }
                }, 100);
            }
        }

        @Override
        public void onConnectionFailed() {
            Logger.Log(Blebricks.class, "Event: OnConnectGattListener - onConnectionFailed");
            clearState();
        }

        @Override
        public void onDisconnect() {
            Logger.Log(Blebricks.class, "Event: OnConnectGattListener - onDisconnect");
            clearState();
        }

        @Override
        public void onCharacteristicRead(final BluetoothGattCharacteristic characteristic, final String hex) {
            final String serviceUUID = characteristic.getService().getUuid().toString().trim().toUpperCase();
            final String characteristicUUID = characteristic.getUuid().toString().trim().toUpperCase();
            final String address = mConnectingAddress;
            Logger.Log(Blebricks.class, "Event: OnConnectGattListener - onCharacteristicRead - " + characteristicUUID + " - " + address + " - " + hex);

            Property property = null;
            for (int i = 0; i < mReadingProperties.size() && property == null; i++) {
                Property p = mReadingProperties.get(i);
                if (p.getServiceUUID().trim().toUpperCase().equals(serviceUUID) && p.getCharacteristicUUID().trim().toUpperCase().equals(characteristicUUID))
                    property = p;
            }
            mReadingProperties.remove(property);

            final Property readProperty = property;

            if (mOnReadFromConnectionListener != null) {
                OnReadFromConnectionListener l = mOnReadFromConnectionListener;
                mOnReadFromConnectionListener = null;
                l.OnReadFromConnectionDone(address, hex, readProperty);
            }


            if (getHasToAnalyze()) {
                for (Component c : Components.Array)
                    c.AnalyzeConnectionData(address, readProperty, BLEUtils.HexToBytes(hex), false);
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGattCharacteristic characteristic) {
            final String serviceUUID = characteristic.getService().getUuid().toString().trim().toUpperCase();
            final String characteristicUUID = characteristic.getUuid().toString().trim().toUpperCase();
            final String value = BLEUtils.BytesToHex(characteristic.getValue()).trim().toUpperCase();
            final String address = mConnectingAddress;

            Logger.Log(Blebricks.class, "Event: OnConnectGattListener - onCharacteristicWrite. Value: " + value);

            Command command = null;
            for (int i = 0; i < mWritingCommands.size() && command == null; i++) {
                Command c = mWritingCommands.get(i);
                if (c.getServiceUUID().trim().toUpperCase().equals(serviceUUID) && c.getCharacteristicUUID().trim().toUpperCase().equals(characteristicUUID) && (c.getDataType() + c.getValue()).trim().toUpperCase().equals(value))
                    command = c;
            }
            mWritingCommands.remove(command);

            final Command writeCommand = command;
            if (mOnCommandOnConnectionListener != null) {
                OnCommandOnConnectionListener l = mOnCommandOnConnectionListener;
                mOnCommandOnConnectionListener = null;
                Logger.Log(this, "Calling event");
                l.OnCommandOnConnectionDone(address, writeCommand);
            }
        }

        @Override
        public void onCharacteristicNotify(final BluetoothGattCharacteristic characteristic, final String hex) {
            Logger.Log(Blebricks.class, "Event: OnConnectGattListener - onCharacteristicNotify");
            final String serviceUUID = characteristic.getService().getUuid().toString().trim().toUpperCase();
            final String characteristicUUID = characteristic.getUuid().toString().trim().toUpperCase();
            final String address = mConnectingAddress;

            Property property = null;
            for (int i = 0; i < mNotificationProperties.size() && property == null; i++) {
                Property p = mNotificationProperties.get(i);
                if (p.getServiceUUID().trim().toUpperCase().equals(serviceUUID) && p.getCharacteristicUUID().trim().toUpperCase().equals(characteristicUUID))
                    property = p;
            }

            final Property notifyProperty = property;

            if (mOnNotificationFromConnectionListener != null)
                mOnNotificationFromConnectionListener.OnNotificationFromConnectionReceived(address, hex, notifyProperty);

            if (getHasToAnalyze()) {
                for (Component c : Components.Array)
                    c.AnalyzeConnectionData(address, notifyProperty, BLEUtils.HexToBytes(hex), true);
            }
        }

        @Override
        public void onReadRSSI(final int rssi) {
            Logger.Log(Blebricks.class, "Event: OnConnectGattListener - onReadRSSI");
            final String address = mConnectingAddress;

            if (mOnReadRSSIFromConnectionListener != null) {
                OnReadRSSIFromConnectionListener l = mOnReadRSSIFromConnectionListener;
                mOnReadRSSIFromConnectionListener = null;
                l.OnReadRSSIFromConnectionDone(address, rssi);
            }
        }
    }

    private static final OnGattListener mConnectGattListener = new OnConnectGattListener();
    //endregion

    //region Writing on connection

    /**
     * Send a command on a connection
     *
     * @param command  The command to send
     * @param listener To check the status
     */
    public static void CommandOnConnection(@NonNull final Command command, @Nullable final OnCommandOnConnectionListener listener) {
        Logger.Log(Blebricks.class, "Called: CommandOnConnection with command " + command.getDataType());
        final String address = mConnectingAddress;
        mOnCommandOnConnectionListener = listener;

        if (!mIsBLECompatible || !IsBluetoothEnabled() || !IsConnected()) {
            if (mOnCommandOnConnectionListener != null) {
                OnCommandOnConnectionListener l = mOnCommandOnConnectionListener;
                mOnCommandOnConnectionListener = null;
                l.OnCommandOnConnectionFailed(address != null ? address : "0.0.0.0", command);
            }
            if (mOnOneShotListener != null) {
                clearState();

                OnOneShotListener l = mOnOneShotListener;
                mOnOneShotListener = null;
                l.OnOneShotCommandFailed(address != null ? address : "0.0.0.0", command);
            }
            return;
        }

        List<BluetoothGattCharacteristic> characteristics = BLEUtils.GetCharacteristicsByUUID(mBluetoothGatt, command.getServiceUUID(), command.getCharacteristicUUID());
        if (characteristics.size() == 0) {
            if (mOnCommandOnConnectionListener != null) {
                OnCommandOnConnectionListener l = mOnCommandOnConnectionListener;
                mOnCommandOnConnectionListener = null;
                l.OnCommandOnConnectionFailed(address != null ? address : "0.0.0.0", command);
            }
            if (mOnOneShotListener != null) {
                clearState();

                OnOneShotListener l = mOnOneShotListener;
                mOnOneShotListener = null;
                l.OnOneShotCommandFailed(address != null ? address : "0.0.0.0", command);
            }
            return;
        }

        mWritingCommands.add(command);
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            characteristic.setValue(BLEUtils.HexToBytes(command.getDataType() + command.getValue()));
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }
    //endregion

    //region Reading from connection

    /**
     * Read a specific property from a connection
     *
     * @param property The property to read
     * @param listener To check the status
     */
    public static void ReadFromConnection(@NonNull final Property property, @Nullable final OnReadFromConnectionListener listener) {
        Logger.Log(Blebricks.class, "Called: ReadFromConnection with property " + property.toString());
        final String address = mConnectingAddress;

        mOnReadFromConnectionListener = listener;

        if (!mIsBLECompatible || !IsBluetoothEnabled() || !IsConnected()) {
            if (mOnReadFromConnectionListener != null) {
                OnReadFromConnectionListener l = mOnReadFromConnectionListener;
                mOnReadFromConnectionListener = null;
                l.OnReadFromConnectionFailed(address != null ? address : "0.0.0.0", property);
            }
            return;
        }

        List<BluetoothGattCharacteristic> characteristics = BLEUtils.GetCharacteristicsByUUID(mBluetoothGatt, property.getServiceUUID(), property.getCharacteristicUUID());
        if (characteristics.size() == 0) {
            if (mOnReadFromConnectionListener != null) {
                OnReadFromConnectionListener l = mOnReadFromConnectionListener;
                mOnReadFromConnectionListener = null;
                l.OnReadFromConnectionFailed(address != null ? address : "0.0.0.0", property);
            }
            return;
        }

        mReadingProperties.add(property);
        for (BluetoothGattCharacteristic characteristic : characteristics)
            mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Read RSSI value from a connection
     *
     * @param listener To check the status
     */
    public static void ReadRSSIFromConnection(@Nullable final OnReadRSSIFromConnectionListener listener) {
        Logger.Log(Blebricks.class, "Called: ReadRSSIFromConnection");
        final String address = mConnectingAddress;

        mOnReadRSSIFromConnectionListener = listener;

        if (!mIsBLECompatible || !IsBluetoothEnabled() || !IsConnected()) {
            if (mOnReadRSSIFromConnectionListener != null) {
                OnReadRSSIFromConnectionListener l = mOnReadRSSIFromConnectionListener;
                mOnReadRSSIFromConnectionListener = null;
                l.OnReadRSSIFromConnectionFailed(address != null ? address : "0.0.0.0");
            }
            return;
        }

        mBluetoothGatt.readRemoteRssi();
    }

    /**
     * Start receiving notification of a specific property from connection.
     * Add a listener to execute some code when a notification is received
     * with the setter SetOnNotificationFromConnectionListener
     *
     * @param property The property on which enable notifications
     * @param listener To check the status
     */
    public static void StartNotificationFromConnection(@NonNull final Property property, @Nullable final OnStartNotificationFromConnectionListener listener) {
        Logger.Log(Blebricks.class, "Called: StartNotificationFromConnection with property " + property.toString());
        final String address = mConnectingAddress;

        mOnStartNotificationFromConnectionListener = listener;

        if (!mIsBLECompatible || !IsBluetoothEnabled() || !IsConnected()) {
            if (mOnStartNotificationFromConnectionListener != null) {
                OnStartNotificationFromConnectionListener l = mOnStartNotificationFromConnectionListener;
                mOnStartNotificationFromConnectionListener = null;
                l.OnStartNotificationFromConnectionFailed(address != null ? address : "0.0.0.0", property);
            }
            return;
        }

        List<BluetoothGattCharacteristic> characteristics = BLEUtils.GetCharacteristicsByUUID(mBluetoothGatt, property.getServiceUUID(), property.getCharacteristicUUID());
        if (characteristics.size() == 0) {
            if (mOnStartNotificationFromConnectionListener != null) {
                OnStartNotificationFromConnectionListener l = mOnStartNotificationFromConnectionListener;
                mOnStartNotificationFromConnectionListener = null;
                l.OnStartNotificationFromConnectionFailed(address != null ? address : "0.0.0.0", property);
            }
            return;
        }

        for (BluetoothGattCharacteristic characteristic : characteristics) {
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mBluetoothGatt.setCharacteristicNotification(characteristic, true);

            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }

        mNotificationProperties.add(property);
        if (mOnStartNotificationFromConnectionListener != null) {
            OnStartNotificationFromConnectionListener l = mOnStartNotificationFromConnectionListener;
            mOnStartNotificationFromConnectionListener = null;
            l.OnStartNotificationFromConnectionDone(address != null ? address : "0.0.0.0", property);
        }
    }

    /**
     * Stop receiving notification of a specific property from connection
     *
     * @param property The property on which disable notifications
     * @param listener To check the status
     */
    public static void StopNotificationFromConnection(@NonNull final Property property, @Nullable final OnStopNotificationFromConnectionListener listener) {
        Logger.Log(Blebricks.class, "Called: StopNotificationFromConnection with property " + property.toString());
        final String address = mConnectingAddress;

        mOnStopNotificationFromConnectionListener = listener;

        if (!mIsBLECompatible || !IsBluetoothEnabled() || !IsConnected()) {
            if (mOnStopNotificationFromConnectionListener != null) {
                OnStopNotificationFromConnectionListener l = mOnStopNotificationFromConnectionListener;
                mOnStopNotificationFromConnectionListener = null;
                l.OnStopNotificationFromConnectionFailed(address != null ? address : "0.0.0.0", property);
            }
            return;
        }

        List<BluetoothGattCharacteristic> characteristics = BLEUtils.GetCharacteristicsByUUID(mBluetoothGatt, property.getServiceUUID(), property.getCharacteristicUUID());
        if (characteristics.size() == 0) {
            if (mOnStopNotificationFromConnectionListener != null) {
                OnStopNotificationFromConnectionListener l = mOnStopNotificationFromConnectionListener;
                mOnStopNotificationFromConnectionListener = null;
                l.OnStopNotificationFromConnectionFailed(address != null ? address : "0.0.0.0", property);
            }
            return;
        }

        for (BluetoothGattCharacteristic characteristic : characteristics) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, false);

            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }

        mNotificationProperties.remove(property);
        if (mOnStopNotificationFromConnectionListener != null) {
            OnStopNotificationFromConnectionListener l = mOnStopNotificationFromConnectionListener;
            mOnStopNotificationFromConnectionListener = null;
            l.OnStopNotificationFromConnectionDone(address != null ? address : "0.0.0.0", property);
        }
    }
    //endregion

    //region Writing on advertising

    /**
     * Start advertising a command to a specific address
     *
     * @param address  The recipient MAC address
     * @param command  The command to send
     * @param listener The listener to check the state of the advertising
     */
    public static void StartAdvertisingAddressedCommand(@NonNull final String address, @NonNull final Command command, final boolean coded, @Nullable final OnAddressedAdvertisingListener listener) {
        Logger.Log(Blebricks.class, "Called: StartAdvertisingAddressedCommand with address " + address + " and command " + command.getDataType());
        mOnAddressedAdvertisingListener = listener;

        if (!StartAdvertisingRaw(address, command, 0, coded)) {
            if (mOnAddressedAdvertisingListener != null) {
                OnAddressedAdvertisingListener l = mOnAddressedAdvertisingListener;
                mOnAddressedAdvertisingListener = null;
                l.OnAddressedAdvertisingFailed(address, command);
            }
        }
    }

    /**
     * Start advertising a command to a specific address for a specified amount of time
     *
     * @param address  The recipient MAC address
     * @param command  The command to send
     * @param timeout  The time, in seconds
     * @param listener The listener to check the state of the advertising
     */
    public static void AdvertisingAddressedCommand(@NonNull final String address, @NonNull final Command command, final int timeout, final boolean coded, @Nullable final OnAddressedAdvertisingListener listener) {
        Logger.Log(Blebricks.class, "Called: AdvertisingAddressedCommand with address " + address + " and command " + command.getDataType() + " (timeout " + timeout + "s)");
        mOnAddressedAdvertisingListener = listener;

        if (!StartAdvertisingRaw(address, command, timeout, coded)) {
            if (mOnAddressedAdvertisingListener != null) {
                OnAddressedAdvertisingListener l = mOnAddressedAdvertisingListener;
                mOnAddressedAdvertisingListener = null;
                l.OnAddressedAdvertisingFailed(address, command);
            }
        }
    }

    /**
     * Start advertising a command in broadcast
     *
     * @param command  The command to send
     * @param listener The listener to check the state of the advertising
     */
    public static void StartAdvertisingBroadcastCommand(@NonNull final Command command, final boolean coded, @Nullable final OnBroadcastAdvertisingListener listener) {
        Logger.Log(Blebricks.class, "Called: StartAdvertisingBroadcastCommand with command " + command.getDataType());
        mOnBroadcastAdvertisingListener = listener;

        if (!StartAdvertisingRaw(Constants.ADV_BROADCAST_ADDRESS, command, 0, coded)) {
            if (mOnBroadcastAdvertisingListener != null) {
                OnBroadcastAdvertisingListener l = mOnBroadcastAdvertisingListener;
                mOnBroadcastAdvertisingListener = null;
                l.OnBroadcastAdvertisingFailed(command);
            }
        }
    }

    /**
     * Start advertising a command in broadcast for a specified amount of time
     *
     * @param command  The command to send
     * @param timeout  The time, in seconds
     * @param listener The listener to check the state of the advertising
     */
    public static void AdvertisingBroadcastCommand(@NonNull final Command command, final int timeout, final boolean coded, @Nullable final OnBroadcastAdvertisingListener listener) {
        Logger.Log(Blebricks.class, "Called: AdvertisingBroadcastCommand with command " + command.getDataType() + " (timeout " + timeout + "s)");
        mOnBroadcastAdvertisingListener = listener;

        if (!StartAdvertisingRaw(Constants.ADV_BROADCAST_ADDRESS, command, timeout, coded)) {
            if (mOnBroadcastAdvertisingListener != null) {
                OnBroadcastAdvertisingListener l = mOnBroadcastAdvertisingListener;
                mOnBroadcastAdvertisingListener = null;
                l.OnBroadcastAdvertisingFailed(command);
            }
        }
    }

    /**
     * Stop advertising
     */
    public static void StopAdvertisingCommand() {
        Logger.Log(Blebricks.class, "Called: StopAdvertisingCommand");
        if (IsAdvertising())
            clearState();
    }

    private static boolean StartAdvertisingRaw(@NonNull final String address, @NonNull final Command command, final int timeout, final boolean coded) {
        Logger.Log(Blebricks.class, "Called: StartAdvertisingRaw with address " + address + " and command " + command.getDataType() + " (timeout " + timeout + "s) (coded: " + coded + ")");
        if (!mIsBLECompatible || !IsBluetoothEnabled())
            return false;

        clearState();

        mDelayedActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdvertisingAddress = address.trim().toUpperCase();
                mAdvertisingCommand = command;


                String sendingMessage = BLEUtils.MacAddressRemoveColons(mAdvertisingAddress) + mAdvertisingCommand.getDataType().trim().toUpperCase() + mAdvertisingCommand.getValue().trim().toUpperCase();
                if (sendingMessage.length() > 54)
                    sendingMessage = sendingMessage.substring(0, 54);
                Logger.Log(Blebricks.class, "Advertising message: " + sendingMessage + " (coded: " + coded + ")");

                AdvertisingSetParameters advSetParameters = null;
                AdvertiseSettings advSettings = null;
                if (android.os.Build.VERSION.SDK_INT >= O) {
                    AdvertisingSetParameters.Builder builder = new AdvertisingSetParameters.Builder()
                            .setLegacyMode(!coded)
                            .setInterval(AdvertisingSetParameters.INTERVAL_MIN)
                            .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MAX);

                    if (coded) {
                        builder.setPrimaryPhy(BluetoothDevice.PHY_LE_CODED)
                                .setSecondaryPhy(BluetoothDevice.PHY_LE_CODED);
                    }

                    advSetParameters = builder.build();
                } else {
                    advSettings = new AdvertiseSettings.Builder()
                            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                            .setTimeout(0)
                            .setConnectable(false)
                            .build();
                }

                AdvertiseData advData = new AdvertiseData.Builder()
                        .setIncludeDeviceName(false)
                        .setIncludeTxPowerLevel(false)
                        .addManufacturerData(BLEUtils.HexToInt("0668"), BLEUtils.HexToBytes(sendingMessage))
                        .build();

                if (android.os.Build.VERSION.SDK_INT >= O) {
                    mBluetoothAdvertiser.startAdvertisingSet(advSetParameters, advData, null, null, null, (AdvertisingSetCallback) mAdvertiseSetCallback);
                } else {
                    mBluetoothAdvertiser.startAdvertising(advSettings, advData, mAdvertiseCallback);
                }


                mCurrentState = State.ADVERTISING;

                if (timeout > 0) {
                    mAdvertiseTimeoutHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            StopAdvertisingCommand();
                        }
                    }, timeout);
                }
            }
        }, 500);
        return true;
    }

    /**
     * Check if is currently advertising a command
     *
     * @return true if advertising, false otherwise
     */
    public static boolean IsAdvertising() {
        Logger.Log(Blebricks.class, "Called: IsAdvertising");
        if (!mIsBLECompatible || !IsBluetoothEnabled())
            return false;

        return mCurrentState == State.ADVERTISING;
    }

    private static final AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(final AdvertiseSettings settingsInEffect) {
            Logger.Log(Blebricks.class, "Event: AdvertiseCallback - onStartSuccess");
            super.onStartSuccess(settingsInEffect);

            final String address = mAdvertisingAddress;
            final Command command = mAdvertisingCommand;

            if (address != null && !address.trim().toUpperCase().equals(Constants.ADV_BROADCAST_ADDRESS)) {
                if (mOnAddressedAdvertisingListener != null)
                    mOnAddressedAdvertisingListener.OnAddressedAdvertisingStarted(address, command);
            } else {
                if (mOnBroadcastAdvertisingListener != null)
                    mOnBroadcastAdvertisingListener.OnBroadcastAdvertisingStarted(command);
            }
        }

        @Override
        public void onStartFailure(final int errorCode) {
            Logger.Log(Blebricks.class, "Event: AdvertiseCallback - onStartFailure. Error code: " + errorCode);
            super.onStartFailure(errorCode);

            final String address = mAdvertisingAddress;
            final Command command = mAdvertisingCommand;

            if (address != null && !address.trim().toUpperCase().equals(Constants.ADV_BROADCAST_ADDRESS)) {
                if (mOnAddressedAdvertisingListener != null)
                    mOnAddressedAdvertisingListener.OnAddressedAdvertisingFailed(address, command);
            } else {
                if (mOnBroadcastAdvertisingListener != null)
                    mOnBroadcastAdvertisingListener.OnBroadcastAdvertisingFailed(command);
            }
        }
    };

    @RequiresApi(Build.VERSION_CODES.O)
    private static Object mAdvertiseSetCallback;
    //endregion

    //region Oneshot

    /**
     * Send a One-Shot-Command (Connect, Write the command, Disconnect) to the nearest BLE-B
     *
     * @param command  The command to send
     * @param listener The listener to check the status
     */
    public static void OneShotCommand(@NonNull final Command command, @Nullable final OnOneShotListener listener) {
        Logger.Log(Blebricks.class, "Called: OneShotCommand with command " + command.getDataType());
        mOnOneShotListener = listener;

        final String tmpEasyAddress = (mEasyAddress != null && mEasyAddress.trim().length() > 0) ? mEasyAddress : "00:00:00:00:00:00";

        if (!mIsBLECompatible || !IsBluetoothEnabled()) {
            if (mOnOneShotListener != null) {
                OnOneShotListener l = mOnOneShotListener;
                mOnOneShotListener = null;
                l.OnOneShotCommandFailed(tmpEasyAddress, command);
            }
            return;
        }

        mConnectingAddress = tmpEasyAddress;
        mOneShotCommand = command;

        if (StartScanRaw(mScanOneShotResultListener)) {
            mOneShotCommandTimeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    mConnectingAddress = null;
                    mOneShotCommand = null;
                    if (mOnOneShotListener != null) {
                        OnOneShotListener l = mOnOneShotListener;
                        mOnOneShotListener = null;
                        l.OnOneShotCommandFailed(tmpEasyAddress, command);
                    }
                }
            }, 15000);

            mCurrentState = State.ONESHOT_SCANNING;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    Logger.Warn(Blebricks.class, "Timeout without advertise, restarting oneshotcommand..");
                    OneShotCommand(command, listener);
                }
            }, Constants.NO_ADVERTISE_SCAN_TIMEOUT_MS);
        } else {
            mScanOneShotResultListener.clear();
            mConnectingAddress = null;
            mOneShotCommand = null;
            if (mOnOneShotListener != null) {
                OnOneShotListener l = mOnOneShotListener;
                mOnOneShotListener = null;
                l.OnOneShotCommandFailed(tmpEasyAddress, command);
            }
        }
    }

    /**
     * Send a One-Shot-Command (Connect, Write the command, Disconnect) to a specific MAC address
     *
     * @param address  The MAC Address of the BLE-B
     * @param command  The command to send
     * @param listener The listener to check the status
     */
    public static void OneShotCommand(@NonNull final String address, @NonNull final Command command, @Nullable final OnOneShotListener listener) {
        Logger.Log(Blebricks.class, "Called: OneShotCommand with address " + address + " and command " + command.getDataType() + command.getValue());

        mOnOneShotListener = listener;

        if (!mIsBLECompatible || !IsBluetoothEnabled() || address.trim().length() == 0) {
            if (mOnOneShotListener != null) {
                OnOneShotListener l = mOnOneShotListener;
                mOnOneShotListener = null;
                l.OnOneShotCommandFailed(address.trim().toUpperCase(), command);
            }
            return;
        }

        mConnectingAddress = address.trim().toUpperCase();
        mOneShotCommand = command;

        mScanOneShotAddressedResultListener.setAddress(address.trim().toUpperCase());
        if (StartScanRaw(mScanOneShotAddressedResultListener)) {
            mOneShotCommandTimeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    mConnectingAddress = null;
                    mOneShotCommand = null;
                    if (mOnOneShotListener != null) {
                        OnOneShotListener l = mOnOneShotListener;
                        mOnOneShotListener = null;
                        l.OnOneShotCommandFailed(address.trim().toUpperCase(), command);
                    }
                }
            }, 15000);

            mCurrentState = State.ONESHOT_SCANNING;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();
                    Logger.Warn(Blebricks.class, "Timeout without advertise, restarting addressed oneshotcommand to..");
                    OneShotCommand(address, command, listener);
                }
            }, Constants.NO_ADVERTISE_SCAN_TIMEOUT_MS);
        } else {
            mScanOneShotAddressedResultListener.clear();
            mConnectingAddress = null;
            mOneShotCommand = null;
            if (mOnOneShotListener != null) {
                OnOneShotListener l = mOnOneShotListener;
                mOnOneShotListener = null;
                l.OnOneShotCommandFailed(address.trim().toUpperCase(), command);
            }

        }
    }

    /**
     * Stop the sending of a One-Shot-Command
     */
    public static void StopOneShotCommand() {
        Logger.Log(Blebricks.class, "Called: StopOneShotCommand");

        if (IsOneShotting()) {
            clearState(false);
            if (mOnOneShotListener != null) {
                OnOneShotListener l = mOnOneShotListener;
                mOnOneShotListener = null;
                l.OnOneShotCommandStopped(mConnectingAddress != null ? mConnectingAddress : "00:00:00:00:00:00", mOneShotCommand != null ? mOneShotCommand : new Command("", "", "", ""));
            }
        }
    }

    /**
     * Check if is currently sending a One-Shot-Command
     *
     * @return true if is sending a One-Shot-Command, false otherwise
     */
    public static boolean IsOneShotting() {
        Logger.Log(Blebricks.class, "Called: IsOneShotting");
        if (!mIsBLECompatible || !IsBluetoothEnabled())
            return false;

        return mCurrentState == State.ONESHOT_CONNECTED || mCurrentState == State.ONESHOT_SCANNING || mCurrentState == State.ONESHOT_SERVICES;
    }

    private static final OnScanResultListener mScanOneShotResultListener = new OnScanResultListener() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            clearState();
            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOneShotListener != null) {
                        OnOneShotListener l = mOnOneShotListener;
                        mOnOneShotListener = null;
                        l.OnOneShotCommandFailed(mEasyAddress != null ? mEasyAddress : "00:00:00:00:00:00", mOneShotCommand != null ? mOneShotCommand : new Command("", "", "", ""));
                    }
                }
            }, 500);
        }

        private Handler mFirstAddressFoundHandler = new Handler();
        private boolean mIsHandlerActive = false;
        private String mTempMaxRSSIAddress = null;
        private int mTempMaxRSSIValue = -1;

        @Override
        public void clear() {
            super.clear();
            mFirstAddressFoundHandler.removeCallbacksAndMessages(null);
            mIsHandlerActive = false;
            mTempMaxRSSIAddress = null;
            mTempMaxRSSIValue = -1;
        }

        @Override
        public void onAnyResultReceived() {
            mDelayedActionHandler.removeCallbacksAndMessages(null); // remove "Timeout after no advertise scan restart"
        }

        @Override
        public void onResult(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            if (mEasyAddress == null || mEasyAddress.length() == 0) {
                if (mIsHandlerActive) {
                    if (mTempMaxRSSIValue < rssi) {
                        mTempMaxRSSIAddress = address.trim().toUpperCase();
                        mTempMaxRSSIValue = rssi;
                    }
                } else { // first run
                    mTempMaxRSSIAddress = address.trim().toUpperCase();
                    mTempMaxRSSIValue = rssi;

                    mIsHandlerActive = true;
                    mFirstAddressFoundHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEasyAddress = mTempMaxRSSIAddress;
                            clear();
                        }
                    }, 1000);
                }
            } else {
                clearState();

                if (!mEasyAddress.equals(mConnectingAddress)) // se 00:00:00:00:00:00, quindi first exec
                    mConnectingAddress = mEasyAddress;

                mGattCallback.setOnGattListener(mOneShotGattListener);

                if (Build.VERSION.SDK_INT >= O && phyCoded)
                    mBluetoothGatt = scanResult.getDevice().connectGatt(mApplicationContext, false, mGattCallback, BluetoothDevice.TRANSPORT_AUTO, BluetoothDevice.PHY_LE_CODED_MASK);
                else
                    mBluetoothGatt = scanResult.getDevice().connectGatt(mApplicationContext, false, mGattCallback);

                mCurrentState = State.ONESHOT_SERVICES;
            }
        }

        @Override
        public boolean filter(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            return mEasyAddress == null || mEasyAddress.length() == 0 || address.trim().toUpperCase().equals(mEasyAddress);
        }
    };

    private static final OnScanAddressResultListener mScanOneShotAddressedResultListener = new OnScanAddressResultListener() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            clearState();
            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOneShotListener != null) {
                        OnOneShotListener l = mOnOneShotListener;
                        mOnOneShotListener = null;
                        l.OnOneShotCommandFailed(mConnectingAddress != null ? mConnectingAddress : "00:00:00:00:00:00", mOneShotCommand != null ? mOneShotCommand : new Command("", "", "", ""));
                    }
                }
            }, 500);
        }

        @Override
        public void onAnyResultReceived() {
            mDelayedActionHandler.removeCallbacksAndMessages(null); // remove "Timeout after no advertise scan restart"
        }

        @Override
        public void onResult(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            clearState();

            mGattCallback.setOnGattListener(mOneShotGattListener);


            if (Build.VERSION.SDK_INT >= O && phyCoded)
                mBluetoothGatt = scanResult.getDevice().connectGatt(mApplicationContext, false, mGattCallback, BluetoothDevice.TRANSPORT_AUTO, BluetoothDevice.PHY_LE_CODED_MASK);
            else
                mBluetoothGatt = scanResult.getDevice().connectGatt(mApplicationContext, false, mGattCallback);

            mCurrentState = State.ONESHOT_SERVICES;
        }

        @Override
        public boolean filter(final ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded) {
            return (address.trim().toUpperCase()).equals(getAddress());
        }
    };

    private static class OnOneShotGattListener extends OnConnectGattListener {
        @Override
        public void onConnected() {
            Logger.Log(Blebricks.class, "Event: OnOneShotGattListener - onConnected");
            super.onConnected();
            final Command command = mOneShotCommand;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCurrentState = State.ONESHOT_CONNECTED;
                    CommandOnConnection(command, null);
                }
            }, 500);
        }

        @Override
        public void onConnectionFailed() {
            Logger.Log(Blebricks.class, "Event: OnOneShotGattListener - onConnectionFailed");
            super.onConnectionFailed();
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGattCharacteristic characteristic) {
            Logger.Log(Blebricks.class, "Event: OnOneShotGattListener - onCharacteristicWrite");
            super.onCharacteristicWrite(characteristic);

            final String address = mConnectingAddress;
            final Command command = mOneShotCommand;
            final int length = (command.getDataType().length() + command.getValue().length()) / 2;

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearState();

                    mDelayedActionHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnOneShotListener != null)
                                mOnOneShotListener.OnOneShotCommandDone(address, command);
                        }
                    }, 1000);
                }
            }, 200 * length);
        }

        @Override
        public void onDisconnect() {
            Logger.Log(Blebricks.class, "Event: OnOneShotGattListener - onDisconnect");
            final String address = mConnectingAddress;
            final Command command = mOneShotCommand;

            super.onDisconnect();

            mDelayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOneShotListener != null) {
                        OnOneShotListener l = mOnOneShotListener;
                        mOnOneShotListener = null;
                        l.OnOneShotCommandFailed(address != null ? address : "00:00:00:00:00:00", command);
                    }
                }
            }, 500);
        }
    }

    private static final OnGattListener mOneShotGattListener = new OnOneShotGattListener();
    //endregion

    public static void clearState() {
        clearState(true);
    }

    private static void clearState(boolean callListener) {
        Logger.Log(Blebricks.class, "Called: clearState (was: " + mCurrentState + ")");

        mDelayedActionHandler.removeCallbacksAndMessages(null);

        if (!mIsBLECompatible || !IsBluetoothEnabled()) {
            mCurrentState = State.NONE;
            return;
        }

        /*
        for(Component c : Components.Array)
            c.setConnected(false);
        */

        switch (mCurrentState) {
            case SCANNING: {
                mScanResultListener.clear();
                mScanSpecificResultListener.clear();
                mScanGlobalResultListener.clear();

                mScanCallback.setOnScanResultListener(null);

                mBluetoothScanner.stopScan(mScanCallback);
                mBluetoothScanner.flushPendingScanResults(mScanCallback);
            }
            break;
            case CONNECT_SCANNING: {
                mScanConnectResultListener.clear();

                mConnectionTimeoutHandler.removeCallbacksAndMessages(null);
                mScanCallback.setOnScanResultListener(null);

                mBluetoothScanner.stopScan(mScanCallback);
                mBluetoothScanner.flushPendingScanResults(mScanCallback);
            }
            break;
            case CONNECT_SERVICES: {
                final String address = mConnectingAddress;
                mConnectingAddress = null;

                mGattCallback.setOnGattListener(null);
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }

                mCurrentState = State.NONE;

                if (callListener && mOnConnectionListener != null) {
                    OnConnectionListener l = mOnConnectionListener;
                    mOnConnectionListener = null;
                    l.OnConnectionFailed(address);
                }
            }
            break;
            case CONNECT_CONNECTED: {
                final String address = mConnectingAddress;

                mConnectingAddress = null;

                mGattCallback.setOnGattListener(null);
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }

                mCurrentState = State.NONE;

                if (callListener && mOnDisconnectionListener != null)
                    mOnDisconnectionListener.OnDisconnection(address);
            }
            break;
            case ADVERTISING: {
                final String address = mAdvertisingAddress;
                final Command command = mAdvertisingCommand;

                mAdvertisingAddress = null;
                mAdvertisingCommand = null;

                mAdvertiseTimeoutHandler.removeCallbacksAndMessages(null);
                if (mBluetoothAdvertiser != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        mBluetoothAdvertiser.stopAdvertisingSet((AdvertisingSetCallback) mAdvertiseSetCallback);
                    } else {
                        mBluetoothAdvertiser.stopAdvertising(mAdvertiseCallback);
                    }
                }

                mCurrentState = State.NONE;

                if (address != null && !address.trim().toUpperCase().equals(Constants.ADV_BROADCAST_ADDRESS)) {
                    if (callListener && mOnAddressedAdvertisingListener != null) {
                        OnAddressedAdvertisingListener l = mOnAddressedAdvertisingListener;
                        mOnAddressedAdvertisingListener = null;
                        l.OnAddressedAdvertisingStopped(address, command);
                    }
                } else {
                    if (callListener && mOnBroadcastAdvertisingListener != null) {
                        OnBroadcastAdvertisingListener l = mOnBroadcastAdvertisingListener;
                        mOnBroadcastAdvertisingListener = null;
                        l.OnBroadcastAdvertisingStopped(command);
                    }
                }
            }
            break;
            case ONESHOT_SCANNING: {
                mScanOneShotResultListener.clear();
                mScanOneShotAddressedResultListener.clear();

                mOneShotCommandTimeoutHandler.removeCallbacksAndMessages(null);
                mScanCallback.setOnScanResultListener(null);

                mBluetoothScanner.stopScan(mScanCallback);
                mBluetoothScanner.flushPendingScanResults(mScanCallback);
            }
            break;
            case ONESHOT_SERVICES: {
                final String address = mConnectingAddress;
                final Command command = mOneShotCommand;

                mConnectingAddress = null;
                mOneShotCommand = null;

                mGattCallback.setOnGattListener(null);
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }

                mCurrentState = State.NONE;

                if (callListener && mOnOneShotListener != null) {
                    OnOneShotListener l = mOnOneShotListener;
                    mOnOneShotListener = null;
                    l.OnOneShotCommandFailed(address, command);
                }
            }
            break;
            case ONESHOT_CONNECTED: {
                mConnectingAddress = null;
                mOneShotCommand = null;

                mGattCallback.setOnGattListener(null);
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }
            }
            break;
        }

        mCurrentState = State.NONE;
    }

    public static class Components {
        public static final BLEB BLEB = new BLEB();
        public static final CTrace CTrace = new CTrace();
        public static final BLEENV BLEENV = new BLEENV();
        public static final BLEIMU BLEIMU = new BLEIMU();
        public static final BLEPDM BLEPDM = new BLEPDM();
        public static final BLEPRT BLEPRT = new BLEPRT();
        public static final BLEREL BLEREL = new BLEREL();
        public static final BLERGB BLERGB = new BLERGB();
        public static final BLERHT BLERHT = new BLERHT();
        public static final BLEUVA BLEUVA = new BLEUVA();
        public static final BLECAP BLECAP = new BLECAP();
        public static final BLESFX BLESFX = new BLESFX();
        public static final BLESMS BLESMS = new BLESMS();
        public static final BLEBUZ BLEBUZ = new BLEBUZ();
        public static final BLEWPL BLEWPL = new BLEWPL();
        public static final BLEVBR BLEVBR = new BLEVBR();
        public static final BLEIRR BLEIRR = new BLEIRR();
        public static final BLEIRT BLEIRT = new BLEIRT();
        public static final BLEPOW BLEPOW = new BLEPOW();
        public static final BLECO2 BLECO2 = new BLECO2();
        public static final BLEGPS BLEGPS = new BLEGPS();
        public static final BLEPMX BLEPMX = new BLEPMX();
        public static final BLEACC BLEACC = new BLEACC();
        public static final BLEVID BLEVID = new BLEVID();
        public static final BLECLT BLECLT = new BLECLT();
        public static final BLEADX BLEADX = new BLEADX();
        public static final BLEESP BLEESP = new BLEESP();
        public static final BLECMS BLECMS = new BLECMS();
        public static final BLERMS BLERMS = new BLERMS();
        public static final Datalogger Datalogger = new Datalogger();
        public static final DirectInteraction DirectInteraction = new DirectInteraction();
        public static final SentryMode SentryMode = new SentryMode();

        public static Component[] Array = new Component[]{
                BLEB,
                CTrace,
                BLEENV,
                BLEIMU,
                BLEPDM,
                BLEPRT,
                BLEREL,
                BLERGB,
                BLERHT,
                BLEUVA,
                BLECAP,
                BLESFX,
                BLESMS,
                BLEBUZ,
                BLEWPL,
                BLEVBR,
                BLEIRR,
                BLEIRT,
                BLEPOW,
                BLECO2,
                BLEGPS,
                BLEPMX,
                BLEACC,
                BLEVID,
                BLECLT,
                BLEADX,
                BLEESP,
                BLECMS,
                BLERMS,
                DirectInteraction,
                SentryMode,
                Datalogger
        };
    }

    public interface OnDeviceFoundFromScanListener {
        void OnDeviceFound(final String address, final String name, final int rssi, final String manufacturerData, final int battery, final String rawData);
    }

    public interface OnAdvertiseReceivedFromScanListener {
        void OnAdvertiseReceived(final String address, final String name, final int rssi, final String manufacturerData, final int battery, final String rawData);
    }

    public interface OnScanListener {
        void OnScanStarted();

        void OnScanFailed(final int errorCode);

        void OnScanStopped();
    }

    public interface OnConnectionListener {
        void OnConnectionFailed(final String address);

        void OnConnectionStopped(final String address);

        void OnConnectionDone(final String address, final String name);
    }

    public interface OnDisconnectionListener {
        void OnDisconnection(final String address);
    }

    public interface OnCommandOnConnectionListener {
        void OnCommandOnConnectionDone(final String address, final Command command);

        void OnCommandOnConnectionFailed(final String address, final Command command);
    }

    public interface OnReadFromConnectionListener {
        void OnReadFromConnectionDone(final String address, final String value, final Property property);

        void OnReadFromConnectionFailed(final String address, final Property property);
    }

    public interface OnReadRSSIFromConnectionListener {
        void OnReadRSSIFromConnectionDone(final String address, final int rssi);

        void OnReadRSSIFromConnectionFailed(final String address);
    }

    public interface OnNotificationFromConnectionListener {
        void OnNotificationFromConnectionReceived(final String address, final String value, final Property property);
    }

    public interface OnStartNotificationFromConnectionListener {
        void OnStartNotificationFromConnectionDone(final String address, final Property property);

        void OnStartNotificationFromConnectionFailed(final String address, final Property property);
    }

    public interface OnStopNotificationFromConnectionListener {
        void OnStopNotificationFromConnectionDone(final String address, final Property property);

        void OnStopNotificationFromConnectionFailed(final String address, final Property property);
    }

    public interface OnAddressedAdvertisingListener {
        void OnAddressedAdvertisingStarted(final String address, final Command command);

        void OnAddressedAdvertisingFailed(final String address, final Command command);

        void OnAddressedAdvertisingStopped(final String address, final Command command);
    }

    public interface OnBroadcastAdvertisingListener {
        void OnBroadcastAdvertisingStarted(final Command command);

        void OnBroadcastAdvertisingFailed(final Command command);

        void OnBroadcastAdvertisingStopped(final Command command);
    }

    public interface OnOneShotListener {
        void OnOneShotCommandDone(final String address, final Command command);

        void OnOneShotCommandFailed(final String address, final Command command);

        void OnOneShotCommandStopped(final String address, final Command command);
    }

    public interface OnAskPermissionsListener {
        void OnAskPermissionsDone();
    }

    public interface OnAskEnableBluetoothListener {
        void OnAskEnableBluetoothResponse(final boolean enabled);
    }

    public interface OnAskEnableGpsListener {
        void OnAskEnableGpsResponse(final boolean enabled);
    }
}
