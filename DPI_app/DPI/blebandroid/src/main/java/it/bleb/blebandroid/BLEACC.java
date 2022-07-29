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

public class BLEACC extends Component {
    BLEACC() {
        super("BLE-ACC", "Low-energy accelerometer");
    }

    public enum Sensitivity {
        HIGH, MEDIUM, LOW
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnAccelerationListener mOnAccelerationListener;

        public OnAccelerationListener getOnAccelerationListener() {
            return mOnAccelerationListener;
        }

        public void setOnAccelerationListener(OnAccelerationListener onAccelerationListener) {
            mOnAccelerationListener = onAccelerationListener;
        }

        public interface OnAccelerationListener {
            void OnAccelerationReceived(final String address, final double x, final double y, final double z, final boolean shockAlert, final boolean orientationAlert, final boolean orientationAlertEnabled, final boolean wakeupAlert);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xA9: {
                setConnected(true);
                if (ScanEvents.getOnAccelerationListener() != null) {
                    boolean shockAlert = BLEUtils.GetBit(data[6], 3);
                    boolean orientationAlert = BLEUtils.GetBit(data[6], 2);
                    boolean orientationAlertEnabled = BLEUtils.GetBit(data[6], 1);
                    boolean wakeupAlert = BLEUtils.GetBit(data[6], 0);

                    ScanEvents.getOnAccelerationListener().OnAccelerationReceived(address, BLEUtils.SignedBytesToInt(data[0], data[1]) / 100d, BLEUtils.SignedBytesToInt(data[2], data[3]) / 100d, BLEUtils.SignedBytesToInt(data[4], data[5]) / 100d, shockAlert, orientationAlert, orientationAlertEnabled, wakeupAlert);
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

        public Command SetSensitivity(Sensitivity sensitivity) {
            Command c = new Command("30");

            String cmdValue = "";

            switch (sensitivity) {
                case HIGH:
                    cmdValue += "02000B";
                    break;
                case MEDIUM:
                    cmdValue += "320106";
                    break;
                case LOW:
                    cmdValue += "640305";
                    break;
            }


            c.setValue(cmdValue);
            return c;
        }

        public Command SetConfiguration(Integer inactivityInterval, Integer inactivityThreshold, Integer shockThreshold, Boolean orientationChangeInterruptEnabled, Integer alertDuration) {
            Command c = new Command("31");

            String cmdValue = "";

            cmdValue += inactivityInterval != null ? BLEUtils.IntToHex(inactivityInterval, 1) : "FF";
            cmdValue += inactivityThreshold != null ? BLEUtils.IntToHex(inactivityThreshold, 1) : "FF";
            cmdValue += shockThreshold != null ? BLEUtils.IntToHex(shockThreshold, 1) : "FF";
            cmdValue += orientationChangeInterruptEnabled != null ? BLEUtils.IntToHex(orientationChangeInterruptEnabled ? 1 : 0, 1) : "FF";
            cmdValue += alertDuration != null ? BLEUtils.IntToHex(alertDuration, 1) : "FF";

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mConfiguration = new Property("CEE2BBA9-30B8-4A6B-913E-0EF628448151");

        public Property getConfiguration() {
            return mConfiguration;
        }
    }
    //endregion

    //region ConnectionEvents read
    public final ConnectionEvents ConnectionEvents = new ConnectionEvents();

    public static class ConnectionEvents {
        private ConnectionEvents() {
        }

        private OnConfigurationValuesListener mOnConfigurationValuesListener;

        public OnConfigurationValuesListener getOnConfigurationValuesListener() {
            return mOnConfigurationValuesListener;
        }

        public void setOnConfigurationValuesListener(OnConfigurationValuesListener onConfigurationValuesListener) {
            mOnConfigurationValuesListener = onConfigurationValuesListener;
        }

        public interface OnConfigurationValuesListener {
            void OnConfigurationValuesReceived(final String address, final double x, final double y, final double z, final boolean shockAlert, final boolean orientationAlert, final boolean orientationAlertEnabled, final boolean wakeupAlert, final int range, final int outputDataRate, final Sensitivity sensitivity, final double samplingInterval, final int inactivityInterval, final int inactivityThreshold, final int shockThreshold, final int alertDuration, final boolean orientationChangeInterruptEnabled);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getConfiguration())) {
            if (ConnectionEvents.getOnConfigurationValuesListener() != null) {
                boolean shockAlert = BLEUtils.GetBit(data[6], 3);
                boolean orientationAlert = BLEUtils.GetBit(data[6], 2);
                boolean orientationAlertEnabled = BLEUtils.GetBit(data[6], 1);
                boolean wakeupAlert = BLEUtils.GetBit(data[6], 0);


                Sensitivity sensitivity;
                switch(BLEUtils.UnsignedBytesToInt(data[9])) {
                    case 0x02:
                        sensitivity = Sensitivity.HIGH;
                        break;
                    case 0x32:
                        sensitivity = Sensitivity.MEDIUM;
                        break;
                    default:
                        sensitivity = Sensitivity.LOW;
                        break;
                }

                ConnectionEvents.getOnConfigurationValuesListener().OnConfigurationValuesReceived(
                        address,
                        BLEUtils.SignedBytesToInt(data[0], data[1]) / 100d,
                        BLEUtils.SignedBytesToInt(data[2], data[3]) / 100d,
                        BLEUtils.SignedBytesToInt(data[4], data[5]) / 100d,
                        shockAlert,
                        orientationAlert,
                        orientationAlertEnabled,
                        wakeupAlert,
                        BLEUtils.UnsignedBytesToInt(data[7]),
                        BLEUtils.UnsignedBytesToInt(data[8]),
                        sensitivity,
                        ByteToDoubleConversions.from10msTo2550ms(BLEUtils.UnsignedBytesToInt(data[9])),
                        BLEUtils.UnsignedBytesToInt(data[10]),
                        BLEUtils.UnsignedBytesToInt(data[11]),
                        BLEUtils.UnsignedBytesToInt(data[12]),
                        BLEUtils.UnsignedBytesToInt(data[13]),
                        BLEUtils.UnsignedBytesToInt(data[14]) == 1
                        );
            }
        }
    }
    //endregion
}
