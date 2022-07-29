/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 29/05/2019 14:01
 */

package it.bleb.blebandroid;

import java.util.Arrays;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;

public class SentryMode extends Component {
    private boolean mIsSentryMode = false;

    public boolean isSentryMode() {
        return mIsSentryMode;
    }

    @Override
    public void setConnected(boolean connected) {
        super.setConnected(connected);
        if(!connected)
            mIsSentryMode = false;
    }

    SentryMode() {
        super("SentryMode", "Check the distance between two different BLE-Bs");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnWaitingForAddressListener mOnWaitingForAddressListener;
        private OnSentryValueListener mOnSentryValueListener;
        private OnSentryDeviceLostListener mOnSentryDeviceLostListener;

        public OnWaitingForAddressListener getOnWaitingForAddressListener() {
            return mOnWaitingForAddressListener;
        }

        public void setOnWaitingForAddressListener(OnWaitingForAddressListener onWaitingForAddressListener) {
            mOnWaitingForAddressListener = onWaitingForAddressListener;
        }

        public OnSentryValueListener getOnSentryValueListener() {
            return mOnSentryValueListener;
        }

        public void setOnSentryValueListener(OnSentryValueListener onSentryValueListener) {
            mOnSentryValueListener = onSentryValueListener;
        }

        public OnSentryDeviceLostListener getOnSentryDeviceLostListener() {
            return mOnSentryDeviceLostListener;
        }

        public void setOnSentryDeviceLostListener(OnSentryDeviceLostListener onSentryDeviceLostListener) {
            mOnSentryDeviceLostListener = onSentryDeviceLostListener;
        }

        public interface OnWaitingForAddressListener {
            void OnWaitingForAddressReceived(final String address, int interval);
        }

        public interface OnSentryValueListener {
            void OnSentryValueReceived(final String address, final String targetAddress, int rssi, int interval);
        }

        public interface OnSentryDeviceLostListener {
            void OnSentryDeviceLostReceived(final String address, final String targetAddress, int interval);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xC7: {
                setConnected(true);
                mIsSentryMode = true;

                String targetAddress = BLEUtils.BytesToMacAddress(Arrays.copyOfRange(data, 0, 6));
                int detectedRssi = BLEUtils.SignedBytesToInt(data[6]);
                int interval = BLEUtils.UnsignedBytesToInt(data[7]);

                if(targetAddress.equals("FF:FF:FF:FF:FF:FF")) {
                    if (ScanEvents.getOnWaitingForAddressListener() != null)
                        ScanEvents.getOnWaitingForAddressListener().OnWaitingForAddressReceived(address, interval);
                } else {
                    if(detectedRssi > -128) {
                        if (ScanEvents.getOnSentryValueListener() != null)
                            ScanEvents.getOnSentryValueListener().OnSentryValueReceived(address, targetAddress, detectedRssi, interval);
                    } else {
                        if (ScanEvents.getOnSentryDeviceLostListener() != null)
                            ScanEvents.getOnSentryDeviceLostListener().OnSentryDeviceLostReceived(address, targetAddress, interval);
                    }
                }
            }
            break;
        }
    }
    //endregion

    //region Commands
    public final CommandsContainer Commands = new CommandsContainer();

    public static class CommandsContainer {
        private CommandsContainer() {
        }

        /**
         * Enable or Disable the Sentry Mode
         * @param enabled True if you want to enable it, false otherwise
         * @param interval In seconds, from 1s to 255s (0 for "unchanged")
         * @return The command
         */
        public Command SetSentryMode(final boolean enabled, final int interval) {
            Command c = new Command("AD");

            String cmdValue = "";
            if(enabled)
                cmdValue += "FFFFFFFFFFFF";
            else
                cmdValue += "000000000000";
            cmdValue += BLEUtils.IntToHex(interval, 1);
            c.setValue(cmdValue);
            return c;
        }

        /**
         * Enable the Sentry Mode and set the listened address.
         * @param address The mac address (in hex "00:00:00:00:00:00" format)
         * @return The command
         */
        public Command SetSentryModeAddress(final String address, final int interval) {
            Command c = new Command("AD");

            String cmdValue = "";
            cmdValue += BLEUtils.MacAddressRemoveColons(address);
            cmdValue += BLEUtils.IntToHex(interval, 1);
            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {

    }
}
