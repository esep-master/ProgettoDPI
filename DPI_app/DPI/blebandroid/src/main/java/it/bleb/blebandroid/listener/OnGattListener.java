/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.listener;

import android.bluetooth.BluetoothGattCharacteristic;

public interface OnGattListener {
    void onConnected();
    void onConnectionFailed();
    void onDisconnect();
    void onCharacteristicRead(BluetoothGattCharacteristic characteristic, String hex);
    void onCharacteristicWrite(BluetoothGattCharacteristic characteristic);
    void onCharacteristicNotify(BluetoothGattCharacteristic characteristic, String hex);
    void onReadRSSI(int rssi);
}
