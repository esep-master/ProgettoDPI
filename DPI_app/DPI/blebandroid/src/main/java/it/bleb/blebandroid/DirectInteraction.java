/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 28/05/2019 14:53
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Command;
import it.bleb.blebandroid.utils.DoubleToByteConversions;
import it.bleb.blebandroid.utils.EnumHexValue;
import it.bleb.blebandroid.utils.ValuesContainer;

public class DirectInteraction extends Component {
    public static ValuesContainer<DirectInteractionTriggerEnum> DirectInteractionTrigger = new ValuesContainer<>(
            new EnumHexValue<>(DirectInteractionTriggerEnum.ANALOG_INPUT_A, "Analog Input A", 0x0100),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ANALOG_INPUT_B, "Analog Input B", 0x0200),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_INPUTS, "Digital Inputs", 0x0300),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_OUTPUT_0, "Digital Output 0", 0x0400),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_OUTPUT_1, "Digital Output 1", 0x0500),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_OUTPUT_2, "Digital Output 2", 0x0600),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_OUTPUT_3, "Digital Output 3", 0x0700),
            new EnumHexValue<>(DirectInteractionTriggerEnum.MESSAGE, "Message", 0xFFFF),
            new EnumHexValue<>(DirectInteractionTriggerEnum.TEMPERATURE, "Temperature", 0x0800),
            new EnumHexValue<>(DirectInteractionTriggerEnum.SOIL_MOISTURE_FROM_SMS, "Soil Moisture from SMS", 0x0C00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.TOUCH_DETECTION_FROM_CAP, "Touch Detection from CAP", 0x0D00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.CAP_STATUS, "CAP Status from CAP", 0x0D00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.BUTTON, "Button", 0x1B00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.BATTERY, "Battery", 0x1C00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.UV_LIGHT_MSB_FROM_UVA, "UV Light MSB from UVA", 0x1D00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_INPUT_1_FROM_REL, "Digital Input 1 from REL", 0x1E00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_INPUT_2_FROM_REL, "Digital Input 2 from REL", 0x1E01),
            new EnumHexValue<>(DirectInteractionTriggerEnum.MUTUAL_CAPACITANCE_MSB_FROM_SMS, "Mutual Capacitance MSB from SMS", 0x1F00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.SELF_CAPACITANCE_MSB_FROM_SMS, "Self-Capacitance MSB From SMS", 0x1F01),
            new EnumHexValue<>(DirectInteractionTriggerEnum.BLUE_LED_BRIGHTNESS, "Blue LED brightness", 0x3900),
            new EnumHexValue<>(DirectInteractionTriggerEnum.GREEN_LED_BRIGHTNESS, "Green LED brightness", 0x3901),
            new EnumHexValue<>(DirectInteractionTriggerEnum.RED_LED_BRIGHTNESS, "Red LED brightness", 0x3902),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DISTANCE_MSB_FROM_PDM, "Distance MSB from PDM", 0x3A00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.RELAY_STATUS_FROM_REL, "Relay status from REL", 0x3B00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_OUTPUT_1_DUTY_CYCLE_FROM_REL, "Digital Output 1 duty-cycle from REL", 0x3B01),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DIGITAL_OUTPUT_2_DUTY_CYCLE_FROM_REL, "Digital Output 2 duty-cycle from REL", 0x3B02),
            new EnumHexValue<>(DirectInteractionTriggerEnum.HUMIDITY_MSB_FROM_RHT, "Humidity MSB from RHT", 0x5500),
            new EnumHexValue<>(DirectInteractionTriggerEnum.TEMPERATURE_MSB_FROM_RHT, "Temperature MSB from RHT", 0x5501),
            new EnumHexValue<>(DirectInteractionTriggerEnum.TEMPERATURE_MSB_FROM_PRT, "Temperature MSB from PRT", 0x7100),
            new EnumHexValue<>(DirectInteractionTriggerEnum.PRESSURE_MSB_FROM_PRT, "Pressure MSB from PRT", 0x7101),
            new EnumHexValue<>(DirectInteractionTriggerEnum.X_AXIS_ACCELERATION_MSB_FROM_IMU, "X-Axis Acceleration MSB from IMU", 0x8D00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Y_AXIS_ACCELERATION_MSB_FROM_IMU, "Y-Axis Acceleration MSB from IMU", 0x8D01),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Z_AXIS_ACCELERATION_MSB_FROM_IMU, "Z-Axis Acceleration MSB from IMU", 0x8D02),
            new EnumHexValue<>(DirectInteractionTriggerEnum.FREE_FALL_ACCELERATION_FROM_IMU, "Free fall from IMU", 0x8D03),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ACCELERATION_MSB_FROM_IMU, "Acceleration module MSB from IMU", 0x8D04),
            new EnumHexValue<>(DirectInteractionTriggerEnum.X_AXIS_MAGNETIC_FIELD_MSB_FROM_IMU, "X-Axis Magnetic Field MSB from IMU", 0x8E00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Y_AXIS_MAGNETIC_FIELD_MSB_FROM_IMU, "Y-Axis Magnetic Field MSB from IMU", 0x8E01),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Z_AXIS_MAGNETIC_FIELD_MSB_FROM_IMU, "Z-Axis Magnetic Field MSB from IMU", 0x8E02),
            new EnumHexValue<>(DirectInteractionTriggerEnum.MAGNETIC_FIELD_MSB_FROM_IMU, "Magnetic Field module MSB from IMU", 0x8E04),
            new EnumHexValue<>(DirectInteractionTriggerEnum.X_AXIS_ANGULAR_RATE_MSB_FROM_IMU, "X-Axis Angular Rate MSB from IMU", 0x8F00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Y_AXIS_ANGULAR_RATE_MSB_FROM_IMU, "Y-Axis Angular Rate MSB from IMU", 0x8F01),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Z_AXIS_ANGULAR_RATE_MSB_FROM_IMU, "Z-Axis Angular Rate MSB from IMU", 0x8F02),
            new EnumHexValue<>(DirectInteractionTriggerEnum.HEADING_MSB_FROM_IMU, "Heading MSB from IMU", 0x9000),
            new EnumHexValue<>(DirectInteractionTriggerEnum.PITCH_MSB_FROM_IMU, "Pitch MSB from IMU", 0x9001),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ROLL_MSB_FROM_IMU, "Roll MSB from IMU", 0x9002),
            new EnumHexValue<>(DirectInteractionTriggerEnum.TEMPERATURE_FROM_ENV, "Temperature from ENV", 0xC800),
            new EnumHexValue<>(DirectInteractionTriggerEnum.PRESSURE_MSB_FROM_ENV, "Pressure MSB from ENV", 0xC801),
            new EnumHexValue<>(DirectInteractionTriggerEnum.HUMIDITY_FROM_ENV, "Humidity from ENV", 0xC802),
            new EnumHexValue<>(DirectInteractionTriggerEnum.IAQ_INDEX_FROM_ENV, "IAQ Index from ENV", 0xC803),
            new EnumHexValue<>(DirectInteractionTriggerEnum.IAQ_ACCURACY_FROM_ENV, "IAQ Accuracy from ENV", 0xC804),
            new EnumHexValue<>(DirectInteractionTriggerEnum.VOC_FROM_ENV, "VOC from ENV", 0xC805),
            new EnumHexValue<>(DirectInteractionTriggerEnum.W_QUATERNION_MSB_FROM_IMU, "W Quaternion MSB from IMU", 0xC500),
            new EnumHexValue<>(DirectInteractionTriggerEnum.X_QUATERNION_MSB_FROM_IMU, "X Quaternion MSB from IMU", 0xC501),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Y_QUATERNION_MSB_FROM_IMU, "Y Quaternion MSB from IMU", 0xC502),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Z_QUATERNION_MSB_FROM_IMU, "Z Quaternion MSB from IMU", 0xC503),
            new EnumHexValue<>(DirectInteractionTriggerEnum.RED_LIGHT_MSB_FROM_RGB, "Red Light MSB from RGB", 0xC600),
            new EnumHexValue<>(DirectInteractionTriggerEnum.GREEN_LIGHT_MSB_FROM_RGB, "Green Light MSB from RGB", 0xC601),
            new EnumHexValue<>(DirectInteractionTriggerEnum.BLUE_LIGHT_MSB_FROM_RGB, "Blue Light MSB from RGB", 0xC602),
            new EnumHexValue<>(DirectInteractionTriggerEnum.WHITE_LIGHT_MSB_FROM_RGB, "White Light MSB From RGB", 0xC603),
            new EnumHexValue<>(DirectInteractionTriggerEnum.COLOUR_LIGHT_FROM_RGB, "Colour Light from RGB", 0xC604),
            new EnumHexValue<>(DirectInteractionTriggerEnum.RSSI_FROM_ANOTHER_BLEB_FROM_SENTRY, "RSSI from another BLE-B from Sentry Mode", 0xC700),
            new EnumHexValue<>(DirectInteractionTriggerEnum.RSSI_FROM_THIS_BLEB_FROM_SENTRY, "RSSI from this BLE-B", 0xC701),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DISTANCE_FROM_ANOTHER_BLEB_FROM_SENTRY, "Distance from another BLE-B from Sentry Mode", 0xC700),
            new EnumHexValue<>(DirectInteractionTriggerEnum.DISTANCE_FROM_THIS_BLEB_FROM_SENTRY, "Distance from this BLE-B", 0xC701),
            new EnumHexValue<>(DirectInteractionTriggerEnum.POWDER_DENSITY_FROM_POW, "Powder Density MSB From POW", 0x2700),
            new EnumHexValue<>(DirectInteractionTriggerEnum.CO2_CONCENTRATION_FROM_CO2, "CO2 Concentration MSB From CO2", 0x2800),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ALTITUDE_MSB_FROM_GPS, "Altitude MSB From GPS", 0xE400),
            new EnumHexValue<>(DirectInteractionTriggerEnum.SPEED_MSB_FROM_GPS, "Speed MSB From GPS", 0xE401),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ORIENTATION_MSB_FROM_GPS, "Orientation From GPS", 0xE403),
            new EnumHexValue<>(DirectInteractionTriggerEnum.FIX_STATUS_FROM_GPS, "Fix Status From GPS", 0xE404),
            new EnumHexValue<>(DirectInteractionTriggerEnum.GEOFENCE_FROM_GPS, "Geo-fence From GPS", 0xE405),
            new EnumHexValue<>(DirectInteractionTriggerEnum.PM25_DENSITY_FROM_PMX, "PM2.5 density MSB from PMX", 0x5600),
            new EnumHexValue<>(DirectInteractionTriggerEnum.PM10_DENSITY_FROM_PMX, "PM10 density MSB from PMX", 0x5601),
            new EnumHexValue<>(DirectInteractionTriggerEnum.X_AXIS_ACCELERATION_FROM_ACC, "X-Axis Acceleration from ACC", 0xA900),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Y_AXIS_ACCELERATION_FROM_ACC, "Y-Axis Acceleration from ACC", 0xA901),
            new EnumHexValue<>(DirectInteractionTriggerEnum.Z_AXIS_ACCELERATION_FROM_ACC, "Z-Axis Acceleration from ACC", 0xA902),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ACCELERATION_MODULE_FROM_ACC, "Acceleration module from ACC", 0xA904),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ACTIVITY_FROM_ACC, "Activity from ACC", 0xA905),
            new EnumHexValue<>(DirectInteractionTriggerEnum.SHOCK_FROM_ACC, "Shock from ACC", 0xA906),
            new EnumHexValue<>(DirectInteractionTriggerEnum.ORIENTATION_CHANGE_FROM_ACC, "Orientation change from ACC", 0xA907),
            new EnumHexValue<>(DirectInteractionTriggerEnum.AMBIENT_TEMPERATURE_FROM_CLT, "Ambient temperature from CLT", 0x7200),
            new EnumHexValue<>(DirectInteractionTriggerEnum.OBJECT_TEMPERATURE_FROM_CLT, "Object temperature from CLT", 0x7201),
            new EnumHexValue<>(DirectInteractionTriggerEnum.WIFI_STATUS_FROM_ESP, "Wi-Fi Status from ESP", 0x1300),
            new EnumHexValue<>(DirectInteractionTriggerEnum.MQTT_STATUS_FROM_ESP, "MQTT Status from ESP", 0x1301),
            new EnumHexValue<>(DirectInteractionTriggerEnum.EDGE_PROCESSING_OUTPUT_FROM_ESP, "Edge Processing Output from ESP", 0xe600),
            new EnumHexValue<>(DirectInteractionTriggerEnum.VOLTAGE_MSB_FROM_CMS, "Voltage MSB from CMS", 0xaa00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.CURRENT_MSB_FROM_CMS, "Current MSB from CMS", 0xaa01),
            new EnumHexValue<>(DirectInteractionTriggerEnum.POWER_MSB_FROM_CMS, "Power MSB from CMS", 0xaa02),
            new EnumHexValue<>(DirectInteractionTriggerEnum.RAW_RMS_VOLTAGE_FROM_RMS, "Raw RMS Voltage MSB from RMS", 0x2a00),
            new EnumHexValue<>(DirectInteractionTriggerEnum.UNKNOWN, "Unknown", 0x0000)
    );

    public enum DirectInteractionTriggerEnum {
        ANALOG_INPUT_A,
        ANALOG_INPUT_B,
        DIGITAL_INPUTS,
        DIGITAL_OUTPUT_0,
        DIGITAL_OUTPUT_1,
        DIGITAL_OUTPUT_2,
        DIGITAL_OUTPUT_3,
        MESSAGE,
        TEMPERATURE,
        SOIL_MOISTURE_FROM_SMS,
        TOUCH_DETECTION_FROM_CAP,
        CAP_STATUS,
        BUTTON,
        BATTERY,
        UV_LIGHT_MSB_FROM_UVA,
        DIGITAL_INPUT_1_FROM_REL,
        DIGITAL_INPUT_2_FROM_REL,
        MUTUAL_CAPACITANCE_MSB_FROM_SMS,
        SELF_CAPACITANCE_MSB_FROM_SMS,
        BLUE_LED_BRIGHTNESS,
        GREEN_LED_BRIGHTNESS,
        RED_LED_BRIGHTNESS,
        DISTANCE_MSB_FROM_PDM,
        RELAY_STATUS_FROM_REL,
        DIGITAL_OUTPUT_1_DUTY_CYCLE_FROM_REL,
        DIGITAL_OUTPUT_2_DUTY_CYCLE_FROM_REL,
        HUMIDITY_MSB_FROM_RHT,
        TEMPERATURE_MSB_FROM_RHT,
        TEMPERATURE_MSB_FROM_PRT,
        PRESSURE_MSB_FROM_PRT,
        X_AXIS_ACCELERATION_MSB_FROM_IMU,
        Y_AXIS_ACCELERATION_MSB_FROM_IMU,
        Z_AXIS_ACCELERATION_MSB_FROM_IMU,
        FREE_FALL_ACCELERATION_FROM_IMU,
        ACCELERATION_MSB_FROM_IMU,
        X_AXIS_MAGNETIC_FIELD_MSB_FROM_IMU,
        Y_AXIS_MAGNETIC_FIELD_MSB_FROM_IMU,
        Z_AXIS_MAGNETIC_FIELD_MSB_FROM_IMU,
        MAGNETIC_FIELD_MSB_FROM_IMU,
        X_AXIS_ANGULAR_RATE_MSB_FROM_IMU,
        Y_AXIS_ANGULAR_RATE_MSB_FROM_IMU,
        Z_AXIS_ANGULAR_RATE_MSB_FROM_IMU,
        HEADING_MSB_FROM_IMU,
        PITCH_MSB_FROM_IMU,
        ROLL_MSB_FROM_IMU,
        TEMPERATURE_FROM_ENV,
        PRESSURE_MSB_FROM_ENV,
        HUMIDITY_FROM_ENV,
        VOC_FROM_ENV,
        IAQ_INDEX_FROM_ENV,
        IAQ_ACCURACY_FROM_ENV,
        W_QUATERNION_MSB_FROM_IMU,
        X_QUATERNION_MSB_FROM_IMU,
        Y_QUATERNION_MSB_FROM_IMU,
        Z_QUATERNION_MSB_FROM_IMU,
        RED_LIGHT_MSB_FROM_RGB,
        GREEN_LIGHT_MSB_FROM_RGB,
        BLUE_LIGHT_MSB_FROM_RGB,
        WHITE_LIGHT_MSB_FROM_RGB,
        COLOUR_LIGHT_FROM_RGB,
        RSSI_FROM_ANOTHER_BLEB_FROM_SENTRY,
        RSSI_FROM_THIS_BLEB_FROM_SENTRY,
        DISTANCE_FROM_ANOTHER_BLEB_FROM_SENTRY,
        DISTANCE_FROM_THIS_BLEB_FROM_SENTRY,
        POWDER_DENSITY_FROM_POW,
        CO2_CONCENTRATION_FROM_CO2,
        ALTITUDE_MSB_FROM_GPS,
        SPEED_MSB_FROM_GPS,
        ORIENTATION_MSB_FROM_GPS,
        FIX_STATUS_FROM_GPS,
        GEOFENCE_FROM_GPS,
        PM25_DENSITY_FROM_PMX,
        PM10_DENSITY_FROM_PMX,
        X_AXIS_ACCELERATION_FROM_ACC,
        Y_AXIS_ACCELERATION_FROM_ACC,
        Z_AXIS_ACCELERATION_FROM_ACC,
        ACCELERATION_MODULE_FROM_ACC,
        ACTIVITY_FROM_ACC,
        SHOCK_FROM_ACC,
        ORIENTATION_CHANGE_FROM_ACC,
        AMBIENT_TEMPERATURE_FROM_CLT,
        OBJECT_TEMPERATURE_FROM_CLT,
        WIFI_STATUS_FROM_ESP,
        MQTT_STATUS_FROM_ESP,
        EDGE_PROCESSING_OUTPUT_FROM_ESP,
        VOLTAGE_MSB_FROM_CMS,
        CURRENT_MSB_FROM_CMS,
        POWER_MSB_FROM_CMS,
        RAW_RMS_VOLTAGE_FROM_RMS,
        UNKNOWN,
    }

    public static ValuesContainer<DirectInteractionComparatorEnum> DirectInteractionComparator = new ValuesContainer<>(
            new EnumHexValue<>(DirectInteractionComparatorEnum.UNKNOWN, "Unknown", 0xFF),
            new EnumHexValue<>(DirectInteractionComparatorEnum.EQUAL, "Equal", 0x00),
            new EnumHexValue<>(DirectInteractionComparatorEnum.LESSER, "Lesser", 0x01),
            new EnumHexValue<>(DirectInteractionComparatorEnum.GREATER, "Greater", 0x02)
    );

    public enum DirectInteractionComparatorEnum {
        UNKNOWN, EQUAL, LESSER, GREATER
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnDirectInteractionMessageListener mOnDirectInteractionMessageListener;

        public OnDirectInteractionMessageListener getOnDirectInteractionMessageListener() {
            return mOnDirectInteractionMessageListener;
        }

        public void setOnDirectInteractionMessageListener(OnDirectInteractionMessageListener onDirectInteractionMessageListener) {
            mOnDirectInteractionMessageListener = onDirectInteractionMessageListener;
        }

        public interface OnDirectInteractionMessageListener {
            void OnDirectInteractionMessageReceived(final String address, final String target, final String commandRaw);
        }
    }
    //endregion

    //region Commands
    public final CommandsContainer Commands = new CommandsContainer();

    public static class CommandsContainer {
        private CommandsContainer() {
        }

        /**
         * Enable or Disable a specific Direct Interaction
         *
         * @param active        Set if the trigger should be enabled or disabled
         * @param trigger       The trigger (event) which cause the sending of a certain command upon a determined event.
         * @param comparator    The comparator: represents how the monitored parameter shall be compared to the triggering threshold
         * @param threshold     The triggering threshold
         * @param command       The command that must be sent in advertising when Direct Interaction is triggered
         * @param targetAddress The Bluetooth Address of the device the Direct Interaction command sent by the BLE-B upon triggering event is addressed to (in hexadecimal format "00:00:00:00:00:00")
         * @return The command
         */
        public Command SetDirectInteraction(final boolean active, final DirectInteractionTriggerEnum trigger, final DirectInteractionComparatorEnum comparator, final int threshold, final Command command, final String targetAddress) {
            Command c = new Command("DD");

            String value = active ? "01" : "00";
            value += BLEUtils.IntToHex(DirectInteractionTrigger.getByEnum(trigger).getValue(), 2);
            value += BLEUtils.IntToHex(DirectInteractionComparator.getByEnum(comparator).getValue(), 1);
            value += BLEUtils.IntToHex(threshold, 1);

            String cmdString = command.getDataType() + command.getValue();
            if (cmdString.length() > 12)
                cmdString = cmdString.substring(0, 12);
            StringBuilder cmdStringB = new StringBuilder(cmdString);
            while (cmdStringB.length() < 12)
                cmdStringB.append('0');
            value += cmdStringB.toString();

            String targetString = BLEUtils.MacAddressRemoveColons(targetAddress.trim());
            if (targetString.length() > 12)
                targetString = targetString.substring(0, 12);
            StringBuilder targetStringB = new StringBuilder(targetString);
            while (targetStringB.length() < 12)
                targetStringB.append('0');
            value += targetStringB.toString();

            c.setValue(value);
            return c;
        }

        /**
         * Delete all direct interaction triggers
         *
         * @return The command
         */
        public Command ResetDirectInteractions() {
            return new Command("DE", "");
        }


        /**
         * Set the reactivity of the Direct Interactions changing
         * the refresh interval of the check of the values
         *
         * @param refreshRate The refresh rate, from 0.02s to 1s
         * @return The command
         */
        public Command SetReactivity(int refreshRate) {
            Command c = new Command("D9");

            String value = BLEUtils.IntToHex(DoubleToByteConversions.from20msTo1s(refreshRate), 1);
            c.setValue(value);
            return c;
        }

        /**
         * Ask for information about a specific direct interaction (by index).
         * The response it will be available in the MainValues property of the BLEB module
         * (to be read in connection).
         *
         * @param index The index of the direct interaction.
         *              With 255 it will reply with the last d.i. available.
         *              With 0 it will reply with the responsiveness of the direct interactions.
         * @return The command
         */
        public Command AskDirectInteractionInfoByIndex(int index) {
            Command c = new Command("BB");

            String value = BLEUtils.IntToHex(index, 1);
            c.setValue(value);
            return c;
        }
    }
    //endregion


    DirectInteraction() {
        super("Direct Interactions", "Allows BLE-B to send arbitrary commands to other BLE-Bs upon arbitrary events");
        setAlwaysConnected();
    }

    @Override
    void AnalyzeManufacturerData(String address, int dataType, byte[] data, byte[] manufacturerData, int battery, int rssi) {

    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {

    }

    @Override
    void AnalyzeDirectInteractionMessage(String address, String target, byte[] command) {
        super.AnalyzeDirectInteractionMessage(address, target, command);

        if (ScanEvents.getOnDirectInteractionMessageListener() != null)
            ScanEvents.getOnDirectInteractionMessageListener().OnDirectInteractionMessageReceived(address, target, BLEUtils.BytesToHex(command));
    }
}
