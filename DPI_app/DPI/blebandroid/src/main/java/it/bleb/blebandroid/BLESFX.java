/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.EnumHexValue;

public class BLESFX extends Component {
    BLESFX() {
        super("BLE-SFX", "Sigfox Communication Module");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnForwardedDataTypeListener mOnForwardedDataTypeListener;
        private OnSfxIdListener mOnSfxIdListener;

        public OnForwardedDataTypeListener getOnForwardedDataTypeListener() {
            return mOnForwardedDataTypeListener;
        }

        public void setOnForwardedDataTypeListener(OnForwardedDataTypeListener listener) {
            mOnForwardedDataTypeListener = listener;
        }

        public OnSfxIdListener getOnSfxIdListener() {
            return mOnSfxIdListener;
        }

        public void setOnSfxIdListener(OnSfxIdListener listener) {
            mOnSfxIdListener = listener;
        }

        public interface OnForwardedDataTypeListener {
            void OnForwardedDataTypeReceived(final String address, final EnumHexValue<DirectInteraction.DirectInteractionTriggerEnum> dataType);
        }

        public interface OnSfxIdListener {
            void OnSfxIdReceived(final String address, final String id);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x0B: {
                setConnected(true);
                if (ScanEvents.getOnForwardedDataTypeListener() != null) {
                    ScanEvents.getOnForwardedDataTypeListener().OnForwardedDataTypeReceived(address, DirectInteraction.DirectInteractionTrigger.getByValue(BLEUtils.UnsignedBytesToInt(data[0])));
                }
            }
            break;
            case 0x91: {
                setConnected(true);
                if (ScanEvents.getOnSfxIdListener() != null) {
                    String id = BLEUtils.ASCIIBytesToString(data[0], data[1], data[2], data[3], data[4], data[5]);
                    ScanEvents.getOnSfxIdListener().OnSfxIdReceived(address, id);
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
         * With this settings, SFX is forwarding a 12-bytes packet composed by
         * two RFU (Reserved for Future Use) bytes set to 0xFF followed by the
         * 10bytes long advertising buffer that the BLE-B is currently using.
         * If such buffer is longer than 10 bytes, it is split into two or
         * more subsequent packets that are forwarded to the Sigfox network
         * after 3 seconds each.
         *
         * @return The command
         */
        public Command OneShotSigfoxUplinkBLEBPayload() {
            Command c = new Command("A3");

            String cmdValue = "01";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the payload to forward by the SFX
         *
         * @param payload The payload to send
         * @return The command
         */
        public Command OneShotSigfoxUplinkCustomPayload(final String payload) {
            Command c = new Command("A3");

            String cmdValue = "00";

            String sanitizedPayload = BLEUtils.BytesToHex(BLEUtils.StringToASCIIBytes("D" + payload));

            if (sanitizedPayload.length() > 24)
                sanitizedPayload = sanitizedPayload.substring(0, 24);
            if (sanitizedPayload.length() < 2)
                sanitizedPayload = "";
            if (sanitizedPayload.length() % 2 != 0)
                sanitizedPayload = sanitizedPayload.substring(0, sanitizedPayload.length() - 1);

            cmdValue += BLEUtils.IntToHex(sanitizedPayload.length() / 2, 1);
            cmdValue += sanitizedPayload;

            c.setValue(cmdValue);
            return c;
        }


        /**
         * Set forwarding interval or to disable periodic forwarding.
         *
         * @param interval The interval, in minutes (from 10 to 255 minutes). Use 0 to disable the periodic uplink
         * @return The command
         */
        public Command PeriodicSigfoxUplink(final int interval) {
            Command c = new Command("A4");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(interval, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Forward notifications based on Direct Interactions to the Sigfox network
         *
         * @param senderID used to identify the BLE-B that is forwarding the notification
         * @return The command
         */
        public Command SendNotification(final String senderID) {
            Command c = new Command("A5");

            String cmdValue = "";

            String sanitizedPayload = BLEUtils.BytesToHex(BLEUtils.StringToASCIIBytes(senderID));
            if (sanitizedPayload.length() > 10)
                sanitizedPayload = sanitizedPayload.substring(0, 10);
            while (sanitizedPayload.length() < 10)
                sanitizedPayload += "0";

            cmdValue += sanitizedPayload;

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Allows requesting the Device ID, which the BLE-B reads and sends within its advertising packets
         *
         * @param enabled true to enable the request, false otherwise
         * @return The command
         */
        public Command RequestSigfoxId(boolean enabled) {
            Command c = new Command("A6");

            String cmdValue = "";
            cmdValue += enabled ? "01" : "00";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Send GPS coordinate to the Sigfox.
         *
         * @param lat Latitude
         * @param lng Longitude
         * @return The command
         */
        public Command SendGPSCoordinate(double lat, double lng) {
            Command c = new Command("A7");

            String cmdValue = "";
            cmdValue += String.format(Locale.US, "%2.5f", lat);
            cmdValue += String.format(Locale.US, "%2.5f", lng);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {

    }
}