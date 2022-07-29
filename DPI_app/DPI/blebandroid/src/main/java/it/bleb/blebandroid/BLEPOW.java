/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 20/11/2019 07:49
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;

public class BLEPOW extends Component {
    BLEPOW() {
        super("BLE-POW", "Powder optical detector");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnDustValueListener mOnDustValueListener;

        public OnDustValueListener getOnDustValueListener() {
            return mOnDustValueListener;
        }

        public void setOnDustValueListener(OnDustValueListener mOnDustValueListener) {
            this.mOnDustValueListener = mOnDustValueListener;
        }

        public interface OnDustValueListener {
            void OnDustValueReceived(final String address, final int powderDensity);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x27: {
                setConnected(true);
                if (ScanEvents.getOnDustValueListener() != null) {
                    ScanEvents.getOnDustValueListener().OnDustValueReceived(address, BLEUtils.UnsignedBytesToInt(data[0], data[1]));
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

        public Command SetSamplingRate(final int ms) {
            Command c = new Command("24");

            int time = ms / 100;

            String cmdValue = "";
            if (time < 1)
                cmdValue += "01";
            else if (time > 252)
                cmdValue += "FC";
            else
                cmdValue += BLEUtils.IntToHex(time, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetSamplingRate1Minute() {
            Command c = new Command("24");

            String cmdValue = "";
            cmdValue += "FD";
            c.setValue(cmdValue);
            return c;
        }
        public Command SetSamplingRate5Minute() {
            Command c = new Command("24");

            String cmdValue = "";
            cmdValue += "FE";
            c.setValue(cmdValue);
            return c;
        }
        public Command SetSamplingRate10Minute() {
            Command c = new Command("24");

            String cmdValue = "";
            cmdValue += "FF";
            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion


    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {

    }
}