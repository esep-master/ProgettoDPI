/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2020. All rights reserved.
 * https://bleb.it
 *
 * Last modified 29/01/2020 14:02
 */

package it.bleb.blebandroid;

import java.util.Locale;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.EnumHexValue;
import it.bleb.blebandroid.utils.Property;
import it.bleb.blebandroid.utils.ValuesContainer;

public class BLEGPS extends Component {
    BLEGPS() {
        super("BLE-GPS", "GPS Module");
    }

    public enum GeoFenceStatusEnum {
        DISABLED, INSIDE, OUTSIDE
    }

    public enum GeoFenceModeEnum {
        DISABLE, ENABLE
    }

    public enum FixEnum {
        NONE, GNSS, DGPS, ESTIMATED
    }

    public enum PowerModeEnum {
        OFF, FULL_ON, ALWAYS_LOCATE, PERIODIC
    }

    public static ValuesContainer<GeoFenceStatusEnum> GeoFenceStatus = new ValuesContainer<>(
            new EnumHexValue<>(GeoFenceStatusEnum.DISABLED, "Disabled", 0x00),
            new EnumHexValue<>(GeoFenceStatusEnum.INSIDE, "Inside", 0x01),
            new EnumHexValue<>(GeoFenceStatusEnum.OUTSIDE, "Outside", 0x02)
    );

    public static ValuesContainer<GeoFenceModeEnum> GeoFenceMode = new ValuesContainer<>(
            new EnumHexValue<>(GeoFenceModeEnum.DISABLE, "Disable Geo-fence", 0x00),
            new EnumHexValue<>(GeoFenceModeEnum.ENABLE, "Enable Geo-fence", 0x01)
    );

    public static ValuesContainer<FixEnum> Fix = new ValuesContainer<>(
            new EnumHexValue<>(FixEnum.NONE, "None", 0x00),
            new EnumHexValue<>(FixEnum.GNSS, "GNSS", 0x01),
            new EnumHexValue<>(FixEnum.DGPS, "DGPS", 0x02),
            new EnumHexValue<>(FixEnum.ESTIMATED, "Estimated", 0x06)
    );

    public static ValuesContainer<PowerModeEnum> PowerMode = new ValuesContainer<>(
            new EnumHexValue<>(PowerModeEnum.OFF, "OFF mode", 0x00),
            new EnumHexValue<>(PowerModeEnum.FULL_ON, "Full ON mode", 0x01),
            new EnumHexValue<>(PowerModeEnum.ALWAYS_LOCATE, "AlwaysLocate™ mode", 0x02),
            new EnumHexValue<>(PowerModeEnum.PERIODIC, "Periodic mode", 0x03)
    );

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnNotReadyReceivedListener mOnNotReadyReceivedListener;
        private OnBaseReceivedListener mOnBaseReceivedListener;
        private OnAdditionalReceivedListener mOnAdditionalReceivedListener;

        public OnNotReadyReceivedListener getOnNotReadyReceivedListener() {
            return mOnNotReadyReceivedListener;
        }

        public void setOnNotReadyReceivedListener(OnNotReadyReceivedListener onNotReadyReceivedListener) {
            mOnNotReadyReceivedListener = onNotReadyReceivedListener;
        }

        public OnBaseReceivedListener getOnBaseReceivedListener() {
            return mOnBaseReceivedListener;
        }

        public void setOnBaseReceivedListener(OnBaseReceivedListener onBaseReceivedListener) {
            mOnBaseReceivedListener = onBaseReceivedListener;
        }

        public OnAdditionalReceivedListener getOnAdditionalReceivedListener() {
            return mOnAdditionalReceivedListener;
        }

        public void setOnAdditionalReceivedListener(OnAdditionalReceivedListener onAdditionalReceivedListener) {
            mOnAdditionalReceivedListener = onAdditionalReceivedListener;
        }

        public interface OnNotReadyReceivedListener {
            void OnNotReadyReceived(final String address);
        }

        public interface OnBaseReceivedListener {
            void OnBaseReceivedReceived(final String address, final double latitude, final double longitude);
        }

        public interface OnAdditionalReceivedListener {
            void OnAdditionalReceived(final String address, final int altitude, final double speed, final double orientation, final FixEnum fix, final GeoFenceStatusEnum geofence);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0xE3: {
                setConnected(true);
                if (ScanEvents.getOnBaseReceivedListener() != null) {
                    boolean allZero = true;
                    for (byte b : data) {
                        if (b != 0) {
                            allZero = false;
                            break;
                        }
                    }

                    if (allZero) {
                        if (ScanEvents.getOnNotReadyReceivedListener() != null)
                            ScanEvents.getOnNotReadyReceivedListener().OnNotReadyReceived(address);
                    } else {
                        String latStr = BLEUtils.BytesToCoordinate(data[0], data[1], data[2], data[3]);
                        String lngStr = BLEUtils.BytesToCoordinate(data[4], data[5], data[6], data[7], (byte)(data[8] & 0xf0));

                        double lat;
                        double lng;

                        try {
                            lat = Double.parseDouble(latStr);
                        } catch (NumberFormatException ignore) {
                            lat = 0;
                        }

                        try {
                            lng = Double.parseDouble(lngStr);
                        } catch (NumberFormatException ignore) {
                            lng = 0;
                        }

                        byte polarityBit = (byte)(data[8] & 0x0f);
                        boolean north = BLEUtils.GetBit(polarityBit, 0);
                        boolean west = BLEUtils.GetBit(polarityBit, 1);

                        ScanEvents.getOnBaseReceivedListener().OnBaseReceivedReceived(address, north ? lat : -lat, west ? -lng : lng);
                    }
                }
            }
            break;
            case 0xE4: {
                setConnected(true);
                if (ScanEvents.getOnAdditionalReceivedListener() != null) {
                    boolean allZero = true;
                    for (byte b : data) {
                        if (b != 0) {
                            allZero = false;
                            break;
                        }
                    }

                    if (allZero) {
                        if (ScanEvents.getOnNotReadyReceivedListener() != null)
                            ScanEvents.getOnNotReadyReceivedListener().OnNotReadyReceived(address);
                    } else {
                        int altitude = BLEUtils.UnsignedBytesToInt(data[0], data[1]);
                        double speed;
                        try {
                            speed = Double.parseDouble(BLEUtils.UnsignedBytesToInt(data[2]) + "." + BLEUtils.UnsignedBytesToInt(data[3]));
                        } catch (NumberFormatException ignore) {
                            speed = 0;
                        }
                        double orientation;
                        try {
                            orientation = Double.parseDouble(BLEUtils.UnsignedBytesToInt(data[4], data[5]) + "." + BLEUtils.UnsignedBytesToInt(data[6]));
                        } catch (NumberFormatException ignore) {
                            orientation = 0;
                        }

                        EnumHexValue<FixEnum> fixEnumHexValue = Fix.getByValue(BLEUtils.UnsignedBytesToInt(data[7]));
                        EnumHexValue<GeoFenceStatusEnum> geofenceEnumHexValue = GeoFenceStatus.getByValue(BLEUtils.UnsignedBytesToInt(data[8]));

                        FixEnum fix = fixEnumHexValue != null ? fixEnumHexValue.getEnum() : FixEnum.NONE;
                        GeoFenceStatusEnum geofence = geofenceEnumHexValue != null ? geofenceEnumHexValue.getEnum() : GeoFenceStatusEnum.DISABLED;

                        ScanEvents.getOnAdditionalReceivedListener().OnAdditionalReceived(address, altitude, speed, orientation, fix, geofence);
                    }
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
         * Enable receiving additional data
         *
         * @param on If true, returns the additional data too
         * @return The command
         */
        public Command SetShowCompleteData(final boolean on) {
            Command c = new Command("B0");

            String cmdValue = "";
            cmdValue += on ? "01" : "00";
            c.setValue(cmdValue);
            return c;
        }

        /**
         * @param powerMode
         * @param periodOn
         * @param periodOnUnit
         * @param periodOff
         * @param periodOffUnit
         * @return
         */
        public Command SetPowerMode(final PowerModeEnum powerMode, final int periodOn, final BLEUtils.PeriodUnitEnum periodOnUnit, final int periodOff, final BLEUtils.PeriodUnitEnum periodOffUnit) {
            Command c = new Command("B1");

            String cmdValue = "";
            cmdValue += BLEUtils.IntToHex(PowerMode.getByEnum(powerMode).getValue(), 1);
            if (powerMode == PowerModeEnum.PERIODIC) {
                int periodOnCoded = BLEUtils.GetCodedPeriod(periodOn, periodOnUnit);
                int periodOffCoded = BLEUtils.GetCodedPeriod(periodOff, periodOffUnit);
                cmdValue += BLEUtils.IntToHex(periodOnCoded, 1);
                cmdValue += BLEUtils.IntToHex(periodOffCoded, 1);
            } else
                cmdValue += "0000";

            c.setValue(cmdValue);
            return c;
        }


        private String formatCoordinate(double coordinate, int integers, int decimals) {
            String coordinateS = String.format(Locale.US, "%." + decimals + "f", coordinate);
            int dotIndex = coordinateS.indexOf(".");

            String integer = coordinateS.substring(0, dotIndex);
            while (integer.length() < integers)
                integer = "0" + integer;
            while (integer.length() > integers)
                integer = integer.substring(1);

            String decimal = coordinateS.substring(dotIndex + 1);
            while (decimal.length() < decimals)
                decimal = decimal + "0";
            while (decimal.length() > decimals)
                decimal = decimal.substring(0, decimal.length() - 1);

            return integer + "f" + decimal;
        }

        /**
         * Set the geofence
         *
         * @param mode      Mode
         * @param latitude  Latitude
         * @param longitude Longitude
         * @param range     Range in meters (will be approximated to units of 100 meters!)
         * @return
         */
        public Command SetGeofence(GeoFenceModeEnum mode, double latitude, double longitude, final int range) {
            Command c = new Command("B2");

            String cmdValue = "";

            int nw = 0;
            if (latitude >= 0)
                nw += 1;
            else
                latitude *= -1;
            if (longitude < 0) {
                nw += 2;
                longitude *= -1;
            }

            cmdValue += BLEUtils.IntToHex(GeoFenceMode.getByEnum(mode).getValue(), 1);
            cmdValue += formatCoordinate(latitude, 2, 5);
            cmdValue += formatCoordinate(longitude, 3, 5) + String.format(Locale.US, "%d", nw);
            cmdValue += BLEUtils.IntToHex(range > 100 ? range / 100 : 1, 2);

            c.setValue(cmdValue);
            return c;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BBE2-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, GeoFenceModeEnum geofenceMode, double latitude, double longitude, int radius, GeoFenceStatusEnum geofenceStatus, boolean completeData);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                GeoFenceModeEnum mode = GeoFenceMode.getByValue(BLEUtils.UnsignedBytesToInt(data[0])).getEnum();

                String latStr = BLEUtils.BytesToCoordinate(data[1], data[2], data[3], data[4]);
                String lngStr = BLEUtils.BytesToCoordinate(data[5], data[6], data[7], data[8], (byte)(data[9] & 0xf0));

                double lat;
                double lng;

                try {
                    lat = Double.parseDouble(latStr);
                } catch (NumberFormatException ignore) {
                    lat = 0;
                }

                try {
                    lng = Double.parseDouble(lngStr);
                } catch (NumberFormatException ignore) {
                    lng = 0;
                }

                byte polarityBit = (byte)(data[9] & 0x0f);
                boolean north = BLEUtils.GetBit(polarityBit, 0);
                boolean west = BLEUtils.GetBit(polarityBit, 1);

                int radius = BLEUtils.UnsignedBytesToInt(data[10], data[11]);

                GeoFenceStatusEnum status = GeoFenceStatus.getByValue(BLEUtils.UnsignedBytesToInt(data[12])).getEnum();

                boolean completeData = BLEUtils.UnsignedBytesToInt(data[13]) == 1;

                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(address, mode, north ? lat : -lat, west ? -lng : lng, radius, status, completeData);
            }
        }
    }
    //endregion
}
