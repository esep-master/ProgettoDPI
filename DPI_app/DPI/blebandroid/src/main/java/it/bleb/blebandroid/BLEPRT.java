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

public class BLEPRT extends Component {
    BLEPRT() {
        super("BLE-PRT", "Absolute pressure and temperature sensor");
    }

    public enum Oversampling {
        SKIP_READ, OVERSAMPLING_1, OVERSAMPLING_2, OVERSAMPLING_4, OVERSAMPLING_8, OVERSAMPLING_16, UNCHANGED
    }

    public enum FilterCoefficient {
        SKIP_READ, COEFFICIENT_2, COEFFICIENT_4, COEFFICIENT_8, COEFFICIENT_16, UNCHANGED
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnTemperatureAndAbsolutePressureListener mOnTemperatureAndAbsolutePressureListener;

        public OnTemperatureAndAbsolutePressureListener getOnTemperatureAndAbsolutePressureListener() {
            return mOnTemperatureAndAbsolutePressureListener;
        }

        public void setOnTemperatureAndAbsolutePressureListener(OnTemperatureAndAbsolutePressureListener onTemperatureAndAbsolutePressureListener) {
            mOnTemperatureAndAbsolutePressureListener = onTemperatureAndAbsolutePressureListener;
        }

        public interface OnTemperatureAndAbsolutePressureListener {
            void OnTemperatureAndAbsolutePressureReceived(final String address, final double temperature, final int pressure);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x71: {
                setConnected(true);
                if (ScanEvents.getOnTemperatureAndAbsolutePressureListener() != null)
                    ScanEvents.getOnTemperatureAndAbsolutePressureListener().OnTemperatureAndAbsolutePressureReceived(address, BLEUtils.SignedBytesToInt(data[0], data[1]) / 100d, BLEUtils.UnsignedBytesToInt(data[2], data[3], data[4]));
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
         * Set the configuration of the PRT brick
         * @param readingInterval from 0.01s to 1036800s
         * @param temperatureOversampling
         * @param pressureOversampling
         * @param filterCoefficient
         * @param temperatureOffset from -128°C to 127°C, use 127°C for UNCHANGED
         * @param pressureOffset from -128 hPa to 127 hPa, use 127 hPa for UNCHANGED
         * @return The command
         */
        public Command SetConfiguration(final double readingInterval, final Oversampling temperatureOversampling, final Oversampling pressureOversampling, final FilterCoefficient filterCoefficient, final int temperatureOffset, final int pressureOffset) {
            Command c = new Command("0C");

            String cmdValue = "";

            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo12days(readingInterval), 1);

            switch (temperatureOversampling) {
                default:
                    cmdValue += "FF";
                    break;
                case SKIP_READ:
                    cmdValue += "00";
                    break;
                case OVERSAMPLING_1:
                    cmdValue += "01";
                    break;
                case OVERSAMPLING_2:
                    cmdValue += "02";
                    break;
                case OVERSAMPLING_4:
                    cmdValue += "03";
                    break;
                case OVERSAMPLING_8:
                    cmdValue += "04";
                    break;
                case OVERSAMPLING_16:
                    cmdValue += "05";
                    break;
            }

            switch (pressureOversampling) {
                default:
                    cmdValue += "FF";
                    break;
                case SKIP_READ:
                    cmdValue += "00";
                    break;
                case OVERSAMPLING_1:
                    cmdValue += "01";
                    break;
                case OVERSAMPLING_2:
                    cmdValue += "02";
                    break;
                case OVERSAMPLING_4:
                    cmdValue += "03";
                    break;
                case OVERSAMPLING_8:
                    cmdValue += "04";
                    break;
                case OVERSAMPLING_16:
                    cmdValue += "05";
                    break;
            }

            switch (filterCoefficient) {
                default:
                    cmdValue += "FF";
                    break;
                case SKIP_READ:
                    cmdValue += "00";
                    break;
                case COEFFICIENT_2:
                    cmdValue += "01";
                    break;
                case COEFFICIENT_4:
                    cmdValue += "02";
                    break;
                case COEFFICIENT_8:
                    cmdValue += "03";
                    break;
                case COEFFICIENT_16:
                    cmdValue += "04";
                    break;
            }

            cmdValue += BLEUtils.IntToHex(temperatureOffset, 1);
            cmdValue += BLEUtils.IntToHex(pressureOffset, 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB71-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final double temperature, final int pressure, final double readingInterval, final Oversampling temperatureOversampling, final Oversampling pressureOversampling, final FilterCoefficient filterCoefficient, final int temperatureOffset, final int pressureOffset);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {

                Oversampling tempOver;
                switch (data[6]) {
                    default:
                        tempOver = Oversampling.SKIP_READ;
                        break;
                    case 1:
                        tempOver = Oversampling.OVERSAMPLING_1;
                        break;
                    case 2:
                        tempOver = Oversampling.OVERSAMPLING_2;
                        break;
                    case 3:
                        tempOver = Oversampling.OVERSAMPLING_4;
                        break;
                    case 4:
                        tempOver = Oversampling.OVERSAMPLING_8;
                        break;
                    case 5:
                        tempOver = Oversampling.OVERSAMPLING_16;
                        break;
                }

                Oversampling pressOver;
                switch (data[7]) {
                    default:
                        pressOver = Oversampling.SKIP_READ;
                        break;
                    case 1:
                        pressOver = Oversampling.OVERSAMPLING_1;
                        break;
                    case 2:
                        pressOver = Oversampling.OVERSAMPLING_2;
                        break;
                    case 3:
                        pressOver = Oversampling.OVERSAMPLING_4;
                        break;
                    case 4:
                        pressOver = Oversampling.OVERSAMPLING_8;
                        break;
                    case 5:
                        pressOver = Oversampling.OVERSAMPLING_16;
                        break;
                }

                FilterCoefficient coeff;
                switch (data[8]) {
                    default:
                        coeff = FilterCoefficient.SKIP_READ;
                        break;
                    case 1:
                        coeff = FilterCoefficient.COEFFICIENT_2;
                        break;
                    case 2:
                        coeff = FilterCoefficient.COEFFICIENT_4;
                        break;
                    case 3:
                        coeff = FilterCoefficient.COEFFICIENT_8;
                        break;
                    case 4:
                        coeff = FilterCoefficient.COEFFICIENT_16;
                        break;
                }

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address,
                        BLEUtils.SignedBytesToInt(data[0], data[1]) / 100d,
                        BLEUtils.UnsignedBytesToInt(data[2], data[3], data[4]),
                        ByteToDoubleConversions.from10msTo12days(BLEUtils.UnsignedBytesToInt(data[5])),
                        tempOver,
                        pressOver,
                        coeff,
                        BLEUtils.SignedBytesToInt(data[9]),
                        BLEUtils.SignedBytesToInt(data[10]));
            }
        }
    }
    //endregion
}
