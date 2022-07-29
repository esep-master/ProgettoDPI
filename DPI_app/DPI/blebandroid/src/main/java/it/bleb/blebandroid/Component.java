/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import android.os.Handler;

public abstract class Component {
    private static final int DELAY_REMOVER_MILLIS = 60000;

    private Handler mDelayDisconnectHandler = new Handler();
    private boolean mIsConnected = false;
    private boolean mIsAlwaysConnected = false;

    private String mName;
    private String mDescription;

    Component(final String name, final String description) {
        setName(name);
        setDescription(description);
    }

    Component(final String name) {
        this(name, null);
    }

    /**
     * Get the name of the component
     * @return The name of the component
     */
    public String getName() {
        return mName;
    }

    void setName(final String name) {
        if(name != null)
            mName = name;
        else
            mName = "";
    }

    /**
     * Get a description of the component
     * @return The description of the component
     */
    public String getDescription() {
        return mDescription;
    }

    void setDescription(final String description) {
        if(description != null)
            mDescription = description;
        else
            mDescription = "";
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void setConnected(boolean connected) {
        if(!mIsAlwaysConnected) {
            mDelayDisconnectHandler.removeCallbacksAndMessages(null);
            if (connected && !mIsAlwaysConnected) {
                mDelayDisconnectHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setConnected(false);
                    }
                }, DELAY_REMOVER_MILLIS);
            }
            mIsConnected = connected;
        }
    }

    public void setAlwaysConnected() {
        mIsAlwaysConnected = true;

        mDelayDisconnectHandler.removeCallbacksAndMessages(null);
        mIsConnected = true;
    }

    abstract void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi);

    abstract void AnalyzeConnectionData(final String address, final Object property, final byte[] data, final boolean notification);

    void AnalyzeDirectInteractionMessage(final String address, final String target, final byte[] command) {

    }
}
