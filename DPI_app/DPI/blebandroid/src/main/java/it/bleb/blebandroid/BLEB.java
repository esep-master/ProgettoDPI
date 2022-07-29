/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 30/04/2019 11:44
 */

package it.bleb.blebandroid;

import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLEB extends Component {

    BLEB() {
        super("BLE-B", "Bluetooth Low Energy and NFC communication");
        setAlwaysConnected();
    }

    private boolean mBLE5 = false;

    public boolean isBLE5() {
        return mBLE5;
    }

    public void setBLE5(boolean isBLE5) {
        this.mBLE5 = isBLE5;
    }

    public enum LedColor {
        WHITE, BLUE, GREEN, RED
    }

    public enum TxPower {
        MINUS_40_DBM, MINUS_20_DBM, MINUS_16_DBM, MINUS_12_DBM, MINUS_8_DBM, MINUS_4_DBM, PLUS_3_DBM, PLUS_4_DBM, PLUS_8_DBM, ZERO_DBM
    }

    public enum GPIOMode {
        DISABLED, DIGITAL_INPUT, DIGITAL_OUTPUT, ANALOG_INPUT, PULSES_COUNTER, RPS, NOT_AVAILABLE
    }

    public enum QuuppaDeviceType {
        GENERAL_TAG, ANDROID_SMARTPHONE, ANDROID_TABLET, WRISTBAND_DEVICE, CHILDREN_WRISTBAND, PENDANT_TAG, TEMPERATURE_HUMIDITY_DEVICE, SEMIACTIVE_RFID_BLE_CARD_TAG, ANTIDEMOLITION_WRISTBAND, SCHOOL_BADGES_TAG, TAMPER_EVIDENT_ASSET_TAG, ACTIVE_KEY_TAG, MINI_TYPE_CARD_TAG, RFID_BLE_CARD_TAG
    }

    public enum ButtonState {
        UNTOUCHED, PRESSED, RELEASED, LONGPRESS
    }

    public enum ButtonMode {
        BUTTON_MODE, SWITCH_MODE
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnNameListener mOnNameListener;
        private OnBatteryListener mOnBatteryListener;
        private OnRSSIListener mOnRSSIListener;
        private OnAnalogInputListener mOnAnalogInputAListener;
        private OnAnalogInputListener mOnAnalogInputBListener;
        private OnDigitalInputsListener mOnDigitalInputsListener;
        private OnDigitalOutputListener mOnDigitalOutput0Listener;
        private OnDigitalOutputListener mOnDigitalOutput1Listener;
        private OnDigitalOutputListener mOnDigitalOutput2Listener;
        private OnDigitalOutputListener mOnDigitalOutput3Listener;
        private OnTemperatureListener mOnTemperatureListener;
        private OnPacketsCounterListener mOnPacketsCounterListener;
        private OnFirmwareReleaseListener mOnFirmwareReleaseListener;
        private OnLedsFeedbackListener mOnLedsFeedbackListener;
        private OnPersonalMessageListener mOnPersonalMessageListener;
        private OnButtonStateListener mOnButtonStateListener;
        private OnTXPowerListener mOnTXPowerListener;
        private OnScanStatusListener mOnScanStatusListener;
        private OnHasDirectInteractionsListener mOnHasDirectInteractionsListener;
        private OnPulsesCounterListener mOnPulsesCounter0Listener;
        private OnPulsesCounterListener mOnPulsesCounter1Listener;
        private OnPulsesCounterListener mOnPulsesCounter2Listener;
        private OnPulsesCounterListener mOnPulsesCounter3Listener;
        private OnRpsListener mOnRps0Listener;
        private OnRpsListener mOnRps1Listener;
        private OnRpsListener mOnRps2Listener;
        private OnRpsListener mOnRps3Listener;

        public OnNameListener getOnNameListener() {
            return mOnNameListener;
        }

        public void setOnNameListener(OnNameListener onNameListener) {
            mOnNameListener = onNameListener;
        }

        public OnBatteryListener getOnBatteryListener() {
            return mOnBatteryListener;
        }

        public void setOnBatteryListener(OnBatteryListener onBatteryListener) {
            mOnBatteryListener = onBatteryListener;
        }

        public OnRSSIListener getOnRSSIListener() {
            return mOnRSSIListener;
        }

        public void setOnRSSIListener(OnRSSIListener onRSSIListener) {
            mOnRSSIListener = onRSSIListener;
        }

        public OnAnalogInputListener getOnAnalogInputAListener() {
            return mOnAnalogInputAListener;
        }

        public void setOnAnalogInputAListener(OnAnalogInputListener onAnalogInputAListener) {
            mOnAnalogInputAListener = onAnalogInputAListener;
        }

        public OnAnalogInputListener getOnAnalogInputBListener() {
            return mOnAnalogInputBListener;
        }

        public void setOnAnalogInputBListener(OnAnalogInputListener onAnalogInputBListener) {
            mOnAnalogInputBListener = onAnalogInputBListener;
        }

        public OnDigitalInputsListener getOnDigitalInputsListener() {
            return mOnDigitalInputsListener;
        }

        public void setOnDigitalInputsListener(OnDigitalInputsListener onDigitalInputsListener) {
            mOnDigitalInputsListener = onDigitalInputsListener;
        }

        public OnDigitalOutputListener getOnDigitalOutput0Listener() {
            return mOnDigitalOutput0Listener;
        }

        public void setOnDigitalOutput0Listener(OnDigitalOutputListener onDigitalOutput0Listener) {
            mOnDigitalOutput0Listener = onDigitalOutput0Listener;
        }

        public OnDigitalOutputListener getOnDigitalOutput1Listener() {
            return mOnDigitalOutput1Listener;
        }

        public void setOnDigitalOutput1Listener(OnDigitalOutputListener onDigitalOutput1Listener) {
            mOnDigitalOutput1Listener = onDigitalOutput1Listener;
        }

        public OnDigitalOutputListener getOnDigitalOutput2Listener() {
            return mOnDigitalOutput2Listener;
        }

        public void setOnDigitalOutput2Listener(OnDigitalOutputListener onDigitalOutput2Listener) {
            mOnDigitalOutput2Listener = onDigitalOutput2Listener;
        }

        public OnDigitalOutputListener getOnDigitalOutput3Listener() {
            return mOnDigitalOutput3Listener;
        }

        public void setOnDigitalOutput3Listener(OnDigitalOutputListener onDigitalOutput3Listener) {
            mOnDigitalOutput3Listener = onDigitalOutput3Listener;
        }

        public OnTemperatureListener getOnTemperatureListener() {
            return mOnTemperatureListener;
        }

        public void setOnTemperatureListener(OnTemperatureListener onTemperatureListener) {
            mOnTemperatureListener = onTemperatureListener;
        }

        public OnPacketsCounterListener getOnPacketsCounterListener() {
            return mOnPacketsCounterListener;
        }

        public void setOnPacketsCounterListener(OnPacketsCounterListener onPacketsCounterListener) {
            mOnPacketsCounterListener = onPacketsCounterListener;
        }

        public OnFirmwareReleaseListener getOnFirmwareReleaseListener() {
            return mOnFirmwareReleaseListener;
        }

        public void setOnFirmwareReleaseListener(OnFirmwareReleaseListener onFirmwareReleaseListener) {
            mOnFirmwareReleaseListener = onFirmwareReleaseListener;
        }

        public OnLedsFeedbackListener getOnLedsFeedbackListener() {
            return mOnLedsFeedbackListener;
        }

        public void setOnLedsFeedbackListener(OnLedsFeedbackListener onLedsFeedbackListener) {
            mOnLedsFeedbackListener = onLedsFeedbackListener;
        }

        public OnPersonalMessageListener getOnPersonalMessageListener() {
            return mOnPersonalMessageListener;
        }

        public void setOnPersonalMessageListener(OnPersonalMessageListener onPersonalMessageListener) {
            mOnPersonalMessageListener = onPersonalMessageListener;
        }

        public OnButtonStateListener getOnButtonStateListener() {
            return mOnButtonStateListener;
        }

        public void setOnButtonStateListener(OnButtonStateListener onButtonStateListener) {
            mOnButtonStateListener = onButtonStateListener;
        }

        public OnTXPowerListener getOnTXPowerListener() {
            return mOnTXPowerListener;
        }

        public void setOnTXPowerListener(OnTXPowerListener onTXPowerListener) {
            mOnTXPowerListener = onTXPowerListener;
        }

        public OnScanStatusListener getOnScanStatusListener() {
            return mOnScanStatusListener;
        }

        public void setOnScanStatusListener(OnScanStatusListener onScanStatusListener) {
            mOnScanStatusListener = onScanStatusListener;
        }

        public OnHasDirectInteractionsListener getOnHasDirectInteractionsListener() {
            return mOnHasDirectInteractionsListener;
        }

        public void setOnHasDirectInteractionsListener(OnHasDirectInteractionsListener onHasDirectInteractionsListener) {
            mOnHasDirectInteractionsListener = onHasDirectInteractionsListener;
        }

        public OnPulsesCounterListener getOnPulsesCounter0Listener() {
            return mOnPulsesCounter0Listener;
        }

        public void setOnPulsesCounter0Listener(OnPulsesCounterListener onPulsesCounter0Listener) {
            mOnPulsesCounter0Listener = onPulsesCounter0Listener;
        }

        public OnPulsesCounterListener getOnPulsesCounter1Listener() {
            return mOnPulsesCounter1Listener;
        }

        public void setOnPulsesCounter1Listener(OnPulsesCounterListener onPulsesCounter1Listener) {
            mOnPulsesCounter1Listener = onPulsesCounter1Listener;
        }

        public OnPulsesCounterListener getOnPulsesCounter2Listener() {
            return mOnPulsesCounter2Listener;
        }

        public void setOnPulsesCounter2Listener(OnPulsesCounterListener onPulsesCounter2Listener) {
            mOnPulsesCounter2Listener = onPulsesCounter2Listener;
        }

        public OnPulsesCounterListener getOnPulsesCounter3Listener() {
            return mOnPulsesCounter3Listener;
        }

        public void setOnPulsesCounter3Listener(OnPulsesCounterListener onPulsesCounter3Listener) {
            mOnPulsesCounter3Listener = onPulsesCounter3Listener;
        }

        public OnRpsListener getOnRps0Listener() {
            return mOnRps0Listener;
        }

        public void setOnRps0Listener(OnRpsListener mOnRps0Listener) {
            this.mOnRps0Listener = mOnRps0Listener;
        }

        public OnRpsListener getOnRps1Listener() {
            return mOnRps1Listener;
        }

        public void setOnRps1Listener(OnRpsListener mOnRps1Listener) {
            this.mOnRps1Listener = mOnRps1Listener;
        }

        public OnRpsListener getOnRps2Listener() {
            return mOnRps2Listener;
        }

        public void setOnRps2Listener(OnRpsListener mOnRps2Listener) {
            this.mOnRps2Listener = mOnRps2Listener;
        }

        public OnRpsListener getOnRps3Listener() {
            return mOnRps3Listener;
        }

        public void setOnRps3Listener(OnRpsListener mOnRps3Listener) {
            this.mOnRps3Listener = mOnRps3Listener;
        }

        public interface OnNameListener {
            void OnNameReceived(final String address, final String name);
        }

        public interface OnBatteryListener {
            void OnBatteryReceived(final String address, final int battery);
        }

        public interface OnRSSIListener {
            void OnRSSIReceived(final String address, final int rssi);
        }

        public interface OnAnalogInputListener {
            void OnAnalogInputReceived(final String address, final double voltage);
        }

        public interface OnDigitalInputsListener {
            void OnDigitalInputsReceived(final String address, final boolean DIN0enabled, final boolean DIN0high, final boolean DIN1enabled, final boolean DIN1high, final boolean DIN2enabled, final boolean DIN2high, final boolean DIN3enabled, final boolean DIN3high);
        }

        public interface OnDigitalOutputListener {
            void OnDigitalOutputReceived(final String address, final int dutyCycle);
        }

        public interface OnTemperatureListener {
            void OnTemperatureReceived(final String address, final int temperature);
        }

        public interface OnPacketsCounterListener {
            void OnPacketsCounterReceived(final String address, final int count);
        }

        public interface OnFirmwareReleaseListener {
            void OnFirmwareReleaseReceived(final String address, final String version);
        }

        public interface OnLedsFeedbackListener {
            void OnLedsFeedbackReceived(final String address, final int blueDutyCycle, final int greenDutyCycle, final int redDutyCycle);
        }

        public interface OnPersonalMessageListener {
            void OnPersonalMessageReceived(final String address, final String message);
        }

        public interface OnButtonStateListener {
            void OnButtonStateReceived(final String address, final ButtonState state);
        }

        public interface OnTXPowerListener {
            void OnTXPowerReceived(String address, TxPower power);
        }

        public interface OnScanStatusListener {
            void OnScanStatusReceived(String address, boolean scanIsActive);
        }

        public interface OnHasDirectInteractionsListener {
            void OnHasDirectInteractionsReceived(String address, boolean hasDirectInteractions);
        }

        public interface OnPulsesCounterListener {
            void OnPulsesCounterReceived(String address, int counterLowHigh, int counterHighLow);
        }

        public interface OnRpsListener {
            void OnRpsReceived(String address, int rps);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x01: {
                if (ScanEvents.getOnAnalogInputAListener() != null)
                    ScanEvents.getOnAnalogInputAListener().OnAnalogInputReceived(address, 3d * (double) BLEUtils.UnsignedBytesToInt(data[0]) / 0xff);
            }
            break;
            case 0x02: {
                if (ScanEvents.getOnAnalogInputBListener() != null)
                    ScanEvents.getOnAnalogInputBListener().OnAnalogInputReceived(address, 3d * (double) BLEUtils.UnsignedBytesToInt(data[0]) / 0xff);
            }
            break;
            case 0x03: {
                if (ScanEvents.getOnDigitalInputsListener() != null)
                    ScanEvents.getOnDigitalInputsListener().OnDigitalInputsReceived(address, BLEUtils.GetBit(data[0], 7), BLEUtils.GetBit(data[0], 6), BLEUtils.GetBit(data[0], 5), BLEUtils.GetBit(data[0], 4), BLEUtils.GetBit(data[0], 3), BLEUtils.GetBit(data[0], 2), BLEUtils.GetBit(data[0], 1), BLEUtils.GetBit(data[0], 0));
            }
            break;
            case 0x04: {
                if (ScanEvents.getOnDigitalOutput0Listener() != null)
                    ScanEvents.getOnDigitalOutput0Listener().OnDigitalOutputReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x05: {
                if (ScanEvents.getOnDigitalOutput1Listener() != null)
                    ScanEvents.getOnDigitalOutput1Listener().OnDigitalOutputReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x06: {
                if (ScanEvents.getOnDigitalOutput2Listener() != null)
                    ScanEvents.getOnDigitalOutput2Listener().OnDigitalOutputReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x07: {
                if (ScanEvents.getOnDigitalOutput3Listener() != null)
                    ScanEvents.getOnDigitalOutput3Listener().OnDigitalOutputReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x08: {
                if (ScanEvents.getOnTemperatureListener() != null)
                    ScanEvents.getOnTemperatureListener().OnTemperatureReceived(address, BLEUtils.SignedBytesToInt(data[0]));
            }
            break;
            case 0x09: {
                if (ScanEvents.getOnPacketsCounterListener() != null)
                    ScanEvents.getOnPacketsCounterListener().OnPacketsCounterReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x0A: {
                if (ScanEvents.getOnFirmwareReleaseListener() != null)
                    ScanEvents.getOnFirmwareReleaseListener().OnFirmwareReleaseReceived(address, String.format(Locale.US, "%.2f", BLEUtils.UnsignedBytesToInt(data[0]) / 10d));
            }
            break;
            case 0x0F: {
                if (ScanEvents.getOnRps0Listener() != null)
                    ScanEvents.getOnRps0Listener().OnRpsReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x10: {
                if (ScanEvents.getOnRps1Listener() != null)
                    ScanEvents.getOnRps1Listener().OnRpsReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x11: {
                if (ScanEvents.getOnRps2Listener() != null)
                    ScanEvents.getOnRps2Listener().OnRpsReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x12: {
                if (ScanEvents.getOnRps3Listener() != null)
                    ScanEvents.getOnRps3Listener().OnRpsReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
            }
            break;
            case 0x39: {
                if (ScanEvents.getOnLedsFeedbackListener() != null)
                    ScanEvents.getOnLedsFeedbackListener().OnLedsFeedbackReceived(address, BLEUtils.UnsignedBytesToInt(data[0]), BLEUtils.UnsignedBytesToInt(data[1]), BLEUtils.UnsignedBytesToInt(data[2]));
            }
            break;
            case 0xE1: {
                if (ScanEvents.getOnPersonalMessageListener() != null)
                    ScanEvents.getOnPersonalMessageListener().OnPersonalMessageReceived(address, BLEUtils.ASCIIBytesToString(data));
            }
            break;
            case 0x23: {
                if (ScanEvents.getOnPulsesCounter0Listener() != null)
                    ScanEvents.getOnPulsesCounter0Listener().OnPulsesCounterReceived(address, BLEUtils.UnsignedBytesToInt(data[0]), BLEUtils.UnsignedBytesToInt(data[1]));
            }
            break;
            case 0x24: {
                if (ScanEvents.getOnPulsesCounter1Listener() != null)
                    ScanEvents.getOnPulsesCounter1Listener().OnPulsesCounterReceived(address, BLEUtils.UnsignedBytesToInt(data[0]), BLEUtils.UnsignedBytesToInt(data[1]));
            }
            break;
            case 0x25: {
                if (ScanEvents.getOnPulsesCounter2Listener() != null)
                    ScanEvents.getOnPulsesCounter2Listener().OnPulsesCounterReceived(address, BLEUtils.UnsignedBytesToInt(data[0]), BLEUtils.UnsignedBytesToInt(data[1]));
            }
            break;
            case 0x26: {
                if (ScanEvents.getOnPulsesCounter3Listener() != null)
                    ScanEvents.getOnPulsesCounter3Listener().OnPulsesCounterReceived(address, BLEUtils.UnsignedBytesToInt(data[0]), BLEUtils.UnsignedBytesToInt(data[1]));
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
         * Set RGB LED color and brightness (0-100).
         *
         * @param color      The color
         * @param brightness from 0 to 100
         * @return The command
         */
        public Command SetSteadyLed(final LedColor color, final int brightness) {
            Command c = new Command("01");

            String cmdValue = "";

            switch (color) {
                case BLUE:
                    cmdValue += "01";
                    break;
                case GREEN:
                    cmdValue += "02";
                    break;
                case RED:
                    cmdValue += "03";
                    break;
                default:
                    cmdValue += "00";
                    break;
            }

            final int cleanedBrightness = brightness > 0 ? (brightness < 100 ? brightness : 100) : 0;
            cmdValue += BLEUtils.IntToHex(cleanedBrightness, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set steady LED off.
         *
         * @return The command
         */
        public Command SetSteadyLedOff() {
            return new Command("01", "0000");
        }

        /**
         * Set BLE-B Beacon’s transmission power.
         *
         * @param dBm The power in dBm
         * @return The command
         */
        public Command SetTxPower(final TxPower dBm) {
            int dBmValue;
            switch (dBm) {
                case MINUS_40_DBM:
                    dBmValue = -40;
                    break;
                case MINUS_20_DBM:
                    dBmValue = -20;
                    break;
                case MINUS_16_DBM:
                    dBmValue = -16;
                    break;
                case MINUS_12_DBM:
                    dBmValue = -12;
                    break;
                case MINUS_8_DBM:
                    dBmValue = -8;
                    break;
                case MINUS_4_DBM:
                    dBmValue = -4;
                    break;
                case PLUS_3_DBM:
                    dBmValue = +3;
                    break;
                case PLUS_4_DBM:
                    dBmValue = +4;
                    break;
                case PLUS_8_DBM:
                    dBmValue = +8;
                    break;
                default:
                    dBmValue = 0;
                    break;
            }

            return new Command("02", BLEUtils.IntToHex(dBmValue, 1));
        }

        /**
         * Set advertising interval (in seconds, from 0.02s to 10s), advertising time-out (in seconds, from 0.02s to 1036800s) and packet refresh interval (in seconds, from 0.01s to 2.55s).
         *
         * @param interval,        0 = unchanged, from 0.02s to 10s
         * @param timeout,         0 = unchanged, from 0.02s to 1036800s
         * @param refreshInterval, 0 = unchanged, from 0.02s to 1036800s
         * @return The command
         */
        public Command SetAdvertising(final double interval, final double timeout, final double refreshInterval) {
            Command c = new Command("03");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from20msTo10s(interval), 1);
            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from20msTo12days(timeout), 1);
            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from20msTo12days(refreshInterval), 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set scan interval (in milliseconds, from 1ms to 255ms), scan window (in milliseconds, from 1ms to 255ms).
         *
         * @param interval, 0 = unchanged, from 1ms to 255ms
         * @param window,   0 = unchanged, from 1ms to 255ms
         * @return The command
         */
        public Command SetScan(final int interval, final int window) {
            Command c = new Command("04");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(interval, 1);
            cmdValue += BLEUtils.IntToHex(window, 1);
            //cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo25s(restartInterval), 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set battery and analogue channels measurement refresh interval (in seconds, from 1s to 255s).
         *
         * @param interval Refresh interval, from 1s to 255s
         * @return The command
         */
        public Command SetAnalogsMeasurement(final int interval) {
            Command c = new Command("05");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(interval, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set temperature measurement refresh interval (in seconds, from 1s to 255s).
         *
         * @param interval in seconds, from 1s to 255s
         * @return The command
         */
        public Command SetTemperatureMeasurement(final int interval) {
            Command c = new Command("06");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(interval, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set BLE-B Beacon’s name (String, 6 characters maximum).
         *
         * @param name The name (max 6 characters)
         * @return The command
         */
        public Command SetDeviceName(final String name) {
            Command c = new Command("07");

            String cmdValue = "";

            String nameHex = BLEUtils.BytesToHex(BLEUtils.StringToASCIIBytes(name));
            if (nameHex.length() > 12)
                nameHex = nameHex.substring(0, 12);
            cmdValue += nameHex;

            StringBuilder padRight = new StringBuilder();
            while (nameHex.length() + padRight.length() < 12)
                padRight.append('0');
            cmdValue += padRight.toString();

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set I’m Alive LED blinking interval (in seconds, from 1s to 255s) and color (from 0 to 100).
         *
         * @param interval       from 1s to 255s
         * @param blueDutyCycle  from 0 to 100
         * @param greenDutyCycle from 0 to 100
         * @param redDutyCycle   from 0 to 100
         * @return The command
         */
        public Command SetImAliveLed(final int interval, final int redDutyCycle, final int greenDutyCycle, final int blueDutyCycle) {
            Command c = new Command("08");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(interval, 1);
            cmdValue += BLEUtils.IntToHex(blueDutyCycle > 0 ? (blueDutyCycle < 100 ? blueDutyCycle : 100) : 0, 1);
            cmdValue += BLEUtils.IntToHex(greenDutyCycle > 0 ? (greenDutyCycle < 100 ? greenDutyCycle : 100) : 0, 1);
            cmdValue += BLEUtils.IntToHex(redDutyCycle > 0 ? (redDutyCycle < 100 ? redDutyCycle : 100) : 0, 1);

            c.setValue(cmdValue);
            return c;
        }


        /**
         * Configure the GPIO 0 as digital/analogue input or digital output connecting as a parameter a GPIOMode... block. If input or pulse counter set the reading interval (from 0.01s to 2.55s), if output the duty-cycle (from 0 to 255)
         *
         * @param mode                from 0 to 100
         * @param intervalOrDutyCycle If input, set the reading interval (from 0 to 100), if output the duty-cycle (from 0 to 255)
         * @return The command
         */
        public Command SetGPIO0Configuration(final GPIOMode mode, final double intervalOrDutyCycle) {
            Command c = new Command("09");

            String cmdValue = "00";

            switch (mode) {
                case DIGITAL_INPUT:
                    cmdValue += "01";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case DIGITAL_OUTPUT:
                    cmdValue += "02";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 100 ? intervalOrDutyCycle : 100) : 0), 1);
                    break;
                case ANALOG_INPUT:
                    cmdValue += "03";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 255 ? intervalOrDutyCycle : 255) : 0), 1);
                    break;
                case PULSES_COUNTER:
                    cmdValue += "04";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case RPS:
                    cmdValue += "05";
                    cmdValue += "00";
                    break;
                default:
                    cmdValue += "0000";
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Configure the GPIO 1 as digital/analogue input or digital output connecting as a parameter a GPIOMode... block. If input or pulse counter set the reading interval (from 0.01s to 2.55s), if output the duty-cycle (from 0 to 255)
         *
         * @param mode from 0 to 100
         *             * @param intervalOrDutyCycle If input, set the reading interval (from 0 to 100), if output the duty-cycle (from 0 to 255)
         * @return
         */
        public Command SetGPIO1Configuration(final GPIOMode mode, final double intervalOrDutyCycle) {
            Command c = new Command("09");

            String cmdValue = "01";

            switch (mode) {
                case DIGITAL_INPUT:
                    cmdValue += "01";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case DIGITAL_OUTPUT:
                    cmdValue += "02";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 100 ? intervalOrDutyCycle : 100) : 0), 1);
                    break;
                case ANALOG_INPUT:
                    cmdValue += "03";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 255 ? intervalOrDutyCycle : 255) : 0), 1);
                    break;
                case PULSES_COUNTER:
                    cmdValue += "04";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case RPS:
                    cmdValue += "05";
                    cmdValue += "00";
                    break;
                default:
                    cmdValue += "0000";
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Configure the GPIO 2 as digital/analogue input or digital output connecting as a parameter a GPIOMode... block. If input or pulse counter set the reading interval (from 0.01s to 2.55s), if output the duty-cycle (from 0 to 255)
         *
         * @param mode
         * @param intervalOrDutyCycle
         * @return
         */
        public Command SetGPIO2Configuration(final GPIOMode mode, final double intervalOrDutyCycle) {
            Command c = new Command("09");

            String cmdValue = "02";

            switch (mode) {
                case DIGITAL_INPUT:
                    cmdValue += "01";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case DIGITAL_OUTPUT:
                    cmdValue += "02";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 100 ? intervalOrDutyCycle : 100) : 0), 1);
                    break;
                case ANALOG_INPUT:
                    cmdValue += "03";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 255 ? intervalOrDutyCycle : 255) : 0), 1);
                    break;
                case PULSES_COUNTER:
                    cmdValue += "04";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case RPS:
                    cmdValue += "05";
                    cmdValue += "00";
                    break;
                default:
                    cmdValue += "0000";
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Configure the GPIO 3 as digital/analogue input or digital output connecting as a parameter a GPIOMode... block. If input or pulse counter set the reading interval (from 0.01s to 2.55s), if output the duty-cycle (from 0 to 255)
         *
         * @param mode
         * @param intervalOrDutyCycle
         * @return
         */
        public Command SetGPIO3Configuration(final GPIOMode mode, final double intervalOrDutyCycle) {
            Command c = new Command("09");

            String cmdValue = "03";

            switch (mode) {
                case DIGITAL_INPUT:
                    cmdValue += "01";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case DIGITAL_OUTPUT:
                    cmdValue += "02";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 100 ? intervalOrDutyCycle : 100) : 0), 1);
                    break;
                case ANALOG_INPUT:
                    cmdValue += "03";
                    cmdValue += BLEUtils.IntToHex((int) (intervalOrDutyCycle > 0 ? (intervalOrDutyCycle < 255 ? intervalOrDutyCycle : 255) : 0), 1);
                    break;
                case PULSES_COUNTER:
                    cmdValue += "04";
                    cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(intervalOrDutyCycle), 1);
                    break;
                case RPS:
                    cmdValue += "05";
                    cmdValue += "00";
                    break;
                default:
                    cmdValue += "0000";
                    break;
            }

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Enable/disable Full Packets Counter (connect a boolean value)
         *
         * @param enabled
         * @return
         */
        public Command SetPacketsCounterEnabled(final boolean enabled) {
            return new Command("0A", enabled ? "01" : "00");
        }

        /**
         * Set the button mode
         *
         * @param buttonMode selected mode
         * @return
         */
        public Command SetButtonMode(final ButtonMode buttonMode) {
            return new Command("0B", buttonMode == ButtonMode.BUTTON_MODE ? "00" : "01");
        }

        /**
         * Enable/disable (boolean value) the personal message, set the Personal Message (String, max 9 characters).
         *
         * @param enabled
         * @param message
         * @return
         */
        public Command SetPersonalMessage(final boolean enabled, final String message) {
            Command c = new Command("A0");

            String cmdValue = "";

            cmdValue += enabled ? "01" : "00";
            cmdValue += "01";

            String messageHex = BLEUtils.BytesToHex(BLEUtils.StringToASCIIBytes(message));
            if (messageHex.length() > 18)
                messageHex = messageHex.substring(0, 18);
            cmdValue += messageHex;

            StringBuilder padRight = new StringBuilder();
            while (messageHex.length() + padRight.length() < 18)
                padRight.append('0');
            cmdValue += padRight.toString();

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Clear the Personal Message. Set if enable or disable (boolean value) the personal message
         *
         * @param enabled
         * @return
         */
        public Command ClearPersonalMessage(final boolean enabled) {
            Command c = new Command("A0");

            String cmdValue = "";

            cmdValue += enabled ? "01" : "00";
            cmdValue += "00";
            cmdValue += "000000000000000000";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Enable/Disable (boolean value) Low Power Mode
         *
         * @param enabled
         * @return
         */
        public Command SetLowPowerMode(final boolean enabled) {
            return new Command("A2", enabled ? "01" : "00");
        }

        /**
         * Set temperature offset value (from -128 to 127) for more accurate temperature measurement.
         *
         * @param offset
         * @return
         */
        public Command SetTemperatureOffset(final int offset) {
            Command c = new Command("AA");

            String cmdValue = "";

            final int sanitizedOffset = (offset > -128 ? (offset < 127 ? offset : 127) : -128);
            cmdValue += BLEUtils.IntToHex(sanitizedOffset, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Enable/Disable (boolean value) Feedback on LEDs’ brightnesses.
         *
         * @param enabled
         * @return
         */
        public Command SetLedsFeedbackEnabled(final boolean enabled) {
            return new Command("E6", enabled ? "01" : "00");
        }

        /**
         * Set duty-cycle for slow-varying digital outputs. Select the GPIO passing its ID number (from 0 to 3), set if its starting status should be high (boolean value), the interval of the high status (in seconds, from 0s to 255s), and the interval of low status (in seconds, from 0s to 255s)
         *
         * @param gpioId
         * @param startingStatusHigh
         * @param highStatusInterval
         * @param lowStatusInterval
         * @return
         */
        public Command SetTogglingOutput(final int gpioId, final boolean startingStatusHigh, final int highStatusInterval, final int lowStatusInterval) {
            Command c = new Command("EE");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(gpioId, 1);
            cmdValue += startingStatusHigh ? "01" : "00";
            cmdValue += BLEUtils.IntToHex(highStatusInterval, 1);
            cmdValue += BLEUtils.IntToHex(lowStatusInterval, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Enable/Disable sending of FW revision (boolean value)
         *
         * @param enabled
         * @return
         */
        public Command SetShowFWRelease(final boolean enabled) {
            return new Command("FF", enabled ? "01" : "00");
        }

        /**
         * Delete commands stored in flash memory
         *
         * @return
         */
        public Command DeleteFlash() {
            return new Command("DF", "");
        }

        /**
         * Enable / Disable (booelan value) Quuppa Mode, choose if it should use a Quuppa Tag ID (boolean value), and set custom Quuppa Tag ID (String in address format, i.e. 00:00:00:00:00:00).
         *
         * @param quuppaModeEnabled
         * @param useCustomTagID
         * @param customTagID
         * @return
         */
        public Command SetQuuppaTagID(final boolean quuppaModeEnabled, final boolean useCustomTagID, final String customTagID) {
            Command c = new Command("F0");

            String cmdValue = "";
            cmdValue += quuppaModeEnabled ? "01" : "00";
            cmdValue += useCustomTagID ? "01" : "02";
            cmdValue += BLEUtils.MacAddressRemoveColons(customTagID);

            c.setValue(cmdValue);
            return c;
        }


        /**
         * Set Quuppa Device Type
         *
         * @param type
         * @return
         */
        public Command SetQuuppaDeviceType(final QuuppaDeviceType type) {
            String typeHex;
            switch (type) {
                default:
                    typeHex = "20";
                    break;
                case ANDROID_SMARTPHONE:
                    typeHex = "21";
                    break;
                case ANDROID_TABLET:
                    typeHex = "22";
                    break;
                case WRISTBAND_DEVICE:
                    typeHex = "23";
                    break;
                case CHILDREN_WRISTBAND:
                    typeHex = "24";
                    break;
                case PENDANT_TAG:
                    typeHex = "25";
                    break;
                case TEMPERATURE_HUMIDITY_DEVICE:
                    typeHex = "26";
                    break;
                case SEMIACTIVE_RFID_BLE_CARD_TAG:
                    typeHex = "27";
                    break;
                case ANTIDEMOLITION_WRISTBAND:
                    typeHex = "28";
                    break;
                case SCHOOL_BADGES_TAG:
                    typeHex = "29";
                    break;
                case TAMPER_EVIDENT_ASSET_TAG:
                    typeHex = "2A";
                    break;
                case ACTIVE_KEY_TAG:
                    typeHex = "2B";
                    break;
                case MINI_TYPE_CARD_TAG:
                    typeHex = "2C";
                    break;
                case RFID_BLE_CARD_TAG:
                    typeHex = "2D";
                    break;
            }

            return new Command("F1", typeHex);
        }

        /**
         * Set Quuppa Packet Type to Quuppa Direction Finding Packet.
         *
         * @return
         */
        public Command SetQuuppaDirectionFindingPacketType() {
            return new Command("F2", "000000000000000000000000000000000000");
        }

        /**
         * Set Quuppa Packet Type to Quuppa Data Packet with Info Payload and set its Developer ID (string of 4 characters in HEX format), if the battery alarm, button 1, button 2, battery voltage meter, temperature meter are supported (boolean values), the firmware version (String in format 1.23) and the hardware version (String in format 1.23).
         *
         * @param developerID
         * @param batteryAlarmSupported
         * @param button1Supported
         * @param button2Supported
         * @param batteryVoltageMeterSupported
         * @param temperatureMeterSupported
         * @param fwVersion
         * @param hwVersion
         * @return
         */
        public Command SetQuuppaDataPacketTypeDeviceInfoPayload(final String developerID, final boolean batteryAlarmSupported, final boolean button1Supported, final boolean button2Supported, final boolean batteryVoltageMeterSupported, final boolean temperatureMeterSupported, final String fwVersion, final String hwVersion) {
            Command c = new Command("F2");

            String cmdValue = "";
            cmdValue += developerID.trim().toUpperCase();

            cmdValue += "01";

            int supported = 0;
            supported += batteryAlarmSupported ? 128 : 0;
            supported += button1Supported ? 64 : 0;
            supported += button2Supported ? 32 : 0;
            supported += batteryVoltageMeterSupported ? 16 : 0;
            supported += temperatureMeterSupported ? 8 : 0;
            supported += false ? 4 : 0;
            supported += false ? 2 : 0;
            supported += false ? 1 : 0;

            cmdValue += BLEUtils.IntToHex(supported, 1);

            int major = 0;
            int minor = 0;

            String cleanedFwVersion = fwVersion.trim().toLowerCase().replace(",", ".");
            try {
                major = Integer.parseInt(cleanedFwVersion.split("\\.")[0]);
            } catch (NumberFormatException ignore) {
            }
            cmdValue += BLEUtils.IntToHex(major, 1);

            try {
                minor = Integer.parseInt(cleanedFwVersion.split("\\.")[1]);
            } catch (NumberFormatException ignore) {
            }
            cmdValue += BLEUtils.IntToHex(minor, 1);

            major = 0;
            minor = 0;

            String cleanedHwVersion = hwVersion.trim().toLowerCase().replace(",", ".");
            try {
                major = Integer.parseInt(cleanedHwVersion.split("\\.")[0]);
            } catch (NumberFormatException ignore) {
            }
            cmdValue += BLEUtils.IntToHex(major, 1);

            try {
                minor = Integer.parseInt(cleanedHwVersion.split("\\.")[1]);
            } catch (NumberFormatException ignore) {
            }
            cmdValue += BLEUtils.IntToHex(minor, 1);

            cmdValue += "000000000000000000";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set Quuppa Packet Type to Quuppa Data Packet with Developer Specific Payload and set its Developer ID (string of 4 characters in HEX format).
         *
         * @param developerID 4 chars HEX format
         * @return
         */
        public Command SetQuuppaDataPacketTypeDeveloperSpecificPayload(final String developerID) {
            Command c = new Command("F2");

            String cmdValue = "";
            cmdValue += developerID.trim().toUpperCase();

            cmdValue += "FF";

            cmdValue += "00"; // reserved

            cmdValue += "00000000000000000000000000";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set Quuppa Packet repetitions number for each packet type (from 0 to 65565).
         *
         * @param directionFindingPacketsLimit         from 0 to 65565
         * @param deviceInfoPacketsLimit               from 0 to 65565
         * @param developerSpecificPayloadPacketsLimit from 0 to 65565
         * @return
         */
        public Command SetQuuppaPacketInterlacing(final int directionFindingPacketsLimit, final int deviceInfoPacketsLimit, final int developerSpecificPayloadPacketsLimit) {
            Command c = new Command("F3");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(directionFindingPacketsLimit, 2);
            cmdValue += BLEUtils.IntToHex(deviceInfoPacketsLimit, 2);
            cmdValue += BLEUtils.IntToHex(developerSpecificPayloadPacketsLimit, 2);
            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set scan inhibition time period
         *
         * @param period In seconds, from 0s to 255s
         * @return The command
         */
        public Command SetInhibitScanPeriod(final int period) {
            Command c = new Command("AB");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(period, 1);
            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set scan inhibition time period
         *
         * @param rssi RSSI value, from -96 dBm to -1 dBm
         * @return The command
         */
        public Command SetRSSIFilterThreshold(final int rssi) {
            Command c = new Command("AC");

            int rssiValid = rssi;
            if (rssiValid < -96)
                rssiValid = -96;
            else if (rssiValid > -1)
                rssiValid = -1;

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(rssiValid, 1);
            c.setValue(cmdValue);
            return c;
        }


        /**
         * Ask for datalogger data. The result can be read after this command is sent.
         *
         * @return The command
         */
        public Command AskForDataloggerData(final int index) {
            Command c = new Command("BC");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(index, 2);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Ask for datalogger data. The result can be read after this command is sent.
         *
         * @return The command
         */
        public Command Bootloader() {
            Command c = new Command("FE");

            String cmdValue = "";
            c.setValue(cmdValue);

            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BBBB-30B8-4A6B-913E-0EF628448151");
        private final Property mIOs = new Property("CEE2BB01-30B8-4A6B-913E-0EF628448151");
        private final Property mInfo = new Property("CEE2BB02-30B8-4A6B-913E-0EF628448151");
        private final Property mBlebricksList = new Property("CEE2BBFF-30B8-4A6B-913E-0EF628448151");

        public Property getMainValues() {
            return mMainValues;
        }

        public Property getIOs() {
            return mIOs;
        }

        public Property getInfo() {
            return mInfo;
        }

        public Property getBlebricksList() {
            return mBlebricksList;
        }
    }
    //endregion

    //region ConnectionEvents read
    public final ConnectionEvents ConnectionEvents = new ConnectionEvents();

    public static class ConnectionEvents {
        private ConnectionEvents() {

        }

        private OnMainValuesListener mOnMainValuesListener;
        private OnIOsListener mOnIOsListener;
        private OnInfoListener mOnInfoListener;
        private OnBlebricksListListener mOnBlebricksListListener;

        public OnMainValuesListener getOnMainValuesListener() {
            return mOnMainValuesListener;
        }

        public void setOnMainValuesListener(OnMainValuesListener onMainValuesListener) {
            mOnMainValuesListener = onMainValuesListener;
        }

        public OnIOsListener getOnIOsListener() {
            return mOnIOsListener;
        }

        public void setOnIOsListener(OnIOsListener onIOsListener) {
            mOnIOsListener = onIOsListener;
        }

        public OnInfoListener getOnInfoListener() {
            return mOnInfoListener;
        }

        public void setOnInfoListener(OnInfoListener onInfoListener) {
            mOnInfoListener = onInfoListener;
        }

        public OnBlebricksListListener getOnBlebricksListListener() {
            return mOnBlebricksListListener;
        }

        public void setOnBlebricksListListener(OnBlebricksListListener onBlebricksListListener) {
            mOnBlebricksListListener = onBlebricksListListener;
        }

        public interface OnMainValuesListener {
            void OnMainValuesReceived(final String address, final String hex);
        }

        public interface OnIOsListener {
            void OnGPIO0Received(final String address, final GPIOMode mode, final int value_or_counterLowHigh, final int interval_or_counterHighLow);

            void OnGPIO1Received(final String address, final GPIOMode mode, final int value_or_counterLowHigh, final int interval_or_counterHighLow);

            void OnGPIO2Received(final String address, final GPIOMode mode, final int value_or_counterLowHigh, final int interval_or_counterHighLow);

            void OnGPIO3Received(final String address, final GPIOMode mode, final int value_or_counterLowHigh, final int interval_or_counterHighLow);

            void OnButtonReceived(final String address, final ButtonState state);

            void OnTemperatureReceived(final String address, final int temperature, final int offset, final int interval);

            void OnLedsReceived(final String address, final int blueDutyCycle, final int greenDutyCycle, final int redDutyCycle);
        }

        public interface OnInfoListener {
            void OnInfoReceived(final String address, final int battery, final int analogsReadingInterval, final TxPower txPower, final int imAliveLedBlinkingInterval, final String fwRelease, final boolean isSentryMode, final int rssiThreshold, final boolean isLowPowerMode);

            void OnAdvertisingReceived(final String address, final double interval, final double duration, final double refreshInterval);

            void OnScanReceived(final String address, final int interval, final int window);
        }

        public interface OnBlebricksListListener {
            void OnBlebricksListReceived(final String address, final boolean PRTisConnected, final boolean IMUisConnected, final boolean ENVisConnected, final boolean UVAisConnected, final boolean RGBisConnected, final boolean RHTisConnected, final boolean RELisConnected, final boolean PDMisConnected, final boolean SMSisConnected, final boolean SFXisConnected);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null)
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address, BLEUtils.BytesToHex(data));

            if (notification)
                Blebricks.AnalyzeManufacturerData(address, 100, 0, data);
        } else if (p.equals(Properties.getIOs())) {
            if (ConnectionEvents.getOnIOsListener() != null) {
                GPIOMode mode0, mode1, mode2, mode3;
                switch (BLEUtils.UnsignedBytesToInt(data[0])) {
                    case 0:
                        mode0 = GPIOMode.DISABLED;
                        break;
                    case 1:
                        mode0 = GPIOMode.DIGITAL_INPUT;
                        break;
                    case 2:
                        mode0 = GPIOMode.DIGITAL_OUTPUT;
                        break;
                    case 3:
                        mode0 = GPIOMode.ANALOG_INPUT;
                        break;
                    case 4:
                        mode0 = GPIOMode.PULSES_COUNTER;
                        break;
                    case 5:
                        mode0 = GPIOMode.RPS;
                        break;
                    default:
                        mode0 = GPIOMode.NOT_AVAILABLE;
                        break;
                }
                switch (BLEUtils.UnsignedBytesToInt(data[3])) {
                    case 0:
                        mode1 = GPIOMode.DISABLED;
                        break;
                    case 1:
                        mode1 = GPIOMode.DIGITAL_INPUT;
                        break;
                    case 2:
                        mode1 = GPIOMode.DIGITAL_OUTPUT;
                        break;
                    case 3:
                        mode1 = GPIOMode.ANALOG_INPUT;
                        break;
                    case 4:
                        mode1 = GPIOMode.PULSES_COUNTER;
                        break;
                    case 5:
                        mode1 = GPIOMode.RPS;
                        break;
                    default:
                        mode1 = GPIOMode.NOT_AVAILABLE;
                        break;
                }
                switch (BLEUtils.UnsignedBytesToInt(data[6])) {
                    case 0:
                        mode2 = GPIOMode.DISABLED;
                        break;
                    case 1:
                        mode2 = GPIOMode.DIGITAL_INPUT;
                        break;
                    case 2:
                        mode2 = GPIOMode.DIGITAL_OUTPUT;
                        break;
                    case 3:
                        mode2 = GPIOMode.ANALOG_INPUT;
                        break;
                    case 4:
                        mode2 = GPIOMode.PULSES_COUNTER;
                        break;
                    case 5:
                        mode2 = GPIOMode.RPS;
                        break;
                    default:
                        mode2 = GPIOMode.NOT_AVAILABLE;
                        break;
                }
                switch (BLEUtils.UnsignedBytesToInt(data[9])) {
                    case 0:
                        mode3 = GPIOMode.DISABLED;
                        break;
                    case 1:
                        mode3 = GPIOMode.DIGITAL_INPUT;
                        break;
                    case 2:
                        mode3 = GPIOMode.DIGITAL_OUTPUT;
                        break;
                    case 3:
                        mode3 = GPIOMode.ANALOG_INPUT;
                        break;
                    case 4:
                        mode3 = GPIOMode.PULSES_COUNTER;
                        break;
                    case 5:
                        mode3 = GPIOMode.RPS;
                        break;
                    default:
                        mode3 = GPIOMode.NOT_AVAILABLE;
                        break;
                }

                if (ConnectionEvents.getOnIOsListener() != null)
                    ConnectionEvents.getOnIOsListener().OnGPIO0Received(address, mode0,
                            BLEUtils.UnsignedBytesToInt(data[1]),
                            BLEUtils.UnsignedBytesToInt(data[2]));
                if (ConnectionEvents.getOnIOsListener() != null)
                    ConnectionEvents.getOnIOsListener().OnGPIO1Received(address, mode1,
                            BLEUtils.UnsignedBytesToInt(data[4]),
                            BLEUtils.UnsignedBytesToInt(data[5]));
                if (ConnectionEvents.getOnIOsListener() != null)
                    ConnectionEvents.getOnIOsListener().OnGPIO2Received(address, mode2,
                            BLEUtils.UnsignedBytesToInt(data[7]),
                            BLEUtils.UnsignedBytesToInt(data[8]));
                if (ConnectionEvents.getOnIOsListener() != null)
                    ConnectionEvents.getOnIOsListener().OnGPIO3Received(address, mode3,
                            BLEUtils.UnsignedBytesToInt(data[10]),
                            BLEUtils.UnsignedBytesToInt(data[11]));

                ButtonState btnState;
                switch (BLEUtils.UnsignedBytesToInt(data[12])) {
                    default:
                        btnState = ButtonState.RELEASED;
                        break;
                    case 1:
                        btnState = ButtonState.PRESSED;
                        break;
                    case 2:
                        btnState = ButtonState.LONGPRESS;
                        break;
                }
                if (ConnectionEvents.getOnIOsListener() != null)
                    ConnectionEvents.getOnIOsListener().OnButtonReceived(address, btnState);
                if (ConnectionEvents.getOnIOsListener() != null)
                    ConnectionEvents.getOnIOsListener().OnTemperatureReceived(address, BLEUtils.SignedBytesToInt(data[13]),
                            BLEUtils.SignedBytesToInt(data[14]),
                            BLEUtils.UnsignedBytesToInt(data[15]));
                if (ConnectionEvents.getOnIOsListener() != null)
                    ConnectionEvents.getOnIOsListener().OnLedsReceived(address, BLEUtils.UnsignedBytesToInt(data[16]),
                            BLEUtils.UnsignedBytesToInt(data[17]),
                            BLEUtils.UnsignedBytesToInt(data[18]));
            }
        } else if (p.equals(Properties.getInfo())) {
            if (ConnectionEvents.getOnInfoListener() != null) {
                TxPower power;
                switch (BLEUtils.SignedBytesToInt(data[2])) {
                    case -40:
                        power = TxPower.MINUS_40_DBM;
                        break;
                    case -20:
                        power = TxPower.MINUS_20_DBM;
                        break;
                    case -16:
                        power = TxPower.MINUS_16_DBM;
                        break;
                    case -12:
                        power = TxPower.MINUS_12_DBM;
                        break;
                    case -8:
                        power = TxPower.MINUS_8_DBM;
                        break;
                    case -4:
                        power = TxPower.MINUS_4_DBM;
                        break;
                    case 3:
                        power = TxPower.PLUS_3_DBM;
                        break;
                    case 4:
                        power = TxPower.PLUS_4_DBM;
                        break;
                    case 8:
                        power = TxPower.PLUS_8_DBM;
                        break;
                    default:
                        power = TxPower.ZERO_DBM;
                        break;
                }

                if (ConnectionEvents.getOnInfoListener() != null) {
                    ConnectionEvents.getOnInfoListener().OnInfoReceived(address,
                            BLEUtils.UnsignedBytesToInt(data[0]),
                            BLEUtils.UnsignedBytesToInt(data[1]),
                            power,
                            BLEUtils.UnsignedBytesToInt(data[9]),
                            BLEUtils.UnsignedBytesToInt(data[10]) + "." + String.format(Locale.US, "%.1f", BLEUtils.UnsignedBytesToInt(data[11]) / 10.),
                            BLEUtils.SignedBytesToInt(data[17]) == 1,
                            BLEUtils.SignedBytesToInt(data[18]),
                            data[19] == 1);
                }
                if (ConnectionEvents.getOnInfoListener() != null) {
                    ConnectionEvents.getOnInfoListener().OnAdvertisingReceived(address,
                            ByteToDoubleConversions.from20msTo10s(BLEUtils.UnsignedBytesToInt(data[3])),
                            ByteToDoubleConversions.from20msTo12days(BLEUtils.UnsignedBytesToInt(data[4])),
                            ByteToDoubleConversions.from20msTo12days(BLEUtils.UnsignedBytesToInt(data[5])));
                }
                if (ConnectionEvents.getOnInfoListener() != null) {
                    ConnectionEvents.getOnInfoListener().OnScanReceived(address,
                            BLEUtils.UnsignedBytesToInt(data[6]),
                            BLEUtils.UnsignedBytesToInt(data[7]));
                }
            }
        } else if (p.equals(Properties.getBlebricksList())) {
            if (ConnectionEvents.getOnBlebricksListListener() != null) {
                ConnectionEvents.getOnBlebricksListListener().OnBlebricksListReceived(address,
                        BLEUtils.UnsignedBytesToInt(data[0]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[1]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[2]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[3]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[4]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[5]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[6]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[7]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[8]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[9]) > 0);
            }
        }
    }
    //endregion
}

