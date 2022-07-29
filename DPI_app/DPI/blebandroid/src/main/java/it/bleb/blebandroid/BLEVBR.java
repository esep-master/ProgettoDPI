/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 14/05/2019 10:37
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.EnumHexValue;
import it.bleb.blebandroid.utils.ValuesContainer;

public class BLEVBR extends Component {
    BLEVBR() {
        super("BLE-VBR", "Brick with a small motor that make vibrations");
    }

    public enum MotorCommandEnum {
        CONTINUOUS_VIBRATION, SHORT_VIBRATION, TWO_SHORT_VIBRATION, THREE_SHORT_VIBRATION, LONG_VIBRATION, TWO_LONG_VIBRATION
    }

    public static ValuesContainer<MotorCommandEnum> MotorCommand = new ValuesContainer<>(
            new EnumHexValue<>(MotorCommandEnum.CONTINUOUS_VIBRATION, "Continuous Vibration", 0x00),
            new EnumHexValue<>(MotorCommandEnum.SHORT_VIBRATION, "Short Vibration", 0x01),
            new EnumHexValue<>(MotorCommandEnum.TWO_SHORT_VIBRATION, "Two Short Vibration", 0x02),
            new EnumHexValue<>(MotorCommandEnum.THREE_SHORT_VIBRATION, "Three Short Vibration", 0x03),
            new EnumHexValue<>(MotorCommandEnum.LONG_VIBRATION, "Long Vibration", 0x04),
            new EnumHexValue<>(MotorCommandEnum.TWO_LONG_VIBRATION, "Two Long Vibration", 0x05)
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
            void OnStatusReceived(final String address, final int motorDutyCycle);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x0E: {
                setConnected(true);
                if (ScanEvents.getOnStatusListener() != null)
                    ScanEvents.getOnStatusListener().OnStatusReceived(address, BLEUtils.UnsignedBytesToInt(data[0]));
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
         * Set the motor current action
         * @param motorCommand the specific action that you want to be executed
         * @param intensity the intensity of the vibration (from 0 to 100)
         * @return The command
         */
        public Command SetMotorAction(final MotorCommandEnum motorCommand, final int intensity) {
            Command c = new Command("1E");

            String cmdValue = "";

            cmdValue += BLEUtils.IntToHex(MotorCommand.getByEnum(motorCommand).getValue(), 1);

            int sanitizedIntensity = intensity;
            if(sanitizedIntensity > 100)
                sanitizedIntensity = 100;
            if(sanitizedIntensity < 0)
                sanitizedIntensity = 0;
            cmdValue += BLEUtils.IntToHex(sanitizedIntensity, 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
    }
}
