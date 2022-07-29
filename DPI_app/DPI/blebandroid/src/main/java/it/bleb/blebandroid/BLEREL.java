/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 30/04/2019 11:44
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLEREL extends Component {
    BLEREL() {
        super("BLE-REL", "Bistable relay with additional I/O pins");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnDigitalInputsListener mOnDigitalInputsListener;
        private OnFeedbackListener mOnFeedbackListener;

        public OnDigitalInputsListener getOnDigitalInputsListener() {
            return mOnDigitalInputsListener;
        }

        public void setOnDigitalInputsListener(OnDigitalInputsListener onDigitalInputsListener) {
            mOnDigitalInputsListener = onDigitalInputsListener;
        }

        public OnFeedbackListener getOnFeedbackListener() {
            return mOnFeedbackListener;
        }

        public void setOnFeedbackListener(OnFeedbackListener onFeedbackListener) {
            mOnFeedbackListener = onFeedbackListener;
        }

        public interface OnDigitalInputsListener {
            void OnDigitalInputsReceived(final String address, final boolean DIN1high, final boolean DIN2high);
        }

        public interface OnFeedbackListener {
            void OnFeedbackReceived(final String address, final boolean relayClosedCircuit, final int DOUT1dutyCycle, final int DOUT2dutyCycle);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x1E: {
                setConnected(true);
                if (ScanEvents.getOnDigitalInputsListener() != null)
                    ScanEvents.getOnDigitalInputsListener().OnDigitalInputsReceived(address, BLEUtils.UnsignedBytesToInt(data[0]) > 0, BLEUtils.UnsignedBytesToInt(data[1]) > 0);
            }
            break;
            case 0x3B: {
                setConnected(true);
                if (ScanEvents.getOnFeedbackListener() != null)
                    ScanEvents.getOnFeedbackListener().OnFeedbackReceived(address, BLEUtils.UnsignedBytesToInt(data[0]) > 0, BLEUtils.UnsignedBytesToInt(data[1]), BLEUtils.UnsignedBytesToInt(data[2]));
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
         * Set the relay configuration
         * @param closedCircuit true if closed circuit, false if open circuit
         * @return The command
         */
        public Command SetRelayConfiguration(final boolean closedCircuit) {
            Command c = new Command("A1");

            String cmdValue = "";

            cmdValue += "00";
            cmdValue += closedCircuit ? "01" : "00";
            cmdValue += "00";
            cmdValue += "00";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the OUT1 configuration
         * @param dutyCycle the duty cycle, from 0 to 100
         * @return The command
         */
        public Command SetOUT1Configuration(final int dutyCycle) {
            Command c = new Command("A1");

            String cmdValue = "";

            cmdValue += "01";
            final int cleanedDutyCycle = dutyCycle < 0 ? 0 : (dutyCycle > 100 ? 100 : dutyCycle);
            cmdValue += BLEUtils.IntToHex(cleanedDutyCycle, 1);
            cmdValue += "00";
            cmdValue += "00";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the OUT2 configuration
         * @param dutyCycle the duty cycle, from 0 to 100
         * @return The command
         */
        public Command SetOUT2Configuration(final int dutyCycle) {
            Command c = new Command("A1");

            String cmdValue = "";

            cmdValue += "02";
            final int cleanedDutyCycle = dutyCycle < 0 ? 0 : (dutyCycle > 100 ? 100 : dutyCycle);
            cmdValue += BLEUtils.IntToHex(cleanedDutyCycle, 1);
            cmdValue += "00";
            cmdValue += "00";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the configuration of the inputs pin
         * @param readingEnabled Enable or disable the readings
         * @param readingInterval Set the reading interval (from 0.01s to 2.55s)
         * @return The command
         */
        public Command SetInputsConfiguration(final boolean readingEnabled, final double readingInterval) {
            Command c = new Command("A1");

            String cmdValue = "";

            cmdValue += "03";
            if (readingEnabled)
                cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from10msTo2550ms(readingInterval), 1);
            else
                cmdValue += "00";
            cmdValue += "00";
            cmdValue += "00";

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set relay toggling configuration
         * @param initialStatusClosedCircuit Set the initial state (true if closed circuit, false otherwise)
         * @param closedCircuitInterval Set the interval of the closed circuit mode (0-255)
         * @param openCircuitInterval Set the interval of the closed circuit mode (0-255)
         * @return The command
         */
        public Command SetRelayTogglingConfiguration(final boolean initialStatusClosedCircuit, final int closedCircuitInterval, final int openCircuitInterval) {
            Command c = new Command("A1");

            String cmdValue = "";

            cmdValue += "F0";
            cmdValue += initialStatusClosedCircuit ? "01" : "00";
            cmdValue += BLEUtils.IntToHex(closedCircuitInterval, 1);
            cmdValue += BLEUtils.IntToHex(openCircuitInterval, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set OUT1 toggling configuration
         * @param initialStatusHighOutput Set the initial state (true if high output, false otherwise)
         * @param highOutputInterval Set the interval of the high output mode (0-255)
         * @param lowOutputInterval Set the interval of the low output mode (0-255)
         * @return The command
         */
        public Command SetOUT1TogglingConfiguration(final boolean initialStatusHighOutput, final int highOutputInterval, final int lowOutputInterval) {
            Command c = new Command("A1");

            String cmdValue = "";

            cmdValue += "F1";
            cmdValue += initialStatusHighOutput ? "01" : "00";
            cmdValue += BLEUtils.IntToHex(highOutputInterval, 1);
            cmdValue += BLEUtils.IntToHex(lowOutputInterval, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set OUT2 toggling configuration
         * @param initialStatusHighOutput Set the initial state (true if high output, false otherwise)
         * @param highOutputInterval Set the interval of the high output mode (0-255)
         * @param lowOutputInterval Set the interval of the low output mode (0-255)
         * @return The command
         */
        public Command SetOUT2TogglingConfiguration(final boolean initialStatusHighOutput, final int highOutputInterval, final int lowOutputInterval) {
            Command c = new Command("A1");

            String cmdValue = "";

            cmdValue += "F2";
            cmdValue += initialStatusHighOutput ? "01" : "00";
            cmdValue += BLEUtils.IntToHex(highOutputInterval, 1);
            cmdValue += BLEUtils.IntToHex(lowOutputInterval, 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB3B-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final boolean relayClosedCircuit, final int DOUT1dutyCycle, final int DOUT2dutyCycle, final boolean DIN1high, final boolean DIN2high, final double readingInterval);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(
                        address,
                        BLEUtils.UnsignedBytesToInt(data[0]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[1]),
                        BLEUtils.UnsignedBytesToInt(data[2]),
                        BLEUtils.UnsignedBytesToInt(data[3]) > 0,
                        BLEUtils.UnsignedBytesToInt(data[4]) > 0,
                        ByteToDoubleConversions.from10msTo2550ms(BLEUtils.UnsignedBytesToInt(data[5]))
                );
            }
        }
    }
    //endregion
}
