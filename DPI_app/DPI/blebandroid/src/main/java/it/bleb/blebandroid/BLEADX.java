/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2020. All rights reserved.
 * https://bleb.it
 *
 * Last modified 25/06/2019 17:36
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLEADX extends Component {
    BLEADX() {
        super("BLE-ADX", "");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnNotRegisteringListener mOnNotRegisteringListener;
        private OnModListener mOnModListener;
        private OnValuesListener mOnValuesListener;

        public OnModListener getOnModListener() {
            return mOnModListener;
        }

        public void setOnModListener(OnModListener onModListener) {
            mOnModListener = onModListener;
        }

        public OnNotRegisteringListener getOnNotRegisteringListener() {
            return mOnNotRegisteringListener;
        }

        public void setOnNotRegisteringListener(OnNotRegisteringListener onNotRegisteringListener) {
            mOnNotRegisteringListener = onNotRegisteringListener;
        }

        public OnValuesListener getOnValuesListener() {
            return mOnValuesListener;
        }

        public void setOnValuesListener(OnValuesListener onValuesListener) {
            mOnValuesListener = onValuesListener;
        }

        public interface OnModListener {
            void OnModReceived(final String address, double mod, String mu, String measure, String stat);
        }

        public interface OnValuesListener {
            void OnValuesReceived(final String address, double x, double y, double z, String mu, String measure, String stat);
        }

        public interface OnNotRegisteringListener {
            void OnNotRegisteringReceived(final String address);
        }


    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xAB: {
                setConnected(true);

                if (ScanEvents.getOnNotRegisteringListener() != null && BLEUtils.UnsignedBytesToInt(data[0]) == 0 && BLEUtils.UnsignedBytesToInt(data[1], data[2]) == 0 && BLEUtils.UnsignedBytesToInt(data[3], data[4]) == 0 && BLEUtils.UnsignedBytesToInt(data[5], data[6]) == 0) {
                    ScanEvents.getOnNotRegisteringListener().OnNotRegisteringReceived(address);
                    return;
                }

                boolean s1 = BLEUtils.GetBit(data[0], 4);
                boolean s2 = BLEUtils.GetBit(data[0], 3);
                boolean s3 = BLEUtils.GetBit(data[0], 2);
                String stats = (s1 ? "1" : "0") + (s2 ? "1" : "0") + (s3 ? "1" : "0");
                boolean velocita = BLEUtils.GetBit(data[0], 1);
                boolean modulo = BLEUtils.GetBit(data[0], 0);

                double multiplier;
                String mu;
                String measureStr;
                if (velocita) {
                    multiplier = 18.62 * 0.001;
                    mu = "m/s";
                    measureStr = "Velocity";
                } else {
                    multiplier = 1.907 * 0.001;
                    mu = "g";
                    measureStr = "Acceleration";
                }

                if (stats.equals("111") && !modulo) {
                    measureStr = "Raw " + measureStr;
                } else if (modulo) {
                    measureStr = measureStr + " RSS";
                }

                String statsStr = "No statistics";
                switch (stats) {
                    case "000":
                        statsStr = "Mean value";
                        break;
                    case "001":
                        statsStr = "Standard deviation";
                        break;
                    case "010":
                        statsStr = "Peak value";
                        break;
                    case "011":
                        statsStr = "Peak to peak value";
                        break;
                    case "100":
                        statsStr = "Crest factor";
                        mu = "";
                        break;
                    case "101":
                        statsStr = "Kurtosis";
                        mu = "";
                        break;
                    case "110":
                        statsStr = "Skewness";
                        mu = "";
                        break;
                }


                if (modulo) {
                    double mod = 0;
                    switch (stats) {
                        case "111":
                        case "000":
                        case "001":
                        case "010":
                        case "011":
                            mod = BLEUtils.UnsignedBytesToInt(data[5], data[6]) * multiplier;
                            break;
                        case "100":
                        case "101":
                        case "110":
                            mod = BLEUtils.UnsignedBytesToInt(data[5]) + BLEUtils.UnsignedBytesToInt(data[6]) / 256.;
                            break;
                    }
                    if (ScanEvents.getOnModListener() != null)
                        ScanEvents.getOnModListener().OnModReceived(address, mod, mu, measureStr, statsStr);
                } else {
                    double x = 0, y = 0, z = 0;
                    switch (stats) {
                        case "111":
                        case "000":
                        case "001":
                        case "010":
                        case "011":
                            x = BLEUtils.SignedBytesToInt(data[1], data[2]) * multiplier;
                            y = BLEUtils.SignedBytesToInt(data[3], data[4]) * multiplier;
                            z = BLEUtils.SignedBytesToInt(data[5], data[6]) * multiplier;
                            break;
                        case "100":
                        case "101":
                        case "110":
                            x = BLEUtils.UnsignedBytesToInt(data[1]) + BLEUtils.UnsignedBytesToInt(data[2]) / 256.;
                            y = BLEUtils.UnsignedBytesToInt(data[3]) + BLEUtils.UnsignedBytesToInt(data[4]) / 256.;
                            z = BLEUtils.UnsignedBytesToInt(data[5]) + BLEUtils.UnsignedBytesToInt(data[6]) / 256.;
                            break;
                    }
                    if (ScanEvents.getOnValuesListener() != null)
                        ScanEvents.getOnValuesListener().OnValuesReceived(address, x, y, z, mu, measureStr, statsStr);
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

        public Command RemoveBias() {
            return new Command("44");
        }
        public Command Stop() {
            Command c = new Command("43");
            c.setValue("0000000000");
            return c;
        }

        public Command Start(boolean s1, boolean s2, boolean s3, boolean vel, boolean mod, double interval) {
            Command c = new Command("43");
            String cmdValue = "";
            cmdValue += "01";
            cmdValue += "02";

            int value = 0;
            value += s1 ? 0b00010000 : 0;
            value += s2 ? 0b00001000 : 0;
            value += s3 ? 0b00000100 : 0;
            value += vel ? 0b00000010 : 0;
            value += mod ? 0b00000001 : 0;

            cmdValue += BLEUtils.IntToHex(value, 1);
            cmdValue += "00";
            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(interval), 1);

            c.setValue(cmdValue);
            return c;
        }

    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BBAB-30B8-4A6B-913E-0EF628448151");

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


    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
//            if (ConnectionEvents.getOnMainValuesListener() != null) {
//
//            }
        }
    }
    //endregion
}
