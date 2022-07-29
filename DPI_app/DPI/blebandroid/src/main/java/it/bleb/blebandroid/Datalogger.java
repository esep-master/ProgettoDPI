/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2020. All rights reserved.
 * https://bleb.it
 *
 * Last modified 06/08/2019 13:46
 */

package it.bleb.blebandroid;

import android.util.Pair;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.EnumHexValue;
import it.bleb.blebandroid.utils.ValuesContainer;

public class Datalogger extends Component {
    private boolean mIsDataloggerEnabled = false;

    public boolean isDataloggerEnabled() {
        return mIsDataloggerEnabled;
    }

    @Override
    public void setConnected(boolean connected) {
        super.setConnected(connected);
        if (!connected)
            mIsDataloggerEnabled = false;
    }

    Datalogger() {
        super("Datalogger", "Saves a log inside the memory of your BLE-B");
    }


    public enum DataloggerStatusEnum {
        OFF, ON, FULL
    }

    public static ValuesContainer<DataloggerStatusEnum> DataloggerStatus = new ValuesContainer<>(
            new EnumHexValue<>(DataloggerStatusEnum.OFF, "OFF", 0x00),
            new EnumHexValue<>(DataloggerStatusEnum.ON, "ON", 0x01),
            new EnumHexValue<>(DataloggerStatusEnum.FULL, "Memory Full", 0x02)
    );

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnDataloggerStatusListener mOnDataloggerStatusListener;

        public OnDataloggerStatusListener getOnDataloggerStatusListener() {
            return mOnDataloggerStatusListener;
        }

        public void setOnDataloggerStatusListener(OnDataloggerStatusListener onDataloggerStatusListener) {
            this.mOnDataloggerStatusListener = onDataloggerStatusListener;
        }

        public interface OnDataloggerStatusListener {
            void OnDataloggerStatusReceived(final String address, final DataloggerStatusEnum status, final int samplingInterval, final BLEUtils.PeriodUnitEnum samplingIntervalUnit, final int timestamp);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x94: {
                setConnected(true);
                mIsDataloggerEnabled = true;

                if (ScanEvents.getOnDataloggerStatusListener() != null) {
                    DataloggerStatusEnum status = DataloggerStatus.getByValue(BLEUtils.UnsignedBytesToInt(data[0])).getEnum();
                    Pair<Integer, BLEUtils.PeriodUnitEnum> samplingIntervalDecoded = BLEUtils.DecodePeriod(BLEUtils.UnsignedBytesToInt(data[1]));
                    int timestamp = BLEUtils.UnsignedBytesToInt(data[2], data[3], data[4], data[5]);

                    ScanEvents.getOnDataloggerStatusListener().OnDataloggerStatusReceived(address, status, samplingIntervalDecoded.first, samplingIntervalDecoded.second, timestamp);
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
         * Start datalogging
         *
         * @param samplingInterval     Sampling interval in seconds/minutes/hours. Seconds and minutes are from 1 to 59, hours are from 1 to 12.
         * @param samplingIntervalUnit The unit of the sampling interval (seconds/minutes/hours).
         * @return The command
         */
        public Command StartDatalogger(final int samplingInterval, final BLEUtils.PeriodUnitEnum samplingIntervalUnit) {
            Command c = new Command("AE");

            String cmdValue = "";

            final int codedSamplingInterval = BLEUtils.GetCodedPeriod(samplingInterval, samplingIntervalUnit);

            cmdValue += BLEUtils.IntToHex(codedSamplingInterval, 1);
            long timestamp = System.currentTimeMillis() / 1000;
            cmdValue += BLEUtils.IntToHex((int) timestamp, 4);

            c.setValue(cmdValue);
            return c;
        }

        /**
         * Stop datalogging
         *
         * @param erase If true, clear the memory
         * @return The command
         */
        public Command StopDatalogger(final boolean erase) {
            Command c = new Command("AF");

            String cmdValue = "";
            cmdValue += erase ? "01" : "00";
            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {

    }
}
