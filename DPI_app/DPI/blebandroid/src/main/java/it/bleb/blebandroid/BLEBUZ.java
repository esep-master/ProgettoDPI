/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 28/05/2019 16:17
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.EnumHexValue;
import it.bleb.blebandroid.utils.ValuesContainer;

public class BLEBUZ extends Component {
    BLEBUZ() {
        super("BLE-BUZ", "Buzzer that makes sounds");
    }

    public enum BuzzerCommandEnum {
        CONTINUOUS, ONE_SHORT_BEEP, TWO_SHORT_BEEP, THREE_SHORT_BEEP, ONE_LONG_BEEP, TWO_LONG_BEEP, THREE_LONG_BEEP
    }

    public static ValuesContainer<BuzzerCommandEnum> BuzzerCommand = new ValuesContainer<>(
            new EnumHexValue<>(BuzzerCommandEnum.CONTINUOUS, "Continuous", 0x00),
            new EnumHexValue<>(BuzzerCommandEnum.ONE_SHORT_BEEP, "One Short Beep", 0x01),
            new EnumHexValue<>(BuzzerCommandEnum.TWO_SHORT_BEEP, "Two Short Beep", 0x02),
            new EnumHexValue<>(BuzzerCommandEnum.THREE_SHORT_BEEP, "Three Short Beep", 0x03),
            new EnumHexValue<>(BuzzerCommandEnum.ONE_LONG_BEEP, "One Short Beep", 0x04),
            new EnumHexValue<>(BuzzerCommandEnum.TWO_LONG_BEEP, "Two Long Beep", 0x05),
            new EnumHexValue<>(BuzzerCommandEnum.THREE_LONG_BEEP, "Three Long Beep", 0x06)
    );

    public enum BuzzerLedEnum {
        RED, GREEN, OFF
    }

    public static ValuesContainer<BuzzerLedEnum> BuzzerLed = new ValuesContainer<>(
            new EnumHexValue<>(BuzzerLedEnum.RED, "Red", 0x01),
            new EnumHexValue<>(BuzzerLedEnum.GREEN, "Green", 0x02),
            new EnumHexValue<>(BuzzerLedEnum.OFF, "Off", 0x03)
    );

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnStatusListener mOnStatusListener;

        public OnStatusListener getOnStatusListener() {
            return mOnStatusListener;
        }

        public void setOnStatusListener(OnStatusListener onStatusListener) {
            mOnStatusListener = onStatusListener;
        }

        public interface OnStatusListener {
            void OnStatusReceived(final String address, final boolean isOn, final int frequency, final BuzzerLedEnum ledStatus);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x22: {
                setConnected(true);
                if (ScanEvents.getOnStatusListener() != null)
                    ScanEvents.getOnStatusListener().OnStatusReceived(address, BLEUtils.UnsignedBytesToInt(data[0]) > 0, BLEUtils.UnsignedBytesToInt(data[0]), BuzzerLed.getByValue(BLEUtils.UnsignedBytesToInt(data[1])).getEnum());
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
         * Set the buzzer current action
         * @param buzzerCommand the specific action that you want to be executed
         * @param frequency the frequency of the sound (0 = OFF, 1 = max freq to 100 = min freq)
         * @return The command
         */
        public Command SetBuzzerAction(final BuzzerCommandEnum buzzerCommand, final int frequency) {
            Command c = new Command("1C");

            String cmdValue = "";

            cmdValue += BLEUtils.IntToHex(BuzzerCommand.getByEnum(buzzerCommand).getValue(), 1);

            int sanitizedFrequency = frequency;
            if(sanitizedFrequency > 100)
                sanitizedFrequency = 100;
            if(sanitizedFrequency < 0)
                sanitizedFrequency = 0;
            cmdValue += BLEUtils.IntToHex(sanitizedFrequency, 1);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Set the led status
         * @param status Set the LED status
         * @return The command
         */
        public Command SetLedStatus(final BuzzerLedEnum status) {
            Command c = new Command("1D");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(BuzzerLed.getByEnum(status).getValue(), 1);

            c.setValue(cmdValue);
            return c;
        }

    }
    //endregion

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
    }
}
