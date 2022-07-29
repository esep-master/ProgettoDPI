/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 20/11/2019 08:19
 */

package it.bleb.blebandroid;

import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.Property;

public class BLECO2 extends Component {
    BLECO2() {
        super("BLE-CO2", "CO2 optical detector");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnCo2ValueListener mOnCo2ValueListener;

        public OnCo2ValueListener getOnCo2ValueListener() {
            return mOnCo2ValueListener;
        }

        public void setOnCo2ValueListener(OnCo2ValueListener mOnCo2ValueListener) {
            this.mOnCo2ValueListener = mOnCo2ValueListener;
        }

        public interface OnCo2ValueListener {
            void OnCo2ValueReceived(final String address, final int co2);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x28: {
                setConnected(true);
                if (ScanEvents.getOnCo2ValueListener() != null) {
                    ScanEvents.getOnCo2ValueListener().OnCo2ValueReceived(address, BLEUtils.UnsignedBytesToInt(data[0], data[1]));
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
            Command c = new Command("25");

            int time = ms / 100;

            String cmdValue = "";
            if (time < 10)
                cmdValue += "0A";
            else if (time > 252)
                cmdValue += "FC";
            else
                cmdValue += BLEUtils.IntToHex(time, 1);
            c.setValue(cmdValue);
            return c;
        }

        public Command SetSamplingRate1Minute() {
            Command c = new Command("25");

            String cmdValue = "";
            cmdValue += "FD";
            c.setValue(cmdValue);
            return c;
        }

        public Command SetSamplingRate5Minute() {
            Command c = new Command("25");

            String cmdValue = "";
            cmdValue += "FE";
            c.setValue(cmdValue);
            return c;
        }

        public Command SetSamplingRate10Minute() {
            Command c = new Command("25");

            String cmdValue = "";
            cmdValue += "FF";
            c.setValue(cmdValue);
            return c;
        }


        public Command Calibrate(int offset) {
            Command c = new Command("26");

            int sOffset = offset / 10;

            String cmdValue = "";
            cmdValue += "00";
            cmdValue += BLEUtils.IntToHex(sOffset, 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB28-30B8-4A6B-913E-0EF628448151");

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

        private ConnectionEvents.OnMainValuesListener mOnMainValuesListener;

        public ConnectionEvents.OnMainValuesListener getOnMainValuesListener() {
            return mOnMainValuesListener;
        }

        public void setOnMainValuesListener(ConnectionEvents.OnMainValuesListener onMainValuesListener) {
            mOnMainValuesListener = onMainValuesListener;
        }

        public interface OnMainValuesListener {
            void OnMainValuesReceived(final String address, final int co2, final int readingInterval, final String firmwareVersion, final int offset);
        }
    }


    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address,
                        BLEUtils.UnsignedBytesToInt(data[0], data[1]),
                        BLEUtils.UnsignedBytesToInt(data[2]) * 100,
                        String.format(Locale.US, "%.1f", BLEUtils.UnsignedBytesToInt(data[3]) / 10d),
                        BLEUtils.SignedBytesToInt(data[4]) * 10
                );
            }
        }
    }
}