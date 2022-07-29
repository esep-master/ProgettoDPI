/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLERHT extends Component {
    BLERHT() {
        super("BLE-RHT", "Relative humidity and temperature sensor");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnRelativeHumidityAndTemperatureListener mOnRelativeHumidityAndTemperatureListener;

        public OnRelativeHumidityAndTemperatureListener getOnRelativeHumidityAndTemperatureListener() {
            return mOnRelativeHumidityAndTemperatureListener;
        }

        public void setOnRelativeHumidityAndTemperatureListener(OnRelativeHumidityAndTemperatureListener onRelativeHumidityAndTemperatureListener) {
            mOnRelativeHumidityAndTemperatureListener = onRelativeHumidityAndTemperatureListener;
        }

        public interface OnRelativeHumidityAndTemperatureListener {
            void OnRelativeHumidityAndTemperatureReceived(final String address, final double relativeHumidity, final double temperature);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x55: {
                setConnected(true);
                if (ScanEvents.getOnRelativeHumidityAndTemperatureListener() != null)
                    ScanEvents.getOnRelativeHumidityAndTemperatureListener().OnRelativeHumidityAndTemperatureReceived(address, BLEUtils.UnsignedBytesToInt(data[0], data[1]) /100d, BLEUtils.SignedBytesToInt(data[2], data[3]) / 100d);
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
         * Set the configuration of the RHT module
         * @param readingInterval The reading interval (from 0.5s to 1036800s)
         * @param temperatureOffset The temperature offset (from -127°C to 128°C, use 127°C for UNCHANGED)
         * @param humidityOffset The relative humidity offset (from -127% to 128%, use 127% for UNCHANGED)
         * @return The command
         */
        public Command SetConfiguration(final double readingInterval, int temperatureOffset, int humidityOffset) {
            Command c = new Command("12");

            String cmdValue = "";


            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from500msTo12days(readingInterval), 1);
            cmdValue += BLEUtils.IntToHex(temperatureOffset, 1);
            cmdValue += BLEUtils.IntToHex(humidityOffset, 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB55-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final int relativeHumidity, final double temperature, final double readingInterval, final int temperatureOffset, final int humidityOffset);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address,
                        BLEUtils.UnsignedBytesToInt(data[0], data[1]),
                        BLEUtils.SignedBytesToInt(data[2], data[3]) / 100d,
                        ByteToDoubleConversions.from500msTo12days(BLEUtils.UnsignedBytesToInt(data[4])),
                        BLEUtils.SignedBytesToInt(data[5]),
                        BLEUtils.SignedBytesToInt(data[6]));
            }
        }
    }
    //endregion
}
