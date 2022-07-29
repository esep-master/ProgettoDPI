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

public class BLEIMU extends Component {
    private boolean mLastReceivedGyroscope = false;
    private boolean mLastReceivedAccelerometer = false;
    private boolean mLastReceivedMagnetometer = false;
    private boolean mLastReceivedQuaternions = false;

    @Override
    public void setConnected(boolean connected) {
        super.setConnected(connected);
        if(!connected) {
            mLastReceivedGyroscope = false;
            mLastReceivedAccelerometer = false;
            mLastReceivedMagnetometer = false;
            mLastReceivedQuaternions = false;
        }
    }


    public boolean isLastReceivedGyroscope() {
        return mLastReceivedGyroscope;
    }

    public boolean isLastReceivedAccelerometer() {
        return mLastReceivedAccelerometer;
    }

    public boolean isLastReceivedMagnetometer() {
        return mLastReceivedMagnetometer;
    }

    public boolean isLastReceivedQuaternions() {
        return mLastReceivedQuaternions;
    }

    BLEIMU() {
        super("BLE-IMU", "9-Axes absolute orientation sensor with accelerometer, gyroscope and magnetometer");
    }

    public enum MeasurementUnitAcceleration {
        M_S2, MG
    }

    public enum MeasurementUnitAngularRate {
        DPS, RPS
    }

    public enum MeasurementUnitEulerAngles {
        DEGREE, RADIANS
    }

    public enum FusionDataOutputFormat {
        WINDOWS, ANDROID
    }

    public static class MeasurementUnit {
        private MeasurementUnitAcceleration mAcceleration;
        private MeasurementUnitAngularRate mAngularRate;
        private MeasurementUnitEulerAngles mEulerAngles;
        private FusionDataOutputFormat mOutputFormat;

        public MeasurementUnit() {
            mAcceleration = null;
            mAngularRate = null;
            mEulerAngles = null;
            mOutputFormat = null;
        }

        public MeasurementUnit(final MeasurementUnitAcceleration acceleration, final MeasurementUnitAngularRate angularRate, final MeasurementUnitEulerAngles eulerAngles, final FusionDataOutputFormat outputFormat) {
            mAcceleration = acceleration;
            mAngularRate = angularRate;
            mEulerAngles = eulerAngles;
            mOutputFormat = outputFormat;
        }

        @Override
        public String toString() {
            if (mAcceleration == null || mAngularRate == null || mEulerAngles == null || mOutputFormat == null)
                return "FF";

            int acc = (mAcceleration == MeasurementUnitAcceleration.M_S2 ? 0 : 1);
            int ang = (mAngularRate == MeasurementUnitAngularRate.DPS ? 0 : 1);
            int eul = (mEulerAngles == MeasurementUnitEulerAngles.DEGREE ? 0 : 1);
            int format = (mOutputFormat == FusionDataOutputFormat.WINDOWS ? 0 : 1);

            return BLEUtils.IntToHex(acc + ang * 2 + eul * 4 + format * 128, 1);
        }
    }

    public enum AccelerometerRange {
        PLUS_MINUS_2, PLUS_MINUS_4, PLUS_MINUS_8, PLUS_MINUS_16,
    }

    public enum AccelerometerBandwidth {
        HZ_7_81, HZ_15_63, HZ_31_25, HZ_62_5, HZ_125, HZ_250, HZ_500, HZ_1000,
    }

    public static class AccelerometerConfiguration {
        private AccelerometerRange mRange;
        private AccelerometerBandwidth mBandwidth;

        public AccelerometerConfiguration() {
            mRange = null;
            mBandwidth = null;
        }

        public AccelerometerConfiguration(AccelerometerRange range, AccelerometerBandwidth bandwidth) {
            mRange = range;
            mBandwidth = bandwidth;
        }

        @Override
        public String toString() {
            if (mRange == null || mBandwidth == null)
                return "FF";

            int range;
            int bandwidth;

            switch (mRange) {
                default:
                    range = 0;
                    break;
                case PLUS_MINUS_4:
                    range = 1;
                    break;
                case PLUS_MINUS_8:
                    range = 2;
                    break;
                case PLUS_MINUS_16:
                    range = 3;
                    break;
            }

            switch (mBandwidth) {
                default:
                    bandwidth = 0;
                    break;
                case HZ_15_63:
                    bandwidth = 1;
                    break;
                case HZ_31_25:
                    bandwidth = 2;
                    break;
                case HZ_62_5:
                    bandwidth = 3;
                    break;
                case HZ_125:
                    bandwidth = 4;
                    break;
                case HZ_250:
                    bandwidth = 5;
                    break;
                case HZ_500:
                    bandwidth = 6;
                    break;
                case HZ_1000:
                    bandwidth = 7;
                    break;
            }

            return BLEUtils.IntToHex(range + bandwidth * 4, 1);
        }
    }

    public enum MagnetometerOutputDataRate {
        HZ_2, HZ_6, HZ_8, HZ_10, HZ_15, HZ_20, HZ_25, HZ_30
    }

    public static class MagnetometerConfiguration {
        private MagnetometerOutputDataRate mOutputDataRate;

        public MagnetometerConfiguration() {
            mOutputDataRate = null;
        }

        public MagnetometerConfiguration(MagnetometerOutputDataRate outputDataRate) {
            mOutputDataRate = outputDataRate;
        }

        @Override
        public String toString() {
            if (mOutputDataRate == null)
                return "FF";

            String result;
            switch (mOutputDataRate) {
                default:
                    result = "68";
                    break;
                case HZ_6:
                    result = "69";
                    break;
                case HZ_8:
                    result = "6A";
                    break;
                case HZ_10:
                    result = "6B";
                    break;
                case HZ_15:
                    result = "6C";
                    break;
                case HZ_20:
                    result = "6D";
                    break;
                case HZ_25:
                    result = "6E";
                    break;
                case HZ_30:
                    result = "6F";
                    break;
            }

            return result;
        }
    }

    public enum GyroscopeRange {
        DPS_2000, DPS_1000, DPS_500, DPS_250, DPS_125
    }

    public enum GyroscopeBandwidth {
        HZ_523, HZ_230, HZ_116, HZ_47, HZ_23, HZ_12, HZ_64, HZ_32
    }

    public static class GyroscopeConfiguration {
        private GyroscopeRange mRange;
        private GyroscopeBandwidth mBandwidth;

        public GyroscopeConfiguration() {
            mRange = null;
            mBandwidth = null;
        }

        public GyroscopeConfiguration(GyroscopeRange range, GyroscopeBandwidth bandwidth) {
            mRange = range;
            mBandwidth = bandwidth;
        }

        @Override
        public String toString() {
            if (mRange == null || mBandwidth == null)
                return "FF";

            int range;
            switch (mRange) {
                default:
                    range = 0;
                    break;
                case DPS_1000:
                    range = 1;
                    break;
                case DPS_500:
                    range = 2;
                    break;
                case DPS_250:
                    range = 3;
                    break;
                case DPS_125:
                    range = 4;
                    break;
            }

            int bandwidth;
            switch (mBandwidth) {
                default:
                    bandwidth = 0;
                    break;
                case HZ_230:
                    bandwidth = 1;
                    break;
                case HZ_116:
                    bandwidth = 2;
                    break;
                case HZ_47:
                    bandwidth = 3;
                    break;
                case HZ_23:
                    bandwidth = 4;
                    break;
                case HZ_12:
                    bandwidth = 5;
                    break;
                case HZ_64:
                    bandwidth = 6;
                    break;
                case HZ_32:
                    bandwidth = 7;
                    break;
            }

            return BLEUtils.IntToHex(range + bandwidth * 8, 1);
        }
    }

    public enum OperationMode {
        CONFIGURATION,
        ACCELEROMETER,
        MAGNETOMETER,
        GYROSCOPE,
        ACCELEROMETER_MAGNETOMETER,
        ACCELEROMETER_GYROSCOPE,
        MAGNETOMETER_GYROSCOPE,
        ACCELEROMETER_MAGNETOMETER_GYROSCOPE,
        RELATIVE_ORIENTATION_QUATERNIONS,
        COMPASS_QUATERNIONS,
        LOW_POWER_RELATIVE_ORIENTATION_QUATERNIONS,
        LOW_POWER_ABSOLUTE_ORIENTATION_QUATERNIONS,
        QUICK_CALIBRATION_ABSOLUTE_ORIENTATION_QUATERNIONS,
        RELATIVE_ORIENTATION_EULER_ANGLES,
        COMPASS_EULER_ANGLES,
        LOW_POWER_RELATIVE_ORIENTATION_EULER_ANGLES,
        LOW_POWER_ABSOLUTE_ORIENTATION_EULER_ANGLES,
        QUICK_CALIBRATION_ABSOLUTE_ORIENTATION_EULER_ANGLES,
        UNCHANGED
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnAccelerationListener mOnAccelerationListener;
        private OnMagneticFieldListener mOnMagneticFieldListener;
        private OnAngularRateListener mOnAngularRateListener;
        private OnEulerAnglesListener mOnEulerAnglesListener;
        private OnQuaternionsListener mOnQuaternionsListener;

        public OnAccelerationListener getOnAccelerationListener() {
            return mOnAccelerationListener;
        }

        public void setOnAccelerationListener(OnAccelerationListener onAccelerationListener) {
            mOnAccelerationListener = onAccelerationListener;
        }

        public OnMagneticFieldListener getOnMagneticFieldListener() {
            return mOnMagneticFieldListener;
        }

        public void setOnMagneticFieldListener(OnMagneticFieldListener onMagneticFieldListener) {
            mOnMagneticFieldListener = onMagneticFieldListener;
        }

        public OnAngularRateListener getOnAngularRateListener() {
            return mOnAngularRateListener;
        }

        public void setOnAngularRateListener(OnAngularRateListener onAngularRateListener) {
            mOnAngularRateListener = onAngularRateListener;
        }

        public OnEulerAnglesListener getOnEulerAnglesListener() {
            return mOnEulerAnglesListener;
        }

        public void setOnEulerAnglesListener(OnEulerAnglesListener onEulerAnglesListener) {
            mOnEulerAnglesListener = onEulerAnglesListener;
        }

        public OnQuaternionsListener getOnQuaternionsListener() {
            return mOnQuaternionsListener;
        }

        public void setOnQuaternionsListener(OnQuaternionsListener onQuaternionsListener) {
            mOnQuaternionsListener = onQuaternionsListener;
        }

        public interface OnAccelerationListener {
            void OnAccelerationReceived(final String address, final double x, final double y, final double z);
        }

        public interface OnMagneticFieldListener {
            void OnMagneticFieldReceived(final String address, final double x, final double y, final double z);
        }

        public interface OnAngularRateListener {
            void OnAngularRateReceived(final String address, final double x, final double y, final double z);
        }

        public interface OnEulerAnglesListener {
            void OnEulerAnglesReceived(final String address, final double heading, final double pitch, final double roll);
        }

        public interface OnQuaternionsListener {
            void OnQuaternionsReceived(final String address, final double w, final double x, final double y, final double z);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x8D: {
                setConnected(true);
                mLastReceivedAccelerometer = true;
                mLastReceivedGyroscope = false;
                mLastReceivedMagnetometer = false;
                mLastReceivedQuaternions = false;

                if (ScanEvents.getOnAccelerationListener() != null)
                    ScanEvents.getOnAccelerationListener().OnAccelerationReceived(address, BLEUtils.SignedBytesToInt(data[0], data[1]) / 100d, BLEUtils.SignedBytesToInt(data[2], data[3]) / 100d, BLEUtils.SignedBytesToInt(data[4], data[5]) / 100d);
            }
            break;
            case 0x8E: {
                setConnected(true);
                mLastReceivedAccelerometer = false;
                mLastReceivedGyroscope = false;
                mLastReceivedMagnetometer = true;
                mLastReceivedQuaternions = false;

                if (ScanEvents.getOnMagneticFieldListener() != null)
                    ScanEvents.getOnMagneticFieldListener().OnMagneticFieldReceived(address, BLEUtils.SignedBytesToInt(data[0], data[1]) / 16d, BLEUtils.SignedBytesToInt(data[2], data[3]) / 16d, BLEUtils.SignedBytesToInt(data[4], data[5]) / 16d);
            }
            break;
            case 0x8F: {
                setConnected(true);
                mLastReceivedAccelerometer = false;
                mLastReceivedGyroscope = true;
                mLastReceivedMagnetometer = false;
                mLastReceivedQuaternions = false;

                if (ScanEvents.getOnAngularRateListener() != null)
                    ScanEvents.getOnAngularRateListener().OnAngularRateReceived(address, BLEUtils.SignedBytesToInt(data[0], data[1]) / 16d, BLEUtils.SignedBytesToInt(data[2], data[3]) / 16d, BLEUtils.SignedBytesToInt(data[4], data[5]) / 16d);
            }
            break;
            case 0x90: {
                setConnected(true);
                mLastReceivedAccelerometer = false;
                mLastReceivedGyroscope = true;
                mLastReceivedMagnetometer = false;
                mLastReceivedQuaternions = false;

                if (ScanEvents.getOnEulerAnglesListener() != null)
                    ScanEvents.getOnEulerAnglesListener().OnEulerAnglesReceived(address, BLEUtils.SignedBytesToInt(data[0], data[1]) / 16d, BLEUtils.SignedBytesToInt(data[2], data[3]) / 16d, BLEUtils.SignedBytesToInt(data[4], data[5]) / 16d);
            }
            break;
            case 0xC5: {
                setConnected(true);
                mLastReceivedAccelerometer = false;
                mLastReceivedGyroscope = false;
                mLastReceivedMagnetometer = false;
                mLastReceivedQuaternions = true;

                if (ScanEvents.getOnQuaternionsListener() != null)
                    ScanEvents.getOnQuaternionsListener().OnQuaternionsReceived(address, BLEUtils.SignedBytesToInt(data[0], data[1]) / 16384d, BLEUtils.SignedBytesToInt(data[2], data[3]) / 16384d, BLEUtils.SignedBytesToInt(data[4], data[5]) / 16384d, BLEUtils.SignedBytesToInt(data[6], data[7]) / 16384d);
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
         * Set the IMU brick configuration
         * @param operationMode The operation mode
         * @param readingInterval Reading interval, from 0.01s to 2.55s
         * @param measurementUnit Measurement unit
         * @param accelerometerConfiguration Configuration of the Accelerometer
         * @param magnetometerConfiguration Configuration of the Magnetometer
         * @param gyroscopeConfiguration Configuration of the Gyroscope
         * @return
         */
        public Command SetConfiguration(final OperationMode operationMode, final double readingInterval, final MeasurementUnit measurementUnit, final AccelerometerConfiguration accelerometerConfiguration, final MagnetometerConfiguration magnetometerConfiguration, final GyroscopeConfiguration gyroscopeConfiguration) {
            Command c = new Command("0D");

            String cmdValue = "";

            switch (operationMode) {
                case CONFIGURATION:
                    cmdValue += "00";
                    break;
                case ACCELEROMETER:
                    cmdValue += "01";
                    break;
                case MAGNETOMETER:
                    cmdValue += "02";
                    break;
                case GYROSCOPE:
                    cmdValue += "03";
                    break;
                case ACCELEROMETER_MAGNETOMETER:
                    cmdValue += "04";
                    break;
                case ACCELEROMETER_GYROSCOPE:
                    cmdValue += "05";
                    break;
                case MAGNETOMETER_GYROSCOPE:
                    cmdValue += "06";
                    break;
                case ACCELEROMETER_MAGNETOMETER_GYROSCOPE:
                    cmdValue += "07";
                    break;
                case RELATIVE_ORIENTATION_QUATERNIONS:
                    cmdValue += "08";
                    break;
                case COMPASS_QUATERNIONS:
                    cmdValue += "09";
                    break;
                case LOW_POWER_RELATIVE_ORIENTATION_QUATERNIONS:
                    cmdValue += "0A";
                    break;
                case LOW_POWER_ABSOLUTE_ORIENTATION_QUATERNIONS:
                    cmdValue += "0B";
                    break;
                case QUICK_CALIBRATION_ABSOLUTE_ORIENTATION_QUATERNIONS:
                    cmdValue += "0C";
                    break;
                case RELATIVE_ORIENTATION_EULER_ANGLES:
                    cmdValue += "0D";
                    break;
                case COMPASS_EULER_ANGLES:
                    cmdValue += "0E";
                    break;
                case LOW_POWER_RELATIVE_ORIENTATION_EULER_ANGLES:
                    cmdValue += "0F";
                    break;
                case LOW_POWER_ABSOLUTE_ORIENTATION_EULER_ANGLES:
                    cmdValue += "10";
                    break;
                case QUICK_CALIBRATION_ABSOLUTE_ORIENTATION_EULER_ANGLES:
                    cmdValue += "11";
                    break;
                case UNCHANGED:
                    cmdValue += "FF";
                    break;
            }

            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(readingInterval), 1);
            cmdValue += measurementUnit.toString();
            cmdValue += accelerometerConfiguration.toString();
            cmdValue += magnetometerConfiguration.toString();
            cmdValue += gyroscopeConfiguration.toString();

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mAccelerometer = new Property("CEE2BB8D-30B8-4A6B-913E-0EF628448151");
        private final Property mMagnetometer = new Property("CEE2BB8E-30B8-4A6B-913E-0EF628448151");
        private final Property mGyroscope = new Property("CEE2BB8F-30B8-4A6B-913E-0EF628448151");
        private final Property mOrientation = new Property("CEE2BBC5-30B8-4A6B-913E-0EF628448151");

        public Property getAccelerometer() {
            return mAccelerometer;
        }

        public Property getMagnetometer() {
            return mMagnetometer;
        }

        public Property getGyroscope() {
            return mGyroscope;
        }

        public Property getOrientation() {
            return mOrientation;
        }
    }
    //endregion

    //region ConnectionEvents read
    public final ConnectionEvents ConnectionEvents = new ConnectionEvents();

    public static class ConnectionEvents {
        private ConnectionEvents() {
        }

        private OnAccelerometerValuesListener mOnAccelerometerValuesListener;
        private OnMagnetometerValuesListener mOnMagnetometerValuesListener;
        private OnGyroscopeValuesListener mOnGyroscopeValuesListener;
        private OnOrientationValuesListener mOnOrientationValuesListener;
        private OnOrientationQuaternionsValuesListener mOnOrientationQuaternionsValuesListener;
        private OnOrientationEulerAnglesValuesListener mOnOrientationEulerAnglesValuesListener;

        public OnAccelerometerValuesListener getOnAccelerometerValuesListener() {
            return mOnAccelerometerValuesListener;
        }

        public void setOnAccelerometerValuesListener(OnAccelerometerValuesListener onAccelerometerValuesListener) {
            mOnAccelerometerValuesListener = onAccelerometerValuesListener;
        }

        public OnMagnetometerValuesListener getOnMagnetometerValuesListener() {
            return mOnMagnetometerValuesListener;
        }

        public void setOnMagnetometerValuesListener(OnMagnetometerValuesListener onMagnetometerValuesListener) {
            mOnMagnetometerValuesListener = onMagnetometerValuesListener;
        }

        public OnGyroscopeValuesListener getOnGyroscopeValuesListener() {
            return mOnGyroscopeValuesListener;
        }

        public void setOnGyroscopeValuesListener(OnGyroscopeValuesListener onGyroscopeValuesListener) {
            mOnGyroscopeValuesListener = onGyroscopeValuesListener;
        }

        public OnOrientationValuesListener getOnOrientationValuesListener() {
            return mOnOrientationValuesListener;
        }

        public void setOnOrientationValuesListener(OnOrientationValuesListener onOrientationValuesListener) {
            mOnOrientationValuesListener = onOrientationValuesListener;
        }

        public OnOrientationQuaternionsValuesListener getOnOrientationQuaternionsValuesListener() {
            return mOnOrientationQuaternionsValuesListener;
        }

        public void setOnOrientationQuaternionsValuesListener(OnOrientationQuaternionsValuesListener onOrientationQuaternionsValuesListener) {
            mOnOrientationQuaternionsValuesListener = onOrientationQuaternionsValuesListener;
        }

        public OnOrientationEulerAnglesValuesListener getOnOrientationEulerAnglesValuesListener() {
            return mOnOrientationEulerAnglesValuesListener;
        }

        public void setOnOrientationEulerAnglesValuesListener(OnOrientationEulerAnglesValuesListener onOrientationEulerAnglesValuesListener) {
            mOnOrientationEulerAnglesValuesListener = onOrientationEulerAnglesValuesListener;
        }

        public interface OnAccelerometerValuesListener {
            void OnAccelerometerValuesReceived(final String address, final double x, final double y, final double z, final AccelerometerRange fullrangeScale, final MeasurementUnitAcceleration measurementUnit, final AccelerometerBandwidth bandwidth, final double readingInterval);
        }

        public interface OnMagnetometerValuesListener {
            void OnMagnetometerValuesReceived(final String address, final double x, final double y, final double z, final MagnetometerOutputDataRate outputDataRate, final double readingInterval);
        }

        public interface OnGyroscopeValuesListener {
            void OnGyroscopeValuesReceived(final String address, final double x, final double y, final double z, final GyroscopeRange fullrangeScale, final MeasurementUnitAngularRate measurementUnit, final GyroscopeBandwidth bandwidth, final double readingInterval);
        }

        public interface OnOrientationValuesListener {
            void OnOrientationValuesReceived(final String address, final OperationMode operationMode, final FusionDataOutputFormat outputDataFormats, final int accelerometerCalibrationStatus, final int magnetometerCalibrationStatus, final int gyroscopeCalibrationStatus, final int systemCalibrationStatus, final double readingInterval);
        }

        public interface OnOrientationQuaternionsValuesListener {
            void OnOrientationQuaternionsValuesReceived(final String address, final double w, final double x, final double y, final double z);
        }

        public interface OnOrientationEulerAnglesValuesListener {
            void OnOrientationEulerAnglesValuesReceived(final String address, final double heading, final double pitch, final double roll, final MeasurementUnitEulerAngles measurementUnit);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getAccelerometer())) {
            if (ConnectionEvents.getOnAccelerometerValuesListener() != null) {
                AccelerometerRange range;
                switch(BLEUtils.UnsignedBytesToInt(data[6])) {
                    default:
                        range = AccelerometerRange.PLUS_MINUS_2;
                        break;
                    case 1:
                        range = AccelerometerRange.PLUS_MINUS_4;
                        break;
                    case 2:
                        range = AccelerometerRange.PLUS_MINUS_8;
                        break;
                    case 3:
                        range = AccelerometerRange.PLUS_MINUS_16;
                        break;
                }

                MeasurementUnitAcceleration meas;
                switch (BLEUtils.UnsignedBytesToInt(data[7])) {
                    default:
                        meas = MeasurementUnitAcceleration.M_S2;
                        break;
                    case 1:
                        meas = MeasurementUnitAcceleration.MG;
                        break;
                }

                AccelerometerBandwidth bandwidth;
                switch (BLEUtils.UnsignedBytesToInt(data[8])) {
                    default:
                        bandwidth = AccelerometerBandwidth.HZ_7_81;
                        break;
                    case 1:
                        bandwidth = AccelerometerBandwidth.HZ_15_63;
                        break;
                    case 2:
                        bandwidth = AccelerometerBandwidth.HZ_31_25;
                        break;
                    case 3:
                        bandwidth = AccelerometerBandwidth.HZ_62_5;
                        break;
                    case 4:
                        bandwidth = AccelerometerBandwidth.HZ_125;
                        break;
                    case 5:
                        bandwidth = AccelerometerBandwidth.HZ_250;
                        break;
                    case 6:
                        bandwidth = AccelerometerBandwidth.HZ_500;
                        break;
                    case 7:
                        bandwidth = AccelerometerBandwidth.HZ_1000;
                        break;
                }

                ConnectionEvents.getOnAccelerometerValuesListener().OnAccelerometerValuesReceived(
                        address,
                        BLEUtils.SignedBytesToInt(data[0], data[1]) / 100d,
                        BLEUtils.SignedBytesToInt(data[2], data[3]) / 100d,
                        BLEUtils.SignedBytesToInt(data[4], data[5]) / 100d,
                        range,
                        meas,
                        bandwidth,
                        ByteToDoubleConversions.from10msTo2550ms(BLEUtils.UnsignedBytesToInt(data[9]))
                );
            }
        } else if (p.equals(Properties.getMagnetometer())) {
            if (ConnectionEvents.getOnMagnetometerValuesListener() != null) {
                MagnetometerOutputDataRate rate;
                switch (BLEUtils.UnsignedBytesToInt(data[6])) {
                    default:
                        rate = MagnetometerOutputDataRate.HZ_2;
                        break;
                    case 0x69:
                        rate = MagnetometerOutputDataRate.HZ_6;
                        break;
                    case 0x6A:
                        rate = MagnetometerOutputDataRate.HZ_8;
                        break;
                    case 0x6B:
                        rate = MagnetometerOutputDataRate.HZ_10;
                        break;
                    case 0x6C:
                        rate = MagnetometerOutputDataRate.HZ_15;
                        break;
                    case 0x6D:
                        rate = MagnetometerOutputDataRate.HZ_20;
                        break;
                    case 0x6E:
                        rate = MagnetometerOutputDataRate.HZ_25;
                        break;
                    case 0x6F:
                        rate = MagnetometerOutputDataRate.HZ_30;
                        break;
                }

                ConnectionEvents.getOnMagnetometerValuesListener().OnMagnetometerValuesReceived(
                        address,
                        BLEUtils.SignedBytesToInt(data[0], data[1]) / 16d,
                        BLEUtils.SignedBytesToInt(data[2], data[3]) / 16d,
                        BLEUtils.SignedBytesToInt(data[4], data[5]) / 16d,
                        rate,
                        ByteToDoubleConversions.from10msTo2550ms(BLEUtils.UnsignedBytesToInt(data[7]))
                );
            }
        } else if (p.equals(Properties.getGyroscope())) {
            if (ConnectionEvents.getOnGyroscopeValuesListener() != null) {
                GyroscopeRange range;
                switch (BLEUtils.UnsignedBytesToInt(data[6])) {
                    default:
                        range = GyroscopeRange.DPS_2000;
                        break;
                    case 1:
                        range = GyroscopeRange.DPS_1000;
                        break;
                    case 2:
                        range = GyroscopeRange.DPS_500;
                        break;
                    case 3:
                        range = GyroscopeRange.DPS_250;
                        break;
                    case 4:
                        range = GyroscopeRange.DPS_125;
                        break;
                }

                MeasurementUnitAngularRate unit;
                switch (BLEUtils.UnsignedBytesToInt(data[7])) {
                    default:
                        unit = MeasurementUnitAngularRate.DPS;
                        break;
                    case 1:
                        unit = MeasurementUnitAngularRate.RPS;
                        break;
                }

                GyroscopeBandwidth bandwidth;
                switch (BLEUtils.UnsignedBytesToInt(data[8])) {
                    default:
                        bandwidth = GyroscopeBandwidth.HZ_523;
                        break;
                    case 1:
                        bandwidth = GyroscopeBandwidth.HZ_230;
                        break;
                    case 2:
                        bandwidth = GyroscopeBandwidth.HZ_116;
                        break;
                    case 3:
                        bandwidth = GyroscopeBandwidth.HZ_47;
                        break;
                    case 4:
                        bandwidth = GyroscopeBandwidth.HZ_23;
                        break;
                    case 5:
                        bandwidth = GyroscopeBandwidth.HZ_12;
                        break;
                    case 6:
                        bandwidth = GyroscopeBandwidth.HZ_64;
                        break;
                    case 7:
                        bandwidth = GyroscopeBandwidth.HZ_32;
                        break;
                }

                ConnectionEvents.getOnGyroscopeValuesListener().OnGyroscopeValuesReceived(
                        address,
                        BLEUtils.SignedBytesToInt(data[0], data[1]) / 16d,
                        BLEUtils.SignedBytesToInt(data[2], data[3]) / 16d,
                        BLEUtils.SignedBytesToInt(data[4], data[5]) / 16d,
                        range,
                        unit,
                        bandwidth,
                        ByteToDoubleConversions.from10msTo2550ms(BLEUtils.UnsignedBytesToInt(data[9]))
                );
            }
        } else if (p.equals(Properties.getOrientation())) {
            if (ConnectionEvents.getOnOrientationValuesListener() != null) {
                int opMode = BLEUtils.UnsignedBytesToInt(data[8]);

                OperationMode op;
                switch(opMode) {
                    default:
                        op = OperationMode.CONFIGURATION;
                        break;
                    case 0x01:
                        op = OperationMode.ACCELEROMETER;
                        break;
                    case 0x02:
                        op = OperationMode.MAGNETOMETER;
                        break;
                    case 0x03:
                        op = OperationMode.GYROSCOPE;
                        break;
                    case 0x04:
                        op = OperationMode.ACCELEROMETER_MAGNETOMETER;
                        break;
                    case 0x05:
                        op = OperationMode.ACCELEROMETER_GYROSCOPE;
                        break;
                    case 0x06:
                        op = OperationMode.MAGNETOMETER_GYROSCOPE;
                        break;
                    case 0x07:
                        op = OperationMode.ACCELEROMETER_MAGNETOMETER_GYROSCOPE;
                        break;
                    case 0x08:
                        op = OperationMode.RELATIVE_ORIENTATION_QUATERNIONS;
                        break;
                    case 0x09:
                        op = OperationMode.COMPASS_QUATERNIONS;
                        break;
                    case 0x0A:
                        op = OperationMode.LOW_POWER_RELATIVE_ORIENTATION_QUATERNIONS;
                        break;
                    case 0x0B:
                        op = OperationMode.LOW_POWER_ABSOLUTE_ORIENTATION_QUATERNIONS;
                        break;
                    case 0x0C:
                        op = OperationMode.QUICK_CALIBRATION_ABSOLUTE_ORIENTATION_QUATERNIONS;
                        break;
                    case 0x0D:
                        op = OperationMode.RELATIVE_ORIENTATION_EULER_ANGLES;
                        break;
                    case 0x0E:
                        op = OperationMode.COMPASS_EULER_ANGLES;
                        break;
                    case 0x0F:
                        op = OperationMode.LOW_POWER_RELATIVE_ORIENTATION_EULER_ANGLES;
                        break;
                    case 0x10:
                        op = OperationMode.LOW_POWER_ABSOLUTE_ORIENTATION_EULER_ANGLES;
                        break;
                    case 0x11:
                        op = OperationMode.QUICK_CALIBRATION_ABSOLUTE_ORIENTATION_EULER_ANGLES;
                        break;
                }

                FusionDataOutputFormat format;
                switch(BLEUtils.UnsignedBytesToInt(data[9])) {
                    default:
                        format = FusionDataOutputFormat.WINDOWS;
                        break;
                    case 1:
                        format = FusionDataOutputFormat.ANDROID;
                        break;
                }

                ConnectionEvents.getOnOrientationValuesListener().OnOrientationValuesReceived(
                        address,
                        op,
                        format,
                        BLEUtils.UnsignedBytesToInt(data[10]),
                        BLEUtils.UnsignedBytesToInt(data[11]),
                        BLEUtils.UnsignedBytesToInt(data[12]),
                        BLEUtils.UnsignedBytesToInt(data[13]),
                        ByteToDoubleConversions.from10msTo2550ms(BLEUtils.UnsignedBytesToInt(data[14])));

                if (opMode >= 0x08 && opMode <= 0x0C) {
                    if (ConnectionEvents.getOnOrientationQuaternionsValuesListener() != null) {
                        ConnectionEvents.getOnOrientationQuaternionsValuesListener().OnOrientationQuaternionsValuesReceived(
                                address,
                                BLEUtils.SignedBytesToInt(data[0], data[1]) / 16384d,
                                BLEUtils.SignedBytesToInt(data[2], data[3]) / 16384d,
                                BLEUtils.SignedBytesToInt(data[4], data[5]) / 16384d,
                                BLEUtils.SignedBytesToInt(data[6], data[7]) / 16384d
                        );
                    }
                } else if (opMode >= 0x0D && opMode <= 0x11) {
                    if (ConnectionEvents.getOnOrientationEulerAnglesValuesListener() != null) {
                        MeasurementUnitEulerAngles unit;
                        switch (BLEUtils.UnsignedBytesToInt(data[6])) {
                            default:
                                unit = MeasurementUnitEulerAngles.DEGREE;
                                break;
                            case 1:
                                unit = MeasurementUnitEulerAngles.RADIANS;
                                break;
                        }

                        ConnectionEvents.getOnOrientationEulerAnglesValuesListener().OnOrientationEulerAnglesValuesReceived(
                                address,
                                BLEUtils.SignedBytesToInt(data[0], data[1]) / 16d,
                                BLEUtils.SignedBytesToInt(data[2], data[3]) / 16d,
                                BLEUtils.SignedBytesToInt(data[4], data[5]) / 16d,
                                unit
                        );
                    }
                }
            }
        }
    }
    //endregion
}
