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

public class BLEPDM extends Component {
    BLEPDM() {
        super("BLE-PDM", "Optical distance meter and presence detector");
    }

    public enum MeasuringStatus {
        VALID, OUT_OF_BOUNDS, WRAPPED_TARGET, HARDWARE_FAILURE
    }

    public enum DistanceMode {
        LONG, MEDIUM, SHORT
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnDistanceListener mOnDistanceListener;
        private OnCountersListener mOnCountersListener;

        public OnCountersListener getOnCountersListener() {
            return mOnCountersListener;
        }

        public void setOnCountersListener(OnCountersListener onCountersListener) {
            mOnCountersListener = onCountersListener;
        }

        public OnDistanceListener getOnDistanceListener() {
            return mOnDistanceListener;
        }

        public void setOnDistanceListener(OnDistanceListener onDistanceListener) {
            mOnDistanceListener = onDistanceListener;
        }

        public interface OnDistanceListener {
            void OnDistanceReceived(final String address, final int distance, final MeasuringStatus status);
        }

        public interface OnCountersListener {
            void OnCountersReceived(final String address, final int enterCounter, final int exitCounter, final int totalCounter);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x3A: {
                setConnected(true);
                if (ScanEvents.getOnDistanceListener() != null) {
                    MeasuringStatus m;
                    switch (data[2]) {
                        case 0:
                            m = MeasuringStatus.VALID;
                            break;
                        case 1:
                            m = MeasuringStatus.OUT_OF_BOUNDS;
                            break;
                        case 2:
                            m = MeasuringStatus.WRAPPED_TARGET;
                            break;
                        default:
                            m = MeasuringStatus.HARDWARE_FAILURE;
                            break;
                    }
                    ScanEvents.getOnDistanceListener().OnDistanceReceived(address, BLEUtils.UnsignedBytesToInt(data[0], data[1]), m);
                }
            }
            break;
            case 0x3C: {
                setConnected(true);
                if (ScanEvents.getOnCountersListener() != null) {
                    ScanEvents.getOnCountersListener().OnCountersReceived(address,
                            BLEUtils.UnsignedBytesToInt(data[0]),
                            BLEUtils.UnsignedBytesToInt(data[1]),
                            BLEUtils.SignedBytesToInt(data[2]));
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

        /**
         * Set the configuration of the PDM brick, enabling Ranging mode
         * @param distanceMode The distance mode
         * @param readingInterval The reading interval (from 0.15s to 2.55s)
         * @return The command
         */
        public Command SetConfigurationRangingMode(final DistanceMode distanceMode, final double readingInterval) {
            Command c = new Command("13");

            String cmdValue = "";

            switch (distanceMode) {
                case LONG:
                    cmdValue += "00";
                    break;
                case MEDIUM:
                    cmdValue += "01";
                    break;
                case SHORT:
                    cmdValue += "02";
                    break;
                default:
                    cmdValue += "00";
                    break;
            }

            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo2550ms(readingInterval), 1);
            cmdValue += "00";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the configuration of the PDM brick, Crossing Counter mode
         * @param readingInterval The reading interval (from 0.15s to 2.55s)
         * @param presenceDetectionThreshold Set, in millimeters, the presence detection threshold
         *                                   (maximum distance within which the PDM considers
         *                                   objects/people as detected), rounded at multiples of
         *                                   256mm. Use 0 (zero) to clear current counters.
         * @return The command
         */
        public Command SetConfigurationCrossingCounterMode(final double readingInterval, final int presenceDetectionThreshold) {
            Command c = new Command("13");

            String cmdValue = "03";

            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo2550ms(readingInterval), 1);

            int presenceDetectionThresholdMSB = presenceDetectionThreshold / 256;
            cmdValue += BLEUtils.IntToHex(presenceDetectionThresholdMSB, 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB3A-30B8-4A6B-913E-0EF628448151");

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

        private OnDistanceValuesListener mOnDistanceValuesListener;
        private OnCountersValuesListener mOnCountersValuesListener;

        public OnDistanceValuesListener getOnDistanceValuesListener() {
            return mOnDistanceValuesListener;
        }

        public void setOnDistanceValuesListener(OnDistanceValuesListener onDistanceValuesListener) {
            mOnDistanceValuesListener = onDistanceValuesListener;
        }

        public OnCountersValuesListener getOnCountersValuesListener() {
            return mOnCountersValuesListener;
        }

        public void setOnCountersValuesListener(OnCountersValuesListener onCountersValuesListener) {
            mOnCountersValuesListener = onCountersValuesListener;
        }

        public interface OnDistanceValuesListener {
            void OnDistanceValuesReceived(final String address, final int distance, final MeasuringStatus status, final DistanceMode distanceMode, final double readingInterval);
        }

        public interface OnCountersValuesListener {
            void OnCountersValuesReceived(final String address, final int enterCounter, final int exitCounter, final int totalCounter, final int threshold);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnDistanceValuesListener() != null) {
                if(data[3] != 3) {
                    MeasuringStatus m;
                    switch (data[2]) {
                        case 0:
                            m = MeasuringStatus.VALID;
                            break;
                        case 1:
                            m = MeasuringStatus.OUT_OF_BOUNDS;
                            break;
                        case 2:
                            m = MeasuringStatus.WRAPPED_TARGET;
                            break;
                        default:
                            m = MeasuringStatus.HARDWARE_FAILURE;
                            break;
                    }

                    DistanceMode d;
                    switch (data[3]) {
                        case 0:
                            d = DistanceMode.LONG;
                            break;
                        case 1:
                            d = DistanceMode.MEDIUM;
                            break;
                        default:
                            d = DistanceMode.SHORT;
                            break;
                    }

                    ConnectionEvents.getOnDistanceValuesListener().OnDistanceValuesReceived(address,
                            BLEUtils.UnsignedBytesToInt(data[0], data[1]),
                            m,
                            d,
                            ByteToDoubleConversions.from150msBy10ms(BLEUtils.UnsignedBytesToInt(data[4])));
                } else {
                    ConnectionEvents.getOnCountersValuesListener().OnCountersValuesReceived(address,
                            BLEUtils.UnsignedBytesToInt(data[0]),
                            BLEUtils.UnsignedBytesToInt(data[1]),
                            BLEUtils.SignedBytesToInt(data[2]),
                            BLEUtils.UnsignedBytesToInt(data[3]) * 256);
                }
            }
        }
    }
    //endregion
}