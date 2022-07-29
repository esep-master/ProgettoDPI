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
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.Property;

public class BLECAP extends Component {
    private boolean mIsLatchedMode = false;
    private boolean mIsWetMode = false;

    public boolean isLatchedMode() {
        return mIsLatchedMode;
    }

    public boolean isWetMode() {
        return mIsWetMode;
    }

    BLECAP() {
        super("BLE-CAP", "Touch sensor data");
    }

    public enum OperationMode {
        NORMAL, LATCHED, UNCHANGED, WET
    }

    public enum SensorStatus {
        PRESSED, NOT_PRESSED, STATUS_A, STATUS_B
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

    public enum LedFeedbackColor {
        NONE, RED, GREEN, BLUE, YELLOW, CYAN, PURPLE, WHITE, UNCHANGED
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnTouchSensorStatusListener mOnTouchSensorStatusListener;
        private OnWetnessListener mOnWetnessListener;

        public OnTouchSensorStatusListener getOnTouchSensorStatusListener() {
            return mOnTouchSensorStatusListener;
        }

        public void setOnTouchSensorStatusListener(OnTouchSensorStatusListener listener) {
            mOnTouchSensorStatusListener = listener;
        }

        public OnWetnessListener getOnWetnessListener() {
            return mOnWetnessListener;
        }

        public void setOnWetnessListener(OnWetnessListener mOnWetnessListener) {
            this.mOnWetnessListener = mOnWetnessListener;
        }

        public interface OnTouchSensorStatusListener {
            void OnTouchSensorStatusReceived(final String address, final SensorStatus sensorStatus);
        }

        public interface OnWetnessListener {
            void OnWetnessReceived(final String address, final float wetness);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x29: {
                setConnected(true);
                mIsWetMode = true;
                mIsLatchedMode = false;
                if (ScanEvents.getOnWetnessListener() != null) {
                    float wetness = BLEUtils.UnsignedBytesToInt(data[0], data[1]) / 100f;
                    ScanEvents.getOnWetnessListener().OnWetnessReceived(address, wetness);
                }
            }
            break;
            case 0x0D: {
                setConnected(true);
                mIsWetMode = false;
                if (ScanEvents.getOnTouchSensorStatusListener() != null) {
                    int statusInt = BLEUtils.UnsignedBytesToInt(data[0]);
                    SensorStatus status;
                    switch (statusInt) {
                        default:
                            mIsLatchedMode = false;
                            status = SensorStatus.NOT_PRESSED;
                            break;
                        case 1:
                            mIsLatchedMode = false;
                            status = SensorStatus.PRESSED;
                            break;
                        case 2:
                            mIsLatchedMode = true;
                            status = SensorStatus.STATUS_A;
                            break;
                        case 3:
                            mIsLatchedMode = true;
                            status = SensorStatus.STATUS_B;
                            break;
                    }
                    ScanEvents.getOnTouchSensorStatusListener().OnTouchSensorStatusReceived(address, status);
                }
            }
        }
    }
    //endregion

    //region Commands
    public final CommandsContainer Commands = new CommandsContainer();

    public static class CommandsContainer {
        private CommandsContainer() {
        }

        /**
         * Set the configuration of the CAP brick
         *
         * @param operationMode    Set normal mode (the touch sensor status changes
         *                         whether the CAP is being touched or not) or latched mode
         *                         (the touch sensor status changes only upon subsequent touches)
         * @param ledFeedbackColor Sets the colour of the feedback LED representing the touch sensor status
         * @return The command
         */
        public Command SetConfiguration(final OperationMode operationMode, LedFeedbackColor ledFeedbackColor) {
            Command c = new Command("19");

            String cmdValue = "";

            switch (operationMode) {
                default:
                    cmdValue += "FF";
                    break;
                case NORMAL:
                    cmdValue += "00";
                    break;
                case LATCHED:
                    cmdValue += "01";
                    break;
                case WET:
                    cmdValue += "02";
                    break;
            }

            switch (ledFeedbackColor) {
                default:
                    cmdValue += "FF";
                    break;
                case NONE:
                    cmdValue += "00";
                    break;
                case RED:
                    cmdValue += "01";
                    break;
                case GREEN:
                    cmdValue += "02";
                    break;
                case BLUE:
                    cmdValue += "03";
                    break;
                case YELLOW:
                    cmdValue += "04";
                    break;
                case CYAN:
                    cmdValue += "05";
                    break;
                case PURPLE:
                    cmdValue += "06";
                    break;
                case WHITE:
                    cmdValue += "07";
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Reset the sensor parameters to the default values
         *
         * @return The command
         */
        public Command ResetSensorsParameters() {
            Command c = new Command("1A");

            String cmdValue = "010000000000";

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
        public Command SetSensorsParameters(final SensorGain gain, final SensorOversampling oversampling, final SensorFilterLevel filterLevel, final SensorPTCClockPrescaler clockPrescaler, final SensorInternalResistance internalResistance) {
            Command c = new Command("1A");

            String cmdValue = "00";

            switch (gain) {
                default:
                    cmdValue += "04";
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
         * Calibrate the Capacitive Sensor of the CAP brick
         *
         * @return The command
         */
        public Command CalibrateCapacitiveSensor() {
            Command c = new Command("1B");
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB0D-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final int sensorSignal, final SensorStatus sensorStatus, final SensorGain sensorGain, final SensorOversampling sensorOversampling, final SensorFilterLevel sensorFilterLevel, final SensorPTCClockPrescaler sensorPTCClockPrescaler, final SensorInternalResistance sensorInternalResistance, final int sensorOffset, final LedFeedbackColor ledFeedbackColor, final String firmwareVersion);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                SensorStatus sensorStatus;
                switch (BLEUtils.UnsignedBytesToInt(data[2])) {
                    case 0:
                        sensorStatus = SensorStatus.NOT_PRESSED;
                        break;
                    case 1:
                        sensorStatus = SensorStatus.PRESSED;
                        break;
                    case 2:
                        sensorStatus = SensorStatus.STATUS_A;
                        break;
                    case 3:
                        sensorStatus = SensorStatus.STATUS_B;
                        break;
                    default:
                        sensorStatus = SensorStatus.NOT_PRESSED;
                        break;
                }

                SensorGain sensorGain;
                switch (BLEUtils.UnsignedBytesToInt(data[3])) {
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
                switch (BLEUtils.UnsignedBytesToInt(data[4])) {
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
                switch (BLEUtils.UnsignedBytesToInt(data[5])) {
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
                switch (BLEUtils.UnsignedBytesToInt(data[6])) {
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
                switch (BLEUtils.UnsignedBytesToInt(data[7])) {
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

                LedFeedbackColor ledFeedbackColor;
                switch (BLEUtils.UnsignedBytesToInt(data[10])) {
                    case 0x00:
                        ledFeedbackColor = LedFeedbackColor.NONE;
                        break;
                    case 0x14:
                        ledFeedbackColor = LedFeedbackColor.RED;
                        break;
                    case 0x32:
                        ledFeedbackColor = LedFeedbackColor.GREEN;
                        break;
                    case 0x64:
                        ledFeedbackColor = LedFeedbackColor.BLUE;
                        break;
                    case 0x04:
                        ledFeedbackColor = LedFeedbackColor.YELLOW;
                        break;
                    case 0x05:
                        ledFeedbackColor = LedFeedbackColor.CYAN;
                        break;
                    case 0x06:
                        ledFeedbackColor = LedFeedbackColor.PURPLE;
                        break;
                    case 0x07:
                        ledFeedbackColor = LedFeedbackColor.WHITE;
                        break;
                    default:
                        ledFeedbackColor = LedFeedbackColor.NONE;
                        break;
                }

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address,
                        BLEUtils.UnsignedBytesToInt(data[0], data[1]),
                        sensorStatus,
                        sensorGain,
                        sensorOversampling,
                        sensorFilterLevel,
                        sensorPTCClockPrescaler,
                        sensorInternalResistance,
                        BLEUtils.UnsignedBytesToInt(data[8], data[9]),
                        ledFeedbackColor,
                        String.format(Locale.US, "%.1f", BLEUtils.UnsignedBytesToInt(data[11]) / 10d)
                );
            }
        }
    }
    //endregion
}