/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2020. All rights reserved.
 * https://bleb.it
 *
 * Last modified 25/06/2019 17:36
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;

public class BLEPMX extends Component {
    BLEPMX() {
        super("BLE-PMX", "PM2.5 and PM10 sensor");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnPMDensityListener mOnPMDensityListener;

        public OnPMDensityListener getOnPMDensityListener() {
            return mOnPMDensityListener;
        }

        public void setOnPMDensityListener(OnPMDensityListener onPMDensityListener) {
            mOnPMDensityListener = onPMDensityListener;
        }

        public interface OnPMDensityListener {
            void OnPMDensityReceived(final String address, final double pm25density, final double pm10density);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x56: {
                setConnected(true);

                double pm25density = BLEUtils.UnsignedBytesToInt(data[0], data[1]) / 10.0;
                double pm10density = BLEUtils.UnsignedBytesToInt(data[2], data[3]) / 10.0;
                if (ScanEvents.getOnPMDensityListener() != null)
                    ScanEvents.getOnPMDensityListener().OnPMDensityReceived(address, pm25density, pm10density);
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

        public Command SetConfiguration(final boolean on, int odr) {
            Command c = new Command("27");
            String cmdValue = "";
            cmdValue += on ? "01" : "00";
            cmdValue += BLEUtils.IntToHex(odr, 1);
            c.setValue(cmdValue);
            return c;
        }

    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
    }
    //endregion

    //region ConnectionEvents read
    public final ConnectionEvents ConnectionEvents = new ConnectionEvents();

    public static class ConnectionEvents {
        private ConnectionEvents() {
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {

    }
    //endregion
}
