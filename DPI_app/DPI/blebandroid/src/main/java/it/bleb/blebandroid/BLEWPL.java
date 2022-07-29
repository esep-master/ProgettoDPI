/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 14/05/2019 10:51
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.EnumHexValue;
import it.bleb.blebandroid.utils.ValuesContainer;

public class BLEWPL extends Component {
    BLEWPL() {
        super("BLE-WPL", "White dimmable high-power LED");
    }

    public enum LedStatusEnum {
        OFF, ON
    }

    public static ValuesContainer<LedStatusEnum> LedStatus = new ValuesContainer<>(
            new EnumHexValue<>(LedStatusEnum.OFF, "OFF", 0x00),
            new EnumHexValue<>(LedStatusEnum.ON, "ON", 0x01)
    );

    public enum LedCommandEnum {
        OFF, ON, SHORT_BLINK, TWO_SHORT_BLINK, LONG_BLINK, TWO_LONG_BLINK
    }

    public static ValuesContainer<LedCommandEnum> LedCommand = new ValuesContainer<>(
            new EnumHexValue<>(LedCommandEnum.OFF, "OFF", 0x00),
            new EnumHexValue<>(LedCommandEnum.ON, "ON", 0x01),
            new EnumHexValue<>(LedCommandEnum.SHORT_BLINK, "Short Blink", 0x02),
            new EnumHexValue<>(LedCommandEnum.TWO_SHORT_BLINK, "Two Short Blink", 0x03),
            new EnumHexValue<>(LedCommandEnum.LONG_BLINK, "Long Blink", 0x04),
            new EnumHexValue<>(LedCommandEnum.TWO_LONG_BLINK, "Two Long Blink", 0x05)
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
            void OnStatusReceived(final String address, final LedStatusEnum ledStatus, final int intensity);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x21: {
                setConnected(true);
                if (ScanEvents.getOnStatusListener() != null)
                    ScanEvents.getOnStatusListener().OnStatusReceived(address, LedStatus.getByValue(BLEUtils.UnsignedBytesToInt(data[0])).getEnum(), BLEUtils.UnsignedBytesToInt(data[1]));
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
         * Set the LED current action
         * @param ledCommand the specific action that you want to be executed
         * @param intensity the intensity of the vibration (from 0 to 100)
         * @return The command
         */
        public Command SetLedAction(final LedCommandEnum ledCommand, final int intensity) {
            Command c = new Command("20");

            String cmdValue = "";

            cmdValue += BLEUtils.IntToHex(LedCommand.getByEnum(ledCommand).getValue(), 1);

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
