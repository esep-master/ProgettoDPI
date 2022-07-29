/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 25/06/2019 15:09
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLEIRT extends Component {
    BLEIRT() {
        super("BLE-IRT", "Infrared transmitter");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnMessageListener mOnMessageListener;

        public OnMessageListener getOnMessageListener() {
            return mOnMessageListener;
        }

        public void setOnMessageListener(OnMessageListener onMessageListener) {
            mOnMessageListener = onMessageListener;
        }

        public interface OnMessageListener {
            void OnMessageReceived(final String address, final String messageHex, final String messageAscii);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x93: {
                setConnected(true);
                String messageHex = BLEUtils.BytesToHex(data[0], data[1], data[2], data[3], data[4], data[5]);
                String messageAscii = BLEUtils.ASCIIBytesToString(data[0], data[1], data[2], data[3], data[4], data[5]);
                if (ScanEvents.getOnMessageListener() != null)
                    ScanEvents.getOnMessageListener().OnMessageReceived(address, messageHex, messageAscii);
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
         * Set the power of the transmission
         * @param txPower the power, from 67 (min power) to 52 (max power)
         * @return The command
         */
        public Command SetTransmissionPower(final int txPower) {
            Command c = new Command("21");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(txPower, 1);
            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the message
         * @param hex The message
         * @return The command
         */
        public Command SetMessageHex(final String hex) {
            Command c = new Command("22");
            String cmdValue = "";

            String sanitizedHex = BLEUtils.SanitizeHex(hex);
            while(sanitizedHex.length() < 12)
                sanitizedHex += "0";
            if(sanitizedHex.length() > 12)
                sanitizedHex = sanitizedHex.substring(0, 12);

            cmdValue += sanitizedHex;
            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the message in ASCII format
         * @param message The message
         * @return The command
         */
        public Command SetMessageAscii(final String message) {
            Command c = new Command("22");
            String cmdValue = "";

            String sanitizedMessage = message;
            if(sanitizedMessage.length() > 6)
                sanitizedMessage = sanitizedMessage.substring(0, 6);
            String sanitizedHex = BLEUtils.BytesToHex(BLEUtils.StringToASCIIBytes(sanitizedMessage));
            while(sanitizedHex.length() < 12)
                sanitizedHex += "0";
            if(sanitizedHex.length() > 12)
                sanitizedHex = sanitizedHex.substring(0, 12);

            cmdValue += sanitizedHex;
            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the transmission interval
         * @param interval the interval, in seconds (from 0.1s to 25s)
         * @return The command
         */
        public Command SetTransmissionInterval(final double interval) {
            Command c = new Command("23");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo25s(interval), 1);
            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB93-30B8-4A6B-913E-0EF628448151");

        public Property getMainValues() {
            return mMainValues;
        }
    }
    //endregion

    //region ConnectionEvents read
    public final ConnectionEvents ConnectionEvents = new ConnectionEvents();

    public static class ConnectionEvents {
        private ConnectionEvents() {
        }

        private OnMainValuesListener mOnMainValuesListener;

        public OnMainValuesListener getOnMainValuesListener() {
            return mOnMainValuesListener;
        }

        public void setOnMainValuesListener(OnMainValuesListener onMainValuesListener) {
            mOnMainValuesListener = onMainValuesListener;
        }

        public interface OnMainValuesListener {
            void OnMainValuesReceived(final String address, final String messageHex, final String messageAscii, final int txPower, final double interval);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(
                        address,
                        BLEUtils.BytesToHex(data[0], data[1], data[2], data[3], data[4], data[5]),
                        BLEUtils.ASCIIBytesToString(data[0], data[1], data[2], data[3], data[4], data[5]),
                        BLEUtils.UnsignedBytesToInt(data[6]),
                        ByteToDoubleConversions.from100msTo25s(data[7])
                        );
            }
        }
    }
    //endregion
}
