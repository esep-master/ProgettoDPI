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

public class BLERGB extends Component {
    BLERGB() {
        super("BLE-RGB", "Light sensor with colors detection");
    }

    public enum IntegrationTime {
        TIME_40_MS, TIME_80_MS, TIME_160_MS, TIME_320_MS, TIME_640_MS, TIME_1280_MS, UNCHANGED
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnLightIntensityListener mOnLightIntensityListener;

        public OnLightIntensityListener getOnLightIntensityListener() {
            return mOnLightIntensityListener;
        }

        public void setOnLightIntensityListener(OnLightIntensityListener onLightIntensityListener) {
            mOnLightIntensityListener = onLightIntensityListener;
        }

        public interface OnLightIntensityListener {
            void OnLightIntensityReceived(final String address, final int red, final int green, final int blue, final int white);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xC6: {
                setConnected(true);
                if (ScanEvents.getOnLightIntensityListener() != null)
                    ScanEvents.getOnLightIntensityListener().OnLightIntensityReceived(address, BLEUtils.UnsignedBytesToInt(data[0], data[1]), BLEUtils.UnsignedBytesToInt(data[2], data[3]), BLEUtils.UnsignedBytesToInt(data[4], data[5]), BLEUtils.UnsignedBytesToInt(data[6], data[7]));
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
         * Set the configuration of the RGB module
         * @param integrationTime The integration time
         * @param readingInterval The reading interval (from 0.1s to 1036800s)
         * @return The command
         */
        public Command SetConfiguration(final IntegrationTime integrationTime, final double readingInterval) {
            Command c = new Command("11");

            String cmdValue = "";

            switch (integrationTime) {
                default:
                    cmdValue += "00";
                    break;
                case TIME_40_MS:
                    cmdValue += "01";
                    break;
                case TIME_80_MS:
                    cmdValue += "02";
                    break;
                case TIME_160_MS:
                    cmdValue += "03";
                    break;
                case TIME_320_MS:
                    cmdValue += "04";
                    break;
                case TIME_640_MS:
                    cmdValue += "05";
                    break;
                case TIME_1280_MS:
                    cmdValue += "06";
                    break;
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
        private final Property mMainValues = new Property("CEE2BBC6-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final int red, final int green, final int blue, final int white, final IntegrationTime integrationInterval, final double readingInterval);
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
                switch(data[8]) {
                    default: intTime = IntegrationTime.UNCHANGED; break;
                    case 1: intTime = IntegrationTime.TIME_40_MS; break;
                    case 2: intTime = IntegrationTime.TIME_80_MS; break;
                    case 3: intTime = IntegrationTime.TIME_160_MS; break;
                    case 4: intTime = IntegrationTime.TIME_320_MS; break;
                    case 5: intTime = IntegrationTime.TIME_640_MS; break;
                    case 6: intTime = IntegrationTime.TIME_1280_MS; break;
                }

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(
                        address,
                        BLEUtils.UnsignedBytesToInt(data[0], data[1]),
                        BLEUtils.UnsignedBytesToInt(data[2], data[3]),
                        BLEUtils.UnsignedBytesToInt(data[4], data[5]),
                        BLEUtils.UnsignedBytesToInt(data[6], data[7]),
                        intTime,
                        ByteToDoubleConversions.from100msTo12days(BLEUtils.UnsignedBytesToInt(data[9]))
                );
            }
        }
    }
    //endregion
}
