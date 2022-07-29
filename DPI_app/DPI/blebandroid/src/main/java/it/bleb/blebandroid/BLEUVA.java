/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.ByteToDoubleConversions;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.Property;

public class BLEUVA extends Component {
    BLEUVA() {
        super("BLE-UVA", "UV-A light sensor");
    }

    public enum IntegrationTime {
        TIME_1_T, TIME_2_T, TIME_4_T, UNCHANGED
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnUVLightListener mOnUVLightListener;

        public OnUVLightListener getOnUVLightListener() {
            return mOnUVLightListener;
        }

        public void setOnUVLightListener(OnUVLightListener onUVLightListener) {
            mOnUVLightListener = onUVLightListener;
        }

        public interface OnUVLightListener {
            void OnUVLightReceived(final String address, final double uvLight);
        }

    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x1D: {
                setConnected(true);
                int digitalCounts = BLEUtils.UnsignedBytesToInt(data[0], data[1]);
                if (ScanEvents.getOnUVLightListener() != null)
                    ScanEvents.getOnUVLightListener().OnUVLightReceived(address, (digitalCounts / 186.81) * 5);
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
         * Set the configuration of the BLE-UVA module
         * @param integrationTime Set the sensitivity
         * @param readingInterval Set the reading interval (from 0.1s to 1036800s)
         * @return The command
         */
        public Command SetConfiguration(final IntegrationTime integrationTime, final double readingInterval) {
            Command c = new Command("10");

            String cmdValue = "";

            switch (integrationTime) {
                case TIME_1_T: cmdValue+= "02"; break;
                case TIME_2_T: cmdValue+= "03"; break;
                case TIME_4_T: cmdValue+= "04"; break;
                default: cmdValue += "00"; break;
            }

            cmdValue += BLEUtils.IntToHex(DoubleToByteConversions.from100msTo12days(readingInterval), 1);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB1D-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final int uvLight, final IntegrationTime integrationInterval, final double readingInterval);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {

                IntegrationTime intTime;
                switch(data[2]) {
                    case 2: intTime = IntegrationTime.TIME_1_T; break;
                    case 3: intTime = IntegrationTime.TIME_2_T; break;
                    case 4: intTime = IntegrationTime.TIME_4_T; break;
                    default: intTime = IntegrationTime.UNCHANGED; break;
                }

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(
                        address,
                        BLEUtils.UnsignedBytesToInt(data[0], data[1]),
                        intTime,
                        ByteToDoubleConversions.from100msTo12days(BLEUtils.UnsignedBytesToInt(data[3])));
            }
        }
    }
    //endregion
}
