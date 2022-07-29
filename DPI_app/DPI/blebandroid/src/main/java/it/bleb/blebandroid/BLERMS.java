/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 25/06/2019 15:09
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.Property;

public class BLERMS extends Component {
    BLERMS() {
        super("BLE-RMS", "");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnDataListener mOnDataListener;

        public OnDataListener getOnDataListener() {
            return mOnDataListener;
        }

        public void setOnDataListener(OnDataListener onDataListener) {
            mOnDataListener = onDataListener;
        }

        public interface OnDataListener {
            void OnDataReceived(final String address, double rms);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x2a: {
                setConnected(true);
                double rms = BLEUtils.SignedBytesToInt(data[0], data[1]) / 100.0;

                if (ScanEvents.getOnDataListener() != null)
                    ScanEvents.getOnDataListener().OnDataReceived(address, rms);
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

        public Command SetConfiguration(int inputVoltageRange, int shuntFullScaleRange) {
            Command c = new Command("37");
            String cmdValue = "";

            int configuration = 0b0000000110011111;
            switch (inputVoltageRange) {
                case 32:
                    configuration = configuration | 0b0010000000000000;
                    break;
            }
            switch (shuntFullScaleRange) {
                case 80:
                    configuration = configuration | 0b0000100000000000;
                    break;
                case 160:
                    configuration = configuration | 0b0001000000000000;
                    break;
                case 320:
                    configuration = configuration | 0b0001100000000000;
                    break;
            }
            cmdValue += BLEUtils.IntToHex(configuration, 2);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB2A-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, double rms, int inputVoltageRange, int shuntFullScaleRange);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                double rms = BLEUtils.SignedBytesToInt(data[0], data[1]) / 100.0;

                int inputVoltageRange = BLEUtils.GetBit(data[2], 5) ? 32 : 16;
                int shuntFullScaleRange = BLEUtils.GetBit(data[2], 4) && BLEUtils.GetBit(data[2], 3)
                        ? 320
                        : BLEUtils.GetBit(data[8], 4)
                        ? 160
                        : BLEUtils.GetBit(data[8], 3)
                        ? 80
                        : 40;
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address, rms, inputVoltageRange, shuntFullScaleRange);
            }
        }
    }
    //endregion
}
