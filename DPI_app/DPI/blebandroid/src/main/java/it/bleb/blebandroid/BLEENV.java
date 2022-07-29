/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.Property;

public class BLEENV extends Component {
    BLEENV() {
        super("BLE-ENV", "Gas, humidity, pressure and temperature sensor with Indoor Air Quality value");
    }


    public enum PowerMode {
        LowPower, UltraLowPower
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnEnvironmentDataListener mOnEnvironmentDataListener;

        public OnEnvironmentDataListener getOnEnvironmentDataListener() {
            return mOnEnvironmentDataListener;
        }

        public void setOnEnvironmentDataListener(OnEnvironmentDataListener onEnvironmentDataListener) {
            mOnEnvironmentDataListener = onEnvironmentDataListener;
        }

        public interface OnEnvironmentDataListener {
            void OnEnvironmentDataReceived(final String address, final int temperature, final int pressure, final int humidity, final int voc, final int indoorAirQuality, final boolean indoorAirQualityIsReliable, final int indoorAirQualityAccuracy);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xC8: {
                setConnected(true);
                if (ScanEvents.getOnEnvironmentDataListener() != null)
                    ScanEvents.getOnEnvironmentDataListener().OnEnvironmentDataReceived(address, BLEUtils.SignedBytesToInt(data[0]), BLEUtils.UnsignedBytesToInt(data[1], data[2], data[3]), BLEUtils.UnsignedBytesToInt(data[4]), BLEUtils.UnsignedBytesToInt(data[5]) * 2, BLEUtils.UnsignedBytesToInt(data[6]) * 2, BLEUtils.UnsignedBytesToInt(data[7]) > 0, BLEUtils.UnsignedBytesToInt(data[7]));
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
         * Set the configuration of the ENV module. ONLY IN CONNECTION! WARNING: This command will cause the device to restart (without memory loss).
         * @param powerMode
         * @param temperatureOffset The temperature offset (from -127°C to 128°C). WARNING: The temperature value is affecting the whole behaviour of the sensor (pressure, humidity and IAQ).
         * @return The command
         */
        public Command SetConfiguration(final PowerMode powerMode, final int temperatureOffset) {
            Command c = new Command("0F");

            String cmdValue = "";

            switch (powerMode) {
                case LowPower:
                    cmdValue += "00";
                    break;
                case UltraLowPower:
                    cmdValue += "01";
                    break;
            }

            cmdValue += BLEUtils.IntToHex(temperatureOffset, 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion


    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BBA9-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final int temperature, final int pressure, final int humidity, final int indoorAirQuality, final boolean indoorAirQualityIsReliable, final int indoorAirQualityAccuracy, final int temperatureOffset, final PowerMode powerMode);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            PowerMode powerMode;
            switch(BLEUtils.UnsignedBytesToInt(data[8])) {
                case 0x00:
                    powerMode = PowerMode.LowPower;
                    break;
                case 0x01:
                    powerMode = PowerMode.UltraLowPower;
                    break;
                default:
                    powerMode = PowerMode.LowPower;
            }

            if (ConnectionEvents.getOnMainValuesListener() != null)
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address,
                        BLEUtils.SignedBytesToInt(data[0]),
                        BLEUtils.UnsignedBytesToInt(data[1], data[2], data[3]),
                        BLEUtils.UnsignedBytesToInt(data[4]),
                        BLEUtils.UnsignedBytesToInt(data[5]) * 2,
                        BLEUtils.UnsignedBytesToInt(data[6]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[6]),
                        BLEUtils.SignedBytesToInt(data[7]),
                        powerMode);

        }
    }
    //endregion
}