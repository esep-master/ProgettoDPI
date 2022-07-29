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

public class BLECMS extends Component {
    BLECMS() {
        super("BLE-CMS", "");
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
            void OnDataReceived(final String address, int multiplier, int voltage, int current, int power);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xaa: {
                setConnected(true);
                int multiplier = (int) Math.round(Math.pow(10, BLEUtils.UnsignedBytesToInt(data[0]) - 1));
                int voltage = BLEUtils.SignedBytesToInt(data[1], data[2]) * 10;
                int current = BLEUtils.SignedBytesToInt(data[3], data[4]);
                int power = BLEUtils.SignedBytesToInt(data[5], data[6]);

                if (ScanEvents.getOnDataListener() != null)
                    ScanEvents.getOnDataListener().OnDataReceived(address, multiplier, voltage, current, power);
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

        public Command SetConfiguration(final double sampling, int inputVoltageRange, int shuntFullScaleRange, int shuntResistance, String shuntResistanceMu, int multiplier) {
            Command c = new Command("39");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(sampling), 1);

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

            cmdValue += BLEUtils.IntToHex(shuntResistance, 2);

            switch (shuntResistanceMu) {
                case "Ω":
                    cmdValue += BLEUtils.IntToHex(2, 1);
                    break;
                default:
                    cmdValue += BLEUtils.IntToHex(1, 1);
                    break;
            }
            switch (multiplier) {
                case 10:
                    cmdValue += BLEUtils.IntToHex(2, 1);
                    break;
                case 100:
                    cmdValue += BLEUtils.IntToHex(3, 1);
                    break;
                case 1000:
                    cmdValue += BLEUtils.IntToHex(4, 1);
                    break;
                default:
                    cmdValue += BLEUtils.IntToHex(1, 1);
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BBAA-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, int multiplier, int voltage, int current, int power, double sampling, int inputVoltageRange, int shuntFullScaleRange, int shuntResistance, String shuntResistanceMu);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                int multiplier = (int) Math.round(Math.pow(10, BLEUtils.UnsignedBytesToInt(data[0]) - 1));
                int voltage = BLEUtils.SignedBytesToInt(data[1], data[2]);
                int current = BLEUtils.SignedBytesToInt(data[3], data[4]);
                int power = BLEUtils.SignedBytesToInt(data[5], data[6]);
                double sampling = ByteToDoubleConversions.from10msTo2550ms(BLEUtils.UnsignedBytesToInt(data[7]));

                int inputVoltageRange = BLEUtils.GetBit(data[8], 5) ? 32 : 16;
                int shuntFullScaleRange = BLEUtils.GetBit(data[8], 4) && BLEUtils.GetBit(data[8], 3)
                        ? 320
                        : BLEUtils.GetBit(data[8], 4)
                        ? 160
                        : BLEUtils.GetBit(data[8], 3)
                        ? 80
                        : 40;
                int shuntResistance = BLEUtils.UnsignedBytesToInt(data[10], data[11]);
                String shuntResistanceMu = BLEUtils.UnsignedBytesToInt(data[12]) == 1 ? "mΩ" : "Ω";

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address, multiplier, voltage, current, power, sampling, inputVoltageRange, shuntFullScaleRange, shuntResistance, shuntResistanceMu);
            }
        }
    }
    //endregion
}
