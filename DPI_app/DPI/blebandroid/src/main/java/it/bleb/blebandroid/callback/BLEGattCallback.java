/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.callback;

import android.bluetooth.*;

import it.bleb.blebandroid.listener.OnGattListener;
import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Constants;
import it.bleb.blebandroid.utils.Logger;

import java.util.UUID;

public class BLEGattCallback extends BluetoothGattCallback {
    private OnGattListener mOnGattListener;

    public void setOnGattListener(OnGattListener onGattListener) {
        this.mOnGattListener = onGattListener;
    }

    public BLEGattCallback() {
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        switch (newState) {
            case BluetoothAdapter.STATE_CONNECTED:
                gatt.discoverServices();
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                if (mOnGattListener != null)
                    mOnGattListener.onDisconnect();
                break;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        UUID mainService = UUID.fromString(Constants.UUID_MAIN_SERVICE);
        UUID sendCommandCharacteristic = UUID.fromString(Constants.UUID_SEND_COMMAND_CHARACTERISTIC);

        UUID gatewayService = UUID.fromString(Constants.UUID_GATEWAY_SERVICE);
        UUID gatewayCharacteristic = UUID.fromString(Constants.UUID_GATEWAY_CHARACTERISTIC);

        for (BluetoothGattService service : gatt.getServices()) {
            Logger.Log(this, "Found service:" + service.getUuid().toString());
            for (BluetoothGattCharacteristic charachteristic : service.getCharacteristics()) {
                Logger.Log(this, "Found characteristic:" + charachteristic.getUuid().toString() + "(" +service.getUuid().toString() + ")");
            }
        }

        if (gatt.getService(mainService) != null && gatt.getService(mainService).getCharacteristic(sendCommandCharacteristic) != null) {
            if (mOnGattListener != null)
                mOnGattListener.onConnected();
        } else {
            if (gatt.getService(gatewayService) != null && gatt.getService(gatewayService).getCharacteristic(gatewayCharacteristic) != null) {
                if (mOnGattListener != null)
                    mOnGattListener.onConnected();
            } else {
                if (mOnGattListener != null)
                    mOnGattListener.onConnectionFailed();

                gatt.disconnect();
                gatt.close();
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            byte[] charValue = characteristic.getValue();
            if (mOnGattListener != null)
                mOnGattListener.onCharacteristicRead(characteristic, BLEUtils.BytesToHex(charValue));
        } else {

        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (mOnGattListener != null)
                mOnGattListener.onCharacteristicWrite(characteristic);
        } else {

        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

        byte[] charValue = characteristic.getValue();
        if (mOnGattListener != null)
            mOnGattListener.onCharacteristicNotify(characteristic, BLEUtils.BytesToHex(charValue));
    }


    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (rssi != Integer.MIN_VALUE && rssi != Integer.MAX_VALUE)
                mOnGattListener.onReadRSSI(rssi);
        } else {

        }
    }
}
