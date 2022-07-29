/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.listener;

public abstract class OnScanAddressResultListener extends OnScanResultListener {
    private String mAddress = "null";

    public void setAddress(String address) {
        if (address == null)
            mAddress = "null";
        else if(address.trim().length() == 0)
            mAddress = "empty";
        else
            mAddress = address.trim().toUpperCase();
    }

    protected String getAddress() {
        return mAddress;
    }

    @Override
    public void clear() {
        super.clear();
        setAddress(null);
    }
}
