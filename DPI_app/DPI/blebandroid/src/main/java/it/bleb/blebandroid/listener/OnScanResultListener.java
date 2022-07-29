/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.listener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Logger;

public abstract class OnScanResultListener {
    public final void onScanResult(ScanResult scanResult) {
        onAnyResultReceived();

        final String name = scanResult.getDevice().getName() != null ? scanResult.getDevice().getName() : "N/A";
        final String address = scanResult.getDevice().getAddress() != null ? scanResult.getDevice().getAddress().trim().toUpperCase() : "00:00:00:00:00:00";
        final int rssi = scanResult.getRssi();

        if (scanResult.getDevice().getName() != null)
            Logger.Log(this, "[SCAN] Found: " + name + " " + address + " (" + rssi + ")");
        //else
        //    Logger.Log(this, "[SCAN] Found: " + address + " ("+ rssi + ")");

        final ScanRecord scanRecord = scanResult.getScanRecord();
        if (scanRecord == null)
            return;


        if (scanRecord != null) {
            // la bleb attuale lo invia storto.. ma in realtà dovrebbe esser così (provo in entrambi i modi per sicurezza)
            String manufacturerDataTmp = BLEUtils.BytesToHex(scanRecord.getManufacturerSpecificData(BLEUtils.HexToInt("6806")));
            if (manufacturerDataTmp.trim().length() == 0)
                manufacturerDataTmp = BLEUtils.BytesToHex(scanRecord.getManufacturerSpecificData(BLEUtils.HexToInt("0668")));

            manufacturerDataTmp = manufacturerDataTmp.trim();

            if (manufacturerDataTmp.length() > 0) {
                final String manufacturerData = manufacturerDataTmp;
                final byte[] rawDataBytes = scanRecord.getBytes() != null ? scanRecord.getBytes() : new byte[0];
                final String rawData = BLEUtils.BytesToHex(rawDataBytes);

                int batteryTmp = -1;
                int index = 0;
                while (index < rawDataBytes.length - 4) {
                    if (rawDataBytes[index] == 0x4 && rawDataBytes[index + 1] == 0x16 && rawDataBytes[index + 2] == 0xf && rawDataBytes[index + 3] == 0x18)
                        batteryTmp = rawDataBytes[index + 4];
                    index += rawDataBytes[index] + 1;
                }
                final int battery = batteryTmp;

                final boolean phyCoded = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && scanResult.getPrimaryPhy() == BluetoothDevice.PHY_LE_CODED;

                if (filter(scanResult, name, address, rssi, manufacturerData, battery, rawData, phyCoded))
                    onResult(scanResult, name, address, rssi, manufacturerData, battery, rawData, phyCoded);
            }
        }

    }

    public void onScanFailed(int errorCode) {
        Logger.Log(this, "[SCAN] Error: " + errorCode);
    }

    public void clear() {
    }

    public abstract void onAnyResultReceived();

    public abstract void onResult(ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded);

    public abstract boolean filter(ScanResult scanResult, final String name, final String address, final int rssi, final String manufacturerData, final int battery, final String rawData, final boolean phyCoded);
}
