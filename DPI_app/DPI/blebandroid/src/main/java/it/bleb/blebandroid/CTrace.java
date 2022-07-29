/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2020. All rights reserved.
 * https://bleb.it
 *
 * Last modified 14/06/2019 22:07
 */

package it.bleb.blebandroid;

import java.util.Arrays;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.Constants;
import it.bleb.blebandroid.utils.Logger;
import it.bleb.blebandroid.utils.Property;

public class CTrace extends Component {
    CTrace() {
        super("Protector", "Contacts Tracing");
    }

    public enum WearingMode {
        BADGE_MODE, WRISTBAND_MODE
    }

    public static class Peripherals {
        private boolean mLog;
        private boolean mBuzzer;
        private boolean mVibrator;
        private boolean mLed;

        public boolean hasLog() {
            return mLog;
        }

        public boolean hasBuzzer() {
            return mBuzzer;
        }

        public boolean hasVibrator() {
            return mVibrator;
        }

        public boolean hasLed() {
            return mLed;
        }

        public Peripherals(boolean log, boolean buzzer, boolean vibrator, boolean led) {
            mLog = log;
            mBuzzer = buzzer;
            mVibrator = vibrator;
            mLed = led;
        }

        public String getHex() {
            byte val = 0;
            if (mLed) val += 1;
            if (mVibrator) val += 2;
            if (mBuzzer) val += 4;
            if (mLog) val += 8;
            return BLEUtils.BytesToHex(val);
        }

        public static Peripherals getFromHex(String hex) {
            byte val = BLEUtils.HexToBytes(hex)[0];
            return new Peripherals(BLEUtils.GetBit(val, 3), BLEUtils.GetBit(val, 2), BLEUtils.GetBit(val, 1), BLEUtils.GetBit(val, 0));
        }
    }

    public static class PeripheralsUnchanged extends Peripherals {

        public PeripheralsUnchanged() {
            super(false, false, false, false);
        }

        public String getHex() {
            return BLEUtils.BytesToHex((byte) 0xff);
        }

        public static Peripherals getFromHex(String hex) {
            return new PeripheralsUnchanged();
        }
    }

    public static class HandsOnFaceAlarm {
        private boolean mBuzzer;
        private boolean mVibrator;
        private boolean mRedLed;
        private boolean mGreenLed;
        private boolean mBlueLed;

        public boolean isBuzzer() {
            return mBuzzer;
        }

        public boolean isVibrator() {
            return mVibrator;
        }

        public boolean isRedLed() {
            return mRedLed;
        }

        public boolean isGreenLed() {
            return mGreenLed;
        }

        public boolean isBlueLed() {
            return mBlueLed;
        }

        public HandsOnFaceAlarm(boolean buzzer, boolean vibrator, boolean redLed, boolean greenLed, boolean blueLed) {
            mBuzzer = buzzer;
            mVibrator = vibrator;
            mRedLed = redLed;
            mGreenLed = greenLed;
            mBlueLed = blueLed;
        }

        public String getHex() {
            byte val = 0;
            if (mBlueLed) val += 1;
            if (mGreenLed) val += 2;
            if (mRedLed) val += 4;
            if (mVibrator) val += 8;
            if (mBuzzer) val += 16;
            return BLEUtils.BytesToHex(val);
        }

        public static HandsOnFaceAlarm getFromHex(String hex) {
            byte val = BLEUtils.HexToBytes(hex)[0];
            return new HandsOnFaceAlarm(BLEUtils.GetBit(val, 4), BLEUtils.GetBit(val, 3), BLEUtils.GetBit(val, 2), BLEUtils.GetBit(val, 1), BLEUtils.GetBit(val, 0));
        }
    }

    public static class LogEvent {
        private int mTimestamp;
        private String mAddress;
        private int mRssi;

        public LogEvent(int timestamp, String address, int rssi) {
            mTimestamp = timestamp;
            mAddress = address;
            mRssi = rssi;
        }

        public int getTimestamp() {
            return mTimestamp;
        }

        public void setTimestamp(int timestamp) {
            mTimestamp = timestamp;
        }

        public String getAddress() {
            return mAddress;
        }

        public void setAddress(String address) {
            mAddress = address;
        }

        public int getRssi() {
            return mRssi;
        }

        public void setRssi(int rssi) {
            mRssi = rssi;
        }

        public static LogEvent getFromRaw(byte[] bytes) {
            int timestamp = BLEUtils.UnsignedBytesToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
            String address = BLEUtils.BytesToMacAddress(Arrays.copyOfRange(bytes, 4, 9));
            int rssi = BLEUtils.SignedBytesToInt(bytes[9]);

            return new LogEvent(timestamp, address, rssi);
        }

        @Override
        public String toString() {
            return "LogEvent{" +
                    "mTimestamp=" + mTimestamp +
                    ", mAddress='" + mAddress + '\'' +
                    ", mRssi=" + mRssi +
                    '}';
        }
    }

    public static class Alerts {
        private boolean mCrowd;
        private boolean mAntiPanic;
        private boolean mTemperature;
        private boolean mShock;
        private boolean mManDown;

        public boolean isCrowd() {
            return mCrowd;
        }

        public void setCrowd(boolean crowd) {
            mCrowd = crowd;
        }

        public boolean isAntiPanic() {
            return mAntiPanic;
        }

        public void setAntiPanic(boolean antiPanic) {
            mAntiPanic = antiPanic;
        }

        public boolean isTemperature() {
            return mTemperature;
        }

        public void setTemperature(boolean temperature) {
            mTemperature = temperature;
        }

        public boolean isShock() {
            return mShock;
        }

        public void setShock(boolean shock) {
            mShock = shock;
        }

        public boolean isManDown() {
            return mManDown;
        }

        public void setManDown(boolean manDown) {
            mManDown = manDown;
        }


        public Alerts(boolean crowd, boolean antiPanic, boolean temperature, boolean shock, boolean manDown) {
            mCrowd = crowd;
            mAntiPanic = antiPanic;
            mTemperature = temperature;
            mShock = shock;
            mManDown = manDown;
        }

        public String getHex() {
            byte val = 0;
            if (mCrowd) val += 4;
            if (mAntiPanic) val += 8;
            if (mTemperature) val += 16;
            if (mShock) val += 64;
            if (mManDown) val += 128;
            return BLEUtils.BytesToHex(val);
        }

        public static Alerts getFromHex(String hex) {
            byte data = BLEUtils.HexToBytes(hex)[0];
            boolean crowd = BLEUtils.GetBit(data, 2);
            boolean antiPanic = BLEUtils.GetBit(data, 3);
            boolean temperature = BLEUtils.GetBit(data, 4);
            boolean shock = BLEUtils.GetBit(data, 6);
            boolean manDown = BLEUtils.GetBit(data, 7);
            return new Alerts(crowd, antiPanic, temperature, shock, manDown);
        }
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnNearDeviceListener mOnNearDeviceListener;
        private OnNoDeviceListener mOnNoDeviceListener;
        private OnCTraceListener mOnCTraceListener;
        private OnCTraceAlertListener mOnCTraceAlertListener;
        private OnCTraceLogStatusListener mOnCTraceLogStatusListener;
        private OnCTraceGroupIDListener mOnCTraceGroupIDListener;
        private OnCTraceMqttListener mOnCTraceMqttListener;
        private OnCTraceMqttOtherDatatypesListener mOnCTraceMqttOtherDatatypesListener;
        private OnCTraceDisarmedListener mOnCTraceDisarmedListener;
        private OnCTraceSnoozeStatusListener mOnCTraceSnoozeStatusListener;

        public OnCTraceSnoozeStatusListener getOnCTraceSnoozeStatusListener() {
            return mOnCTraceSnoozeStatusListener;
        }

        public void setOnCTraceSnoozeStatusListener(OnCTraceSnoozeStatusListener onCTraceSnoozeStatusListener) {
            mOnCTraceSnoozeStatusListener = onCTraceSnoozeStatusListener;
        }

        public OnCTraceMqttListener getOnCTraceMqttListener() {
            return mOnCTraceMqttListener;
        }

        public void setOnCTraceMqttListener(OnCTraceMqttListener onCTraceMqttListener) {
            mOnCTraceMqttListener = onCTraceMqttListener;
        }

        public OnCTraceGroupIDListener getOnCTraceGroupIDListener() {
            return mOnCTraceGroupIDListener;
        }

        public void setOnCTraceGroupIDListener(OnCTraceGroupIDListener onCTraceGroupIDListener) {
            mOnCTraceGroupIDListener = onCTraceGroupIDListener;
        }

        public OnCTraceListener getOnCTraceListener() {
            return mOnCTraceListener;
        }

        public void setOnCTraceListener(OnCTraceListener onCTraceListener) {
            mOnCTraceListener = onCTraceListener;
        }

        public OnNearDeviceListener getOnNearDeviceListener() {
            return mOnNearDeviceListener;
        }

        public void setOnNearDeviceListener(OnNearDeviceListener onNearDeviceListener) {
            mOnNearDeviceListener = onNearDeviceListener;
        }

        public OnNoDeviceListener getOnNoDeviceListener() {
            return mOnNoDeviceListener;
        }

        public OnCTraceAlertListener getOnCTraceAlertListener() {
            return mOnCTraceAlertListener;
        }

        public void setOnCTraceAlertListener(OnCTraceAlertListener onCTraceAlertListener) {
            mOnCTraceAlertListener = onCTraceAlertListener;
        }

        public OnCTraceLogStatusListener getOnCTraceLogStatusListener() {
            return mOnCTraceLogStatusListener;
        }

        public void setOnCTraceLogStatusListener(OnCTraceLogStatusListener onCTraceLogStatusListener) {
            mOnCTraceLogStatusListener = onCTraceLogStatusListener;
        }

        public void setOnNoDeviceListener(OnNoDeviceListener onNoDeviceListener) {
            mOnNoDeviceListener = onNoDeviceListener;
        }

        public OnCTraceMqttOtherDatatypesListener getOnCTraceMqttOtherDatatypesListener() {
            return mOnCTraceMqttOtherDatatypesListener;
        }

        public void setOnCTraceMqttOtherDatatypesListener(OnCTraceMqttOtherDatatypesListener onCTraceMqttOtherDatatypesListener) {
            mOnCTraceMqttOtherDatatypesListener = onCTraceMqttOtherDatatypesListener;
        }

        public OnCTraceDisarmedListener getOnCTraceDisarmedListener() {
            return mOnCTraceDisarmedListener;
        }

        public void setOnCTraceDisarmedListener(OnCTraceDisarmedListener onCTraceDisarmedListener) {
            mOnCTraceDisarmedListener = onCTraceDisarmedListener;
        }

        public interface OnCTraceListener {
            void OnCTraceDetected(final String address);
        }

        public interface OnNearDeviceListener {
            void OnNearDeviceReceived(final String address, final String foundAddress, final int foundRssi);
        }

        public interface OnNoDeviceListener {
            void OnNoDeviceReceived(final String address);
        }

        public interface OnCTraceDisarmedListener {
            void OnCTraceDisarmedReceived(final String address, final boolean disarmed);
        }
        public interface OnCTraceSnoozeStatusListener {
            void OnCTraceSnoozeStatusReceived(final String address, final boolean silenced);
        }

        public interface OnCTraceLogStatusListener {
            void OnCTraceLogStatusReceived(final String address, final int rssi, final boolean logInitialized, final boolean logReady, final int battery);
        }

        public interface OnCTraceAlertListener {
            void OnCTraceAlertReceived(final String address, final WearingMode wearingMode, final boolean crowd, final boolean antiPanic, final boolean temperature, final boolean orientation, final boolean shock, final boolean manDown, final boolean handsOnFace);
        }

        public interface OnCTraceGroupIDListener {
            void OnCTraceGroupIDReceived(final String address, final String groupId);
        }

        public interface OnCTraceMqttListener {
            void OnCTraceMqttReceived(final String address, final int battery, final int rssi, final byte[] manufacturerData, final String foundAddress, final int foundRssi, final boolean crowd, final boolean antiPanic, final boolean temperature, final boolean orientation, final boolean shock, final boolean manDown, final boolean snoozeStatus, final WearingMode wearingMode, final boolean disarmed);
        }

        public interface OnCTraceMqttOtherDatatypesListener {
            void OnCTraceMqttOtherDatatypesReceived(final String address, final int battery, final int rssi, final byte[] manufacturerData);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xE5: {
                setConnected(true);

                if (ScanEvents.getOnCTraceListener() != null)
                    ScanEvents.getOnCTraceListener().OnCTraceDetected(address);

                final String foundAddress = BLEUtils.BytesToMacAddress(Arrays.copyOfRange(data, 0, 5));
                final int foundRssi = BLEUtils.SignedBytesToInt(data[5]);
                final String groupId = BLEUtils.BytesToHex(data[6]).toUpperCase();

                final boolean disarmed = BLEUtils.GetBit(data[8], 0);
                final WearingMode wearingMode = BLEUtils.GetBit(data[8], 1) ? WearingMode.WRISTBAND_MODE : WearingMode.BADGE_MODE;
                final boolean snoozeStatus = BLEUtils.GetBit(data[8], 2);

                final boolean logInitialized = BLEUtils.GetBit(data[7], 0);
                final boolean logReady = BLEUtils.GetBit(data[7], 1);
                final boolean crowd = BLEUtils.GetBit(data[7], 2);
                final boolean antiPanic = BLEUtils.GetBit(data[7], 3);
                final boolean temperature = BLEUtils.GetBit(data[7], 4);
                final boolean orientation = BLEUtils.GetBit(data[7], 5);
                final boolean shock = BLEUtils.GetBit(data[7], 6);
                final boolean manDown = BLEUtils.GetBit(data[7], 7);
                final boolean handsOnFace = BLEUtils.GetBit(data[7], 5);

                if (foundAddress.equals("00:00:00:00:00")) {
                    if (ScanEvents.getOnNearDeviceListener() != null)
                        ScanEvents.getOnNoDeviceListener().OnNoDeviceReceived(address);
                } else {
                    if (ScanEvents.getOnNearDeviceListener() != null)
                        ScanEvents.getOnNearDeviceListener().OnNearDeviceReceived(address, foundAddress, foundRssi);
                }

                if (ScanEvents.getOnCTraceGroupIDListener() != null)
                    ScanEvents.getOnCTraceGroupIDListener().OnCTraceGroupIDReceived(address, groupId.equals("FF") ? null : groupId);
                if (ScanEvents.getOnCTraceLogStatusListener() != null)
                    ScanEvents.getOnCTraceLogStatusListener().OnCTraceLogStatusReceived(address, rssi, logInitialized, logReady, battery);
                if (ScanEvents.getOnCTraceAlertListener() != null)
                    ScanEvents.getOnCTraceAlertListener().OnCTraceAlertReceived(address, wearingMode, crowd, antiPanic, temperature, orientation, shock, manDown, handsOnFace);
                if (ScanEvents.getOnCTraceMqttListener() != null)
                    ScanEvents.getOnCTraceMqttListener().OnCTraceMqttReceived(address, battery, rssi, manufacturerData, foundAddress, foundRssi, crowd, antiPanic, temperature, orientation, shock, manDown, snoozeStatus, wearingMode, disarmed);

                if (ScanEvents.getOnCTraceDisarmedListener() != null)
                    ScanEvents.getOnCTraceDisarmedListener().OnCTraceDisarmedReceived(address, disarmed);
                if (ScanEvents.getOnCTraceSnoozeStatusListener() != null)
                    ScanEvents.getOnCTraceSnoozeStatusListener().OnCTraceSnoozeStatusReceived(address, snoozeStatus);
            }
            break;
            default: {
                if (ScanEvents.getOnCTraceMqttOtherDatatypesListener() != null)
                    ScanEvents.getOnCTraceMqttOtherDatatypesListener().OnCTraceMqttOtherDatatypesReceived(address, battery, rssi, manufacturerData);
            }
            break;
        }
    }
    //endregion

    //region Commands
    public final CommandsContainer Commands = new CommandsContainer();

    private static class StartLoggingCommand extends Command {
        private String mValue = null;

        public StartLoggingCommand(String characteristicUUID) {
            super(Constants.UUID_MAIN_SERVICE, characteristicUUID, "", "");
        }

        @Override
        public String getValue() {
            if (mValue == null) {
                String cmdValue = "";
                long timestamp = System.currentTimeMillis();
                cmdValue += BLEUtils.IntToHex(timestamp, 6);
                cmdValue += "00";
                mValue = cmdValue;
            }
            return mValue;
        }
    }

    public static class CommandsContainer {
        private CommandsContainer() {
        }

        public Command SetTracingProfile(final int detectionInterval) {
            Command c = new Command("30");

            String cmdValue = "";

            cmdValue += BLEUtils.IntToHex(detectionInterval, 1);

            c.setValue(cmdValue);
            return c;
        }

        public Command SetAlarmsTable(final int detectedThreshold, final Peripherals detectedAction, final int closeThreshold, final Peripherals closeAction, final int veryCloseThreshold, final Peripherals veryCloseAction) {
            Command c = new Command("31");

            String cmdValue = "";

            cmdValue += BLEUtils.IntToHex(detectedThreshold, 1);
            cmdValue += detectedAction.getHex();
            cmdValue += BLEUtils.IntToHex(closeThreshold, 1);
            cmdValue += closeAction.getHex();
            cmdValue += BLEUtils.IntToHex(veryCloseThreshold, 1);
            cmdValue += veryCloseAction.getHex();

            c.setValue(cmdValue);
            return c;
        }

        public Command SetGroupID(final String groupId) {
            Command c = new Command("32");

            String cmdValue = "";

            String sanitized = groupId;
            if (sanitized.length() > 2) {
                sanitized = sanitized.substring(0, 2);
            }
            while (sanitized.length() < 2) {
                sanitized = "0" + sanitized;
            }

            sanitized = sanitized.toUpperCase();

            cmdValue += sanitized;

            c.setValue(cmdValue);
            return c;
        }

        public Command SetTemperatureThreshold(final int threshold) {
            Command c = new Command("33");

            String cmdValue = "";

            final int sanitizedOffset = (threshold > -128 ? (threshold < 127 ? threshold : 127) : -128);
            cmdValue += BLEUtils.IntToHex(sanitizedOffset, 1);

            c.setValue(cmdValue);
            return c;
        }

        public Command TriggerAlarm(int duration) {
            Command c = new Command("34");
            String cmdValue = "1F";

            final int sDuration = Math.max(Math.min(duration, 255), 1);
            cmdValue += BLEUtils.IntToHex(sDuration, 1);

            c.setValue(cmdValue);
            return c;
        }

        public Command StartLogging() {
            StartLoggingCommand c = new StartLoggingCommand("CEE2CCCC-30B8-4A6B-913E-0EF628448151");
            return c;
        }

        public Command SetShockAlertThreshold(int threshold) {
            Command c = new Command("37");
            String cmdValue = "";
            final int sanitized = Math.max(Math.min(threshold, 100), 0);
            cmdValue += BLEUtils.IntToHex(sanitized, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetHandsOnFaceAlarm(boolean buzzer, boolean vibrator, boolean redLed, boolean greenLed, boolean blueLed) {
            Command c = new Command("38");
            String cmdValue = "";
            cmdValue += (new HandsOnFaceAlarm(buzzer, vibrator, redLed, greenLed, blueLed)).getHex();
            c.setValue(cmdValue);
            return c;
        }

        public Command SetWearingMode(WearingMode wearingMode) {
            Command c = new Command("35");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(wearingMode == WearingMode.BADGE_MODE ? 0 : 1, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetDisarmed(boolean disarmed) {
            Command c = new Command("36");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(disarmed ? 1 : 0, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetInactivityInterval(int interval, int wakeupThreshold) {
            Command c = new Command("39");
            String cmdValue = "";
            final int sInterval = Math.max(Math.min(interval, 180), 0);
            final int sWakeupThreshold = Math.max(Math.min(wakeupThreshold, 10), 1);
            cmdValue += BLEUtils.IntToHex(sInterval, 1);
            cmdValue += BLEUtils.IntToHex(sWakeupThreshold, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetSnoozeInterval(int interval) {
            Command c = new Command("3A");
            String cmdValue = "";
            final int sanitized = Math.max(Math.min(interval, 180), 0);
            cmdValue += BLEUtils.IntToHex(sanitized, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetLogReadyInterval(int interval) {
            Command c = new Command("3B");
            String cmdValue = "";
            final int sanitized = Math.max(Math.min(interval, 180), 1);
            cmdValue += BLEUtils.IntToHex(sanitized, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetPowerButton(boolean enabled) {
            Command c = new Command("3C");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(enabled ? 1 : 0, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetVolumeVibrationIntensity(int volumeIntensity, int vibrationIntensity) {
            Command c = new Command("3D");
            String cmdValue = "";
            final int sVolumeIntensity = Math.max(Math.min(volumeIntensity, 10), 1);
            final int sVibrationIntensity = Math.max(Math.min(vibrationIntensity, 10), 1);
            cmdValue += BLEUtils.IntToHex((sVolumeIntensity << 4) + sVibrationIntensity, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command Shutdown() {
            Command c = new Command("3E");
            String cmdValue = "";
            c.setValue(cmdValue);
            return c;
        }

        public Command SetManDownConfiguration(int manDownInterval, int deviceWornInterval) {
            Command c = new Command("3F");
            String cmdValue = "";
            final int manDownIntervalS = Math.max(Math.min(manDownInterval, 180), 0);
            cmdValue += BLEUtils.IntToHex(manDownIntervalS, 1);
            final int deviceWornIntervalS = Math.max(Math.min(deviceWornInterval, 180), 1);
            cmdValue += BLEUtils.IntToHex(deviceWornIntervalS, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetRescueModeConfiguration(Alerts alerts) {
            Command c = new Command("40");
            String cmdValue = "";
            cmdValue += alerts.getHex();
            c.setValue(cmdValue);
            return c;
        }

        public Command SetDeepSleep(final int hours) {
            Command c = new Command("41");
            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(hours, 1);
            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BBE5-30B8-4A6B-913E-0EF628448151");
        private final Property mSecondaryMainValues = new Property("CEE2BBE6-30B8-4A6B-913E-0EF628448151");
        private final Property mLogValues = new Property("CEE2CCCC-30B8-4A6B-913E-0EF628448151");

        public Property getSecondaryMainValues() {
            return mSecondaryMainValues;
        }

        public Property getMainValues() {
            return mMainValues;
        }

        public Property getLogValues() {
            return mLogValues;
        }
    }
    //endregion

    //region Connection Reading
    public final ConnectionEvents ConnectionEvents = new ConnectionEvents();

    public static class ConnectionEvents {
        private ConnectionEvents() {
        }

        private OnMainValuesListener mOnMainValuesListener;
        private OnLogDataListener mOnLogDataListener;
        private OnSecondMainValuesListener mOnSecondMainValuesListener;

        public OnSecondMainValuesListener getOnSecondMainValuesListener() {
            return mOnSecondMainValuesListener;
        }

        public void setOnSecondMainValuesListener(OnSecondMainValuesListener onSecondMainValuesListener) {
            mOnSecondMainValuesListener = onSecondMainValuesListener;
        }

        public OnLogDataListener getOnLogDataListener() {
            return mOnLogDataListener;
        }

        public void setOnLogDataListener(OnLogDataListener onLogDataListener) {
            mOnLogDataListener = onLogDataListener;
        }

        public OnMainValuesListener getOnMainValuesListener() {
            return mOnMainValuesListener;
        }

        public void setOnMainValuesListener(OnMainValuesListener onMainValuesListener) {
            mOnMainValuesListener = onMainValuesListener;
        }

        public interface OnMainValuesListener {
            void OnMainValuesReceived(final String address, final int detectionInterval, final Peripherals peripherals, final int volumeIntensity, final int vibrationIntensity, final int detectedThreshold, final Peripherals detectedAction, final int closeThreshold, final Peripherals closeAction, final int veryCloseThreshold, final Peripherals veryCloseAction, final String groupId, final byte alert, final int temperatureThreshold, int shockAlertThreshold, HandsOnFaceAlarm handsOnFaceAlarm, WearingMode wearingMode, boolean disarmed, int inactivityInterval, int snoozeInterval, int logReadyInterval, boolean powerButton, final int inactivityThreshold);
        }

        public interface OnSecondMainValuesListener {
            void OnSecondMainValuesReceived(final String address, final int manDownInterval, final int deviceWornInterval, final Alerts resqueModeAlerts, final boolean snoozeSilenced);
        }

        public interface OnLogDataListener {
            void OnLogDataReceived(final String address, final LogEvent logEvent1, final LogEvent logEvent2);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                final int detectionInterval = BLEUtils.UnsignedBytesToInt(data[0]);
                final Peripherals peripherals = new Peripherals(true, true, true, true);
                final int volumeIntensity = BLEUtils.UnsignedBytesToInt(data[1]) >> 4;
                final int vibrationIntensity = BLEUtils.UnsignedBytesToInt(data[1]) & 0b1111;
                final int detectedThreshold = BLEUtils.SignedBytesToInt(data[2]);
                final Peripherals detectedAction = Peripherals.getFromHex(BLEUtils.BytesToHex(data[3]));
                final int closeThreshold = BLEUtils.SignedBytesToInt(data[4]);
                final Peripherals closeAction = Peripherals.getFromHex(BLEUtils.BytesToHex(data[5]));
                final int veryCloseThreshold = BLEUtils.SignedBytesToInt(data[6]);
                final Peripherals veryCloseAction = Peripherals.getFromHex(BLEUtils.BytesToHex(data[7]));
                final String groupId = BLEUtils.BytesToHex(data[8]).toUpperCase();
                final byte alert = data[9];
                final int temperatureThreshold = BLEUtils.SignedBytesToInt(data[10]);
                final int shockAlertThreshold = BLEUtils.UnsignedBytesToInt(data[11]);
                final HandsOnFaceAlarm handsOnFaceAlarm = HandsOnFaceAlarm.getFromHex(BLEUtils.BytesToHex(data[12]));
                final WearingMode wearingMode = BLEUtils.UnsignedBytesToInt(data[13]) > 0 ? WearingMode.WRISTBAND_MODE : WearingMode.BADGE_MODE;
                final boolean disarmed = BLEUtils.GetBit(data[14], 0);
                final int inactivityInterval = BLEUtils.UnsignedBytesToInt(data[15]);
                final int snoozeInterval = BLEUtils.UnsignedBytesToInt(data[16]);
                final int logReadyInterval = BLEUtils.UnsignedBytesToInt(data[17]);
                final boolean powerButton = BLEUtils.GetBit(data[18], 0);
                final int inactivityThreshold = data.length >= 20 ? BLEUtils.UnsignedBytesToInt(data[19]) : 1;

                if (ConnectionEvents.getOnMainValuesListener() != null)
                    ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address, detectionInterval, peripherals, volumeIntensity, vibrationIntensity, detectedThreshold, detectedAction, closeThreshold, closeAction, veryCloseThreshold, veryCloseAction, groupId.equals("FF") ? null : groupId, alert, temperatureThreshold, shockAlertThreshold, handsOnFaceAlarm, wearingMode, disarmed, inactivityInterval, snoozeInterval, logReadyInterval, powerButton, inactivityThreshold);
            }
        } else if (p.equals(Properties.getSecondaryMainValues())) {
            final int manDownInterval = BLEUtils.UnsignedBytesToInt(data[0]);
            final int deviceWornInterval = BLEUtils.UnsignedBytesToInt(data[1]);
            final String resqueModeAlertsHex = BLEUtils.BytesToHex(data[2]);
            final boolean snoozeStatus = BLEUtils.UnsignedBytesToInt(data[3]) == 1;

            if (ConnectionEvents.getOnSecondMainValuesListener() != null)
                ConnectionEvents.getOnSecondMainValuesListener().OnSecondMainValuesReceived(address, manDownInterval, deviceWornInterval, Alerts.getFromHex(resqueModeAlertsHex), snoozeStatus);
        } else if (p.equals(Properties.getLogValues())) {
            if (ConnectionEvents.getOnLogDataListener() != null) {
                String log = "";
                for (byte d : data) {
                    log += BLEUtils.BytesToHex(d);
                }
                Logger.Log(this, "Log raw data from notification: 0x" + log);
                ConnectionEvents.getOnLogDataListener().OnLogDataReceived(address, LogEvent.getFromRaw(Arrays.copyOfRange(data, 0, 10)), LogEvent.getFromRaw(Arrays.copyOfRange(data, 10, 20)));
            }
        }

    }
    //endregion
}
