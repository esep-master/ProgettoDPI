/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 25/06/2019 15:09
 */

package it.bleb.blebandroid;

import java.util.Arrays;
import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLEESP extends Component {
    BLEESP() {
        super("BLE-ESP", "");
    }

    public enum WifiStatus {
        NOT_CONNECTED, WAITING_FOR_CREDENTIALS, CONNECTED
    }

    public enum MqttStatus {
        NOT_CONNECTED, CONNECTED
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnInfoReceivedListener mOnInfoReceivedListener;
        private OnPayloadReceivedListener mOnPayloadReceivedListener;

        public OnInfoReceivedListener getOnInfoReceivedListener() {
            return mOnInfoReceivedListener;
        }

        public void setOnInfoReceivedListener(OnInfoReceivedListener onInfoReceivedListener) {
            mOnInfoReceivedListener = onInfoReceivedListener;
        }

        public OnPayloadReceivedListener getOnPayloadReceivedListener() {
            return mOnPayloadReceivedListener;
        }

        public void setOnPayloadReceivedListener(OnPayloadReceivedListener onPayloadReceivedListener) {
            mOnPayloadReceivedListener = onPayloadReceivedListener;
        }

        public interface OnInfoReceivedListener {
            void OnInfoReceived(final String address, final WifiStatus wifiStatus, final MqttStatus mqttStatus);
        }

        public interface OnPayloadReceivedListener {
            void OnPayloadReceived(final String address, final byte[] payload);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x13: {
                setConnected(true);
                WifiStatus wifiStatus;
                switch ((BLEUtils.GetBit(data[0], 1) ? 2 : 0) + (BLEUtils.GetBit(data[0], 0) ? 1 : 0)) {
                    default:
                        wifiStatus = WifiStatus.NOT_CONNECTED;
                        break;
                    case 1:
                        wifiStatus = WifiStatus.CONNECTED;
                        break;
                    case 2:
                        wifiStatus = WifiStatus.WAITING_FOR_CREDENTIALS;
                        break;
                }

                MqttStatus mqttStatus;
                switch (BLEUtils.GetBit(data[0], 4) ? 1 : 0) {
                    default:
                        mqttStatus = MqttStatus.NOT_CONNECTED;
                        break;
                    case 1:
                        mqttStatus = MqttStatus.CONNECTED;
                        break;
                }

                if (ScanEvents.getOnInfoReceivedListener() != null) {
                    ScanEvents.getOnInfoReceivedListener().OnInfoReceived(address, wifiStatus, mqttStatus);
                }
            }
            break;
            case 0xe6: {
                setConnected(true);
                if (ScanEvents.getOnPayloadReceivedListener() != null) {
                    ScanEvents.getOnPayloadReceivedListener().OnPayloadReceived(address, data);
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

        public Command SetPublishInterval(final double interval) {
            Command c = new Command("EF");
            String cmdValue = "01";

            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo6553500ms(interval), 2);
            c.setValue(cmdValue);
            return c;
        }

        public Command Reconfigure() {
            Command c = new Command("EF");
            String cmdValue = "0201";
            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BBFF-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final WifiStatus wifiStatus, final MqttStatus mqttStatus, double publishInterval, String urlBroker, byte[] payload, String fw);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                WifiStatus wifiStatus;
                switch (BLEUtils.UnsignedBytesToInt(data[0])) {
                    default:
                        wifiStatus = WifiStatus.NOT_CONNECTED;
                        break;
                    case 1:
                        wifiStatus = WifiStatus.CONNECTED;
                        break;
                    case 2:
                        wifiStatus = WifiStatus.WAITING_FOR_CREDENTIALS;
                        break;
                }

                MqttStatus mqttStatus;
                switch (BLEUtils.UnsignedBytesToInt(data[1])) {
                    default:
                        mqttStatus = MqttStatus.NOT_CONNECTED;
                        break;
                    case 1:
                        mqttStatus = MqttStatus.CONNECTED;
                        break;
                }

                double publishInterval = ByteToDoubleConversions.from100msTo6553500ms(BLEUtils.UnsignedBytesToInt(data[2], data[3]));
                String urlBroker = String.format(Locale.US, "%d.%d.%d.%d", BLEUtils.UnsignedBytesToInt(data[4]), BLEUtils.UnsignedBytesToInt(data[5]), BLEUtils.UnsignedBytesToInt(data[6]), BLEUtils.UnsignedBytesToInt(data[7]));
                byte[] payload = Arrays.copyOfRange(data, 8, 8 + 9);
                String fw = String.format(Locale.US, "%.1f", BLEUtils.UnsignedBytesToInt(data[17]) / 10.);

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(
                        address,
                        wifiStatus,
                        mqttStatus,
                        publishInterval,
                        urlBroker,
                        payload,
                        fw
                );
            }
        }
    }
    //endregion
}
