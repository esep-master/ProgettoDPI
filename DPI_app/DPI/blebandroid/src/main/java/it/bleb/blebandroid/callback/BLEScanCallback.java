/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.callback;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import it.bleb.blebandroid.listener.OnScanResultListener;
import it.bleb.blebandroid.utils.Logger;

import java.util.List;

public class BLEScanCallback extends ScanCallback {
    private OnScanResultListener mOnScanResultListener;

    public void setOnScanResultListener(OnScanResultListener onScanResultListener) {
        mOnScanResultListener = onScanResultListener;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        
        if (mOnScanResultListener != null && result != null) {
            Logger.Log(this, result.toString());
            mOnScanResultListener.onScanResult(result);
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);

        if (mOnScanResultListener != null) {
            for (ScanResult result : results) {
                mOnScanResultListener.onScanResult(result);
            }
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);

        if (mOnScanResultListener != null)
            mOnScanResultListener.onScanFailed(errorCode);
    }
}
