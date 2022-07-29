/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2020. All rights reserved.
 * https://bleb.it
 *
 * Last modified 25/06/2019 17:36
 */

package it.bleb.blebandroid;

import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLECLT extends Component {
    BLECLT() {
        super("BLE-CLT", "");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnTemperatureListener mOnTemperatureListener;

        public OnTemperatureListener getOnTemperatureListener() {
            return mOnTemperatureListener;
        }

        public void setOnTemperatureListener(OnTemperatureListener onTemperatureListener) {
            mOnTemperatureListener = onTemperatureListener;
        }

        public interface OnTemperatureListener {
            void OnTemperatureReceived(final String address, final double ambientTemperature, final double objectTemperature, final boolean humanMode);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x72: {
                setConnected(true);

                double ambientTemperature = BLEUtils.UnsignedBytesToInt(data[0], data[1]) / 100.0;
                double objectTemperature = BLEUtils.UnsignedBytesToInt(data[2], data[3]) / 100.0;
                boolean humanMode = BLEUtils.UnsignedBytesToInt(data[4]) == 1;

                if (ScanEvents.getOnTemperatureListener() != null)
                    ScanEvents.getOnTemperatureListener().OnTemperatureReceived(address, ambientTemperature, objectTemperature, humanMode);
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

        public Command SetConfiguration(final double samplingInterval, final Double ambientTemperatureOffset, final Double objectTemperatureOffset, final boolean humanMode) {
            Command c = new Command("42");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo25s(samplingInterval), 1);
            cmdValue += BLEUtils.IntToHex((int)(ambientTemperatureOffset * 10), 1);
            cmdValue += BLEUtils.IntToHex((int)(objectTemperatureOffset * 10), 1);
            cmdValue += humanMode ? "01" : "00";
            c.setValue(cmdValue);
            return c;
        }

    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB72-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final double ambientTemperature, final double objectTemperature, final Double ambientTemperatureOffset, final Double objectTemperatureOffset, final boolean humanMode, final double samplingInterval);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                double ambientTemperature = BLEUtils.UnsignedBytesToInt(data[0], data[1]) / 100.0;
                double objectTemperature = BLEUtils.UnsignedBytesToInt(data[2], data[3]) / 100.0;
                double ambientTemperatureOffset = BLEUtils.UnsignedBytesToInt(data[4]) / 10.0;
                double objectTemperatureOffset = BLEUtils.UnsignedBytesToInt(data[5]) / 10.0;
                boolean humanMode = BLEUtils.UnsignedBytesToInt(data[6]) == 1;
                double samplingInterval = ByteToDoubleConversions.from100msTo25s(BLEUtils.UnsignedBytesToInt(data[7]));
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address,
                        ambientTemperature,
                        objectTemperature,
                        ambientTemperatureOffset,
                        objectTemperatureOffset,
                        humanMode,
                        samplingInterval
                );
            }
        }
    }
    //endregion
}
