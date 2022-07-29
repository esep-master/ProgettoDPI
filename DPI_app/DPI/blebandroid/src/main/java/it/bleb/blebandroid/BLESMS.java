/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLESMS extends Component {
    BLESMS() {
        super("BLE-SMS", "Soil Moisture Sensor");
    }

    private OperationMode mLastOperationModeReceived;

    public OperationMode getLastOperationModeReceived() {
        return mLastOperationModeReceived;
    }

    public enum OperationMode {
        RESISTANCE, MUTUAL_CAPACITANCE, SELF_CAPACITANCE
    }

    public enum CalibrationPoint {
        OFFSET, FULL_SCALE
    }

    public enum SensorGain {
        GAIN_1, GAIN_2, GAIN_4, GAIN_8, GAIN_16, GAIN_32
    }

    public enum SensorOversampling {
        DISABLED, OVERSAMPLING_2, OVERSAMPLING_4, OVERSAMPLING_8, OVERSAMPLING_16, OVERSAMPLING_32, OVERSAMPLING_64, OVERSAMPLING_128
    }

    public enum SensorFilterLevel {
        FILTER_1, FILTER_2, FILTER_4, FILTER_8, FILTER_16, FILTER_32, FILTER_64
    }

    public enum SensorPTCClockPrescaler {
        MHZ_4, MHZ_2, MHZ_1, KHZ_500
    }

    public enum SensorInternalResistance {
        KOHM_0, KOHM_20, KOHM_50, KOHM_100
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnResistiveSoilMoistureListener mOnResistiveSoilMoistureListener;
        private OnMutualCapacitanceListener mOnMutualCapacitanceListener;
        private OnSelfCapacitanceListener mOnSelfCapacitanceListener;

        public OnResistiveSoilMoistureListener getOnResistiveSoilMoistureListener() {
            return mOnResistiveSoilMoistureListener;
        }

        public void setOnResistiveSoilMoistureListener(OnResistiveSoilMoistureListener listener) {
            mOnResistiveSoilMoistureListener = listener;
        }

        public OnMutualCapacitanceListener getOnMutualCapacitanceListener() {
            return mOnMutualCapacitanceListener;
        }

        public void setOnMutualCapacitanceListener(OnMutualCapacitanceListener listener) {
            mOnMutualCapacitanceListener = listener;
        }

        public OnSelfCapacitanceListener getOnSelfCapacitanceListener() {
            return mOnSelfCapacitanceListener;
        }

        public void setOnSelfCapacitanceListener(OnSelfCapacitanceListener listener) {
            mOnSelfCapacitanceListener = listener;
        }

        public interface OnResistiveSoilMoistureListener {
            void OnResistiveSoilMoistureReceived(final String address, final int soilMoisture);
        }

        public interface OnMutualCapacitanceListener {
            void OnMutualCapacitanceReceived(final String address, final int capacitance);
        }

        public interface OnSelfCapacitanceListener {
            void OnSelfCapacitanceReceived(final String address, final int capacitance);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x0C: {
                setConnected(true);
                mLastOperationModeReceived = OperationMode.RESISTANCE;
                if (ScanEvents.getOnResistiveSoilMoistureListener() != null)
                    ScanEvents.getOnResistiveSoilMoistureListener().OnResistiveSoilMoistureReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x20: {
                setConnected(true);
                mLastOperationModeReceived = OperationMode.MUTUAL_CAPACITANCE;
                if (ScanEvents.getOnMutualCapacitanceListener() != null)
                    ScanEvents.getOnMutualCapacitanceListener().OnMutualCapacitanceReceived(address, BLEUtils.UnsignedBytesToInt(data[0], data[1]));
            }
            break;
            case 0x1F: {
                setConnected(true);
                mLastOperationModeReceived = OperationMode.SELF_CAPACITANCE;
                if (ScanEvents.getOnSelfCapacitanceListener() != null)
                    ScanEvents.getOnSelfCapacitanceListener().OnSelfCapacitanceReceived(address, BLEUtils.UnsignedBytesToInt(data[0], data[1]));
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
         * Set the configuration of the SMS block.
         *
         * @param operationMode   Set the operation mode
         * @param readingInterval Set the reading interval (from 0.1s to 1036800s)
         * @return The command
         */
        public Command SetConfiguration(final OperationMode operationMode, final double readingInterval) {
            Command c = new Command("14");

            String cmdValue = "";
            switch (operationMode) {
                case RESISTANCE:
                    cmdValue += "00";
                    break;
                case MUTUAL_CAPACITANCE:
                    cmdValue += "01";
                    break;
                case SELF_CAPACITANCE:
                    cmdValue += "02";
                    break;
            }

            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo12days(readingInterval), 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the parameters of the resistive sensor
         *
         * @param offset    The value that is going to correspond to 0% in the soil moisture scale
         * @param fullscale The value that is going to correspond to 100% in the soil moisture scale
         * @return The command
         */
        public Command SetResistiveParameters(final int offset, final int fullscale) {
            Command c = new Command("16");

            String cmdValue = "00";

            cmdValue += BLEUtils.IntToHex(offset, 2);
            cmdValue += BLEUtils.IntToHex(fullscale, 2);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Reset the resistive sensor parameters to the default values
         *
         * @return The command
         */
        public Command ResetResistiveParameters() {
            Command c = new Command("16");

            String cmdValue = "0100000000";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Calibrate the Resistive Sensor of the SMS brick
         *
         * @param calibrationPoint With offset: measures the current resistance value and sets it as the offset value ("dry soil" condition).
         *                         With full-scale: the SMS measures current resistance value and sets it as the full scale value ("wet soil" condition).
         * @return The command
         */
        public Command CalibrateResistiveSensor(final CalibrationPoint calibrationPoint) {
            Command c = new Command("15");

            String cmdValue = "";
            switch (calibrationPoint) {
                case OFFSET:
                    cmdValue += "00";
                    break;
                case FULL_SCALE:
                    cmdValue += "01";
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the parameters of the capacitive sensor
         *
         * @param gain               Set the capacitive sensor gain.
         * @param oversampling       Set the capacitive sensor oversampling
         * @param filterLevel        Set the capacitive sensor filter level.
         * @param clockPrescaler     Set the capacitive sensor clock prescaler
         * @param internalResistance Set the capacitive sensor internal series resistor
         * @return The command
         */
        public Command SetCapacitiveParameters(final SensorGain gain, final SensorOversampling oversampling, SensorFilterLevel filterLevel, final SensorPTCClockPrescaler clockPrescaler, final SensorInternalResistance internalResistance) {
            Command c = new Command("17");

            String cmdValue = "00";

            switch (gain) {
                default:
                    cmdValue += "20";
                    break;
                case GAIN_1:
                    cmdValue += "01";
                    break;
                case GAIN_2:
                    cmdValue += "02";
                    break;
                case GAIN_4:
                    cmdValue += "04";
                    break;
                case GAIN_8:
                    cmdValue += "08";
                    break;
                case GAIN_16:
                    cmdValue += "10";
                    break;
                case GAIN_32:
                    cmdValue += "20";
                    break;
            }

            switch (oversampling) {
                case DISABLED:
                    cmdValue += "00";
                    break;
                case OVERSAMPLING_2:
                    cmdValue += "02";
                    break;
                case OVERSAMPLING_4:
                    cmdValue += "04";
                    break;
                case OVERSAMPLING_8:
                    cmdValue += "08";
                    break;
                case OVERSAMPLING_16:
                    cmdValue += "10";
                    break;
                case OVERSAMPLING_32:
                    cmdValue += "20";
                    break;
                case OVERSAMPLING_64:
                    cmdValue += "40";
                    break;
                case OVERSAMPLING_128:
                    cmdValue += "80";
                    break;
            }

            switch (filterLevel) {
                case FILTER_1:
                    cmdValue += "01";
                    break;
                case FILTER_2:
                    cmdValue += "02";
                    break;
                case FILTER_4:
                    cmdValue += "04";
                    break;
                case FILTER_8:
                    cmdValue += "08";
                    break;
                case FILTER_16:
                    cmdValue += "10";
                    break;
                case FILTER_32:
                    cmdValue += "20";
                    break;
                case FILTER_64:
                    cmdValue += "40";
                    break;
            }

            switch (clockPrescaler) {
                case MHZ_4:
                    cmdValue += "01";
                    break;
                case MHZ_2:
                    cmdValue += "02";
                    break;
                case MHZ_1:
                    cmdValue += "04";
                    break;
                case KHZ_500:
                    cmdValue += "08";
                    break;
            }

            switch (internalResistance) {
                case KOHM_0:
                    cmdValue += "00";
                    break;
                case KOHM_20:
                    cmdValue += "14";
                    break;
                case KOHM_50:
                    cmdValue += "32";
                    break;
                case KOHM_100:
                    cmdValue += "64";
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Reset the capacitive sensor parameters to the default values
         *
         * @return The command
         */
        public Command ResetCapacitiveParameters() {
            Command c = new Command("17");

            String cmdValue = "010000000000";

            c.setValue(cmdValue);
            return c;
        }


        /**
         * Calibrate the Capacitive Sensor of the SMS brick
         *
         * @return The command
         */
        public Command CalibrateCapacitiveSensor() {
            Command c = new Command("18");

            return c;
        }

    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB0C-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final int resistiveSensorSignal, final int soilMoisture, final int resistiveSensorOffset, final int resistiveSensorFullScale, final int capacitiveSensorSignal, final SensorGain capacitiveSensorGain, final SensorOversampling capacitiveSensorOversampling, final SensorFilterLevel capacitiveSensorFilterLevel, final SensorPTCClockPrescaler capacitiveSensorPTCClockPrescaler, final SensorInternalResistance capacitiveSensorInternalResistance, final int capacitiveSensorOffset, final double readingInterval, final OperationMode operationMode, final String firmwareVersion);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                SensorGain sensorGain;
                switch (BLEUtils.UnsignedBytesToInt(data[9])) {
                    case 1:
                        sensorGain = SensorGain.GAIN_1;
                        break;
                    case 2:
                        sensorGain = SensorGain.GAIN_2;
                        break;
                    case 4:
                        sensorGain = SensorGain.GAIN_4;
                        break;
                    case 8:
                        sensorGain = SensorGain.GAIN_8;
                        break;
                    case 16:
                        sensorGain = SensorGain.GAIN_16;
                        break;
                    case 32:
                        sensorGain = SensorGain.GAIN_32;
                        break;
                    default:
                        sensorGain = SensorGain.GAIN_1;
                        break;
                }

                SensorOversampling sensorOversampling;
                switch (BLEUtils.UnsignedBytesToInt(data[10])) {
                    case 0:
                        sensorOversampling = SensorOversampling.DISABLED;
                        break;
                    case 2:
                        sensorOversampling = SensorOversampling.OVERSAMPLING_2;
                        break;
                    case 4:
                        sensorOversampling = SensorOversampling.OVERSAMPLING_4;
                        break;
                    case 8:
                        sensorOversampling = SensorOversampling.OVERSAMPLING_8;
                        break;
                    case 16:
                        sensorOversampling = SensorOversampling.OVERSAMPLING_16;
                        break;
                    case 32:
                        sensorOversampling = SensorOversampling.OVERSAMPLING_32;
                        break;
                    case 64:
                        sensorOversampling = SensorOversampling.OVERSAMPLING_64;
                        break;
                    case 128:
                        sensorOversampling = SensorOversampling.OVERSAMPLING_128;
                        break;
                    default:
                        sensorOversampling = SensorOversampling.DISABLED;
                        break;
                }

                SensorFilterLevel sensorFilterLevel;
                switch (BLEUtils.UnsignedBytesToInt(data[11])) {
                    case 1:
                        sensorFilterLevel = SensorFilterLevel.FILTER_1;
                        break;
                    case 2:
                        sensorFilterLevel = SensorFilterLevel.FILTER_2;
                        break;
                    case 4:
                        sensorFilterLevel = SensorFilterLevel.FILTER_4;
                        break;
                    case 8:
                        sensorFilterLevel = SensorFilterLevel.FILTER_8;
                        break;
                    case 16:
                        sensorFilterLevel = SensorFilterLevel.FILTER_16;
                        break;
                    case 32:
                        sensorFilterLevel = SensorFilterLevel.FILTER_32;
                        break;
                    case 64:
                        sensorFilterLevel = SensorFilterLevel.FILTER_64;
                        break;
                    default:
                        sensorFilterLevel = SensorFilterLevel.FILTER_1;
                        break;
                }

                SensorPTCClockPrescaler sensorPTCClockPrescaler;
                switch (BLEUtils.UnsignedBytesToInt(data[12])) {
                    case 1:
                        sensorPTCClockPrescaler = SensorPTCClockPrescaler.MHZ_4;
                        break;
                    case 2:
                        sensorPTCClockPrescaler = SensorPTCClockPrescaler.MHZ_2;
                        break;
                    case 4:
                        sensorPTCClockPrescaler = SensorPTCClockPrescaler.MHZ_1;
                        break;
                    case 8:
                        sensorPTCClockPrescaler = SensorPTCClockPrescaler.KHZ_500;
                        break;
                    default:
                        sensorPTCClockPrescaler = SensorPTCClockPrescaler.MHZ_4;
                        break;
                }

                SensorInternalResistance sensorInternalResistance;
                switch (BLEUtils.UnsignedBytesToInt(data[13])) {
                    case 0:
                        sensorInternalResistance = SensorInternalResistance.KOHM_0;
                        break;
                    case 20:
                        sensorInternalResistance = SensorInternalResistance.KOHM_20;
                        break;
                    case 50:
                        sensorInternalResistance = SensorInternalResistance.KOHM_50;
                        break;
                    case 100:
                        sensorInternalResistance = SensorInternalResistance.KOHM_100;
                        break;
                    default:
                        sensorInternalResistance = SensorInternalResistance.KOHM_0;
                        break;
                }

                OperationMode operationMode;
                switch (BLEUtils.UnsignedBytesToInt(data[17])) {
                    case 0:
                        operationMode = OperationMode.RESISTANCE;
                        break;
                    case 1:
                        operationMode = OperationMode.MUTUAL_CAPACITANCE;
                        break;
                    case 2:
                        operationMode = OperationMode.SELF_CAPACITANCE;
                        break;
                    default:
                        operationMode = OperationMode.RESISTANCE;
                        break;
                }

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address,
                        BLEUtils.UnsignedBytesToInt(data[0], data[1]),
                        BLEUtils.UnsignedBytesToInt(data[2]),
                        BLEUtils.UnsignedBytesToInt(data[3], data[4]),
                        BLEUtils.UnsignedBytesToInt(data[5], data[6]),
                        BLEUtils.UnsignedBytesToInt(data[7], data[8]),
                        sensorGain,
                        sensorOversampling,
                        sensorFilterLevel,
                        sensorPTCClockPrescaler,
                        sensorInternalResistance,
                        BLEUtils.UnsignedBytesToInt(data[14], data[15]),
                        ByteToDoubleConversions.from100msTo12days(BLEUtils.UnsignedBytesToInt(data[16])),
                        operationMode,
                        String.format(Locale.US, "%.1f", BLEUtils.UnsignedBytesToInt(data[18]) / 10d)
                );
            }
        }
    }
    //endregion
}