/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 25/06/2019 15:09
 */

package it.bleb.blebandroid;

import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLEVID extends Component {
    BLEVID() {
        super("BLE-VID", "Brick with a camera able to record videos");
    }

    public enum RecordingStatus {
        NOT_RECORDING, RECORDING, FILE_SAVED
    }

    public enum WifiStatus {
        WIFI_OFF, LOOKING_FOR_WIFI, WAITING_FOR_CREDENTIALS, CONNECTED
    }

    public enum Quality {
        POOR, REGULAR, HIGH
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnValueReceivedListener mOnValueReceived;

        public OnValueReceivedListener getOnValueReceived() {
            return mOnValueReceived;
        }

        public void setOnValueReceived(OnValueReceivedListener onValueReceived) {
            mOnValueReceived = onValueReceived;
        }

        public interface OnValueReceivedListener {
            void OnValueReceived(final String address, final RecordingStatus recordingStatus, final WifiStatus wifiStatus, final String ipServerFtp);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x95: {
                setConnected(true);
                RecordingStatus recordingStatus;
                WifiStatus wifiStatus;
                String ipServerFtp;

                switch (BLEUtils.UnsignedBytesToInt(data[0])) {
                    default:
                        recordingStatus = RecordingStatus.NOT_RECORDING;
                        break;
                    case 0x01:
                        recordingStatus = RecordingStatus.RECORDING;
                        break;
                    case 0x02:
                        recordingStatus = RecordingStatus.FILE_SAVED;
                        break;
                }

                switch (BLEUtils.UnsignedBytesToInt(data[1])) {
                    default:
                        wifiStatus = WifiStatus.WIFI_OFF;
                        break;
                    case 0x01:
                        wifiStatus = WifiStatus.LOOKING_FOR_WIFI;
                        break;
                    case 0x02:
                        wifiStatus = WifiStatus.WAITING_FOR_CREDENTIALS;
                        break;
                    case 0x03:
                        wifiStatus = WifiStatus.CONNECTED;
                        break;
                }

                ipServerFtp = String.format(Locale.US, "%d.%d.%d.%d", BLEUtils.UnsignedBytesToInt(data[2]), BLEUtils.UnsignedBytesToInt(data[3]), BLEUtils.UnsignedBytesToInt(data[4]), BLEUtils.UnsignedBytesToInt(data[5]));

                if (ScanEvents.getOnValueReceived() != null) {
                    ScanEvents.getOnValueReceived().OnValueReceived(address, recordingStatus, wifiStatus, ipServerFtp);
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

        public Command StartRecording(final int numberOfRepeats, final int playbackSpeed, final Quality quality, final int videoDuration) {
            Command c = new Command("35");
            String cmdValue = "02";

            cmdValue += BLEUtils.IntToHex(Math.max(Math.min(numberOfRepeats, 100), 0), 1);
            cmdValue += BLEUtils.IntToHex(Math.max(Math.min(playbackSpeed, 10), 1), 1);
            switch (quality) {
                case POOR:
                    cmdValue += BLEUtils.IntToHex(0x32, 1);
                    break;
                case HIGH:
                    cmdValue += BLEUtils.IntToHex(0x05, 1);
                    break;
                default:
                    cmdValue += BLEUtils.IntToHex(0x14, 1);
                    break;
            }
            cmdValue += BLEUtils.IntToHex(Math.max(Math.min((videoDuration / 10), 250), 1), 1);

            c.setValue(cmdValue);
            return c;
        }

        public Command StopRecording() {
            Command c = new Command("35");
            String cmdValue = "03";
            c.setValue(cmdValue);
            return c;
        }

        public Command StartFtpServer() {
            Command c = new Command("35");
            String cmdValue = "01";
            c.setValue(cmdValue);
            return c;
        }

        public Command DeepSleep() {
            Command c = new Command("35");
            String cmdValue = "D5";
            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB95-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final int numberOfRepeats, final int playbackSpeed, final Quality quality, final int videoDuration, final RecordingStatus recordingStatus, final WifiStatus wifiStatus, final String ipServerFtp, final boolean isDeepSleeping);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Quality quality;
        switch (BLEUtils.UnsignedBytesToInt(data[2])) {
            case 0x05:
                quality = Quality.HIGH;
                break;
            case 0x32:
                quality = Quality.POOR;
                break;
            default:
                quality = Quality.REGULAR;
                break;
        }

        RecordingStatus recordingStatus;
        switch (BLEUtils.UnsignedBytesToInt(data[4])) {
            default:
                recordingStatus = RecordingStatus.NOT_RECORDING;
                break;
            case 0x01:
                recordingStatus = RecordingStatus.RECORDING;
                break;
            case 0x02:
                recordingStatus = RecordingStatus.FILE_SAVED;
                break;
        }

        WifiStatus wifiStatus;
        switch (BLEUtils.UnsignedBytesToInt(data[5])) {
            default:
                wifiStatus = WifiStatus.WIFI_OFF;
                break;
            case 0x01:
                wifiStatus = WifiStatus.LOOKING_FOR_WIFI;
                break;
            case 0x02:
                wifiStatus = WifiStatus.WAITING_FOR_CREDENTIALS;
                break;
            case 0x03:
                wifiStatus = WifiStatus.CONNECTED;
                break;
        }

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(
                        address,
                        BLEUtils.UnsignedBytesToInt(data[0]),
                        BLEUtils.UnsignedBytesToInt(data[1]),
                        quality,
                        BLEUtils.UnsignedBytesToInt(data[3]) * 10,
                        recordingStatus,
                        wifiStatus,
                        String.format(Locale.US, "%d.%d.%d.%d", BLEUtils.UnsignedBytesToInt(data[6]), BLEUtils.UnsignedBytesToInt(data[7]), BLEUtils.UnsignedBytesToInt(data[8]), BLEUtils.UnsignedBytesToInt(data[9])),
                        BLEUtils.UnsignedBytesToInt(data[10]) == 0x00
                        );
            }
        }
    }
    //endregion
}
