/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

import androidx.annotation.NonNull;

public class Command {
    private String mServiceUUID;
    private String mCharacteristicUUID;
    private String mDataType;
    private String mValue;

    public Command(String serviceUUID, String characteristicUUID, String dataType, String value) {
        setServiceUUID(serviceUUID);
        setCharacteristicUUID(characteristicUUID);
        setDataType(dataType);
        setValue(value);
    }

    public Command(String characteristicUUID, String dataType, String value) {
        this(Constants.UUID_MAIN_SERVICE, characteristicUUID, dataType, value);
    }

    public Command(String dataType, String value) {
        this(Constants.UUID_SEND_COMMAND_CHARACTERISTIC, dataType, value);
    }

    public Command(String dataType) {
        this(dataType, "00");
    }

    public String getServiceUUID() {
        return mServiceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        mServiceUUID = serviceUUID;
    }

    public String getCharacteristicUUID() {
        return mCharacteristicUUID;
    }

    public void setCharacteristicUUID(String characteristicUUID) {
        mCharacteristicUUID = characteristicUUID;
    }

    public String getDataType() {
        return mDataType;
    }

    public void setDataType(String dataType) {
        mDataType = dataType;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value + "00";
    }

    @Override
    public String toString() {
        return "Service UUID: '" + getServiceUUID() + '\'' +
                ", Characteristic UUID: '" + getCharacteristicUUID() + '\'' +
                ", Data Type: '" + getDataType() + '\'' +
                ", Value: '" + getValue() + '\'';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Command))
            return false;

        Command prop = (Command) obj;
        return getServiceUUID().equals(prop.getServiceUUID()) && getCharacteristicUUID().equals(prop.getCharacteristicUUID()) && getDataType().equals(prop.getDataType()) && getValue().equals(prop.getValue());
    }


    /**
     * Combine different commands in one command
     *
     * @param commands The array of commands to merge
     * @return The command
     */
    public static Command CombineCommands(Command... commands) {
        Command result = new Command("");

        StringBuilder value = new StringBuilder();
        for (Command c : commands) {
            value.append(c.getDataType());
            value.append(c.getValue().substring(0, c.getValue().length() - 2));
        }
        value.append("00");

        result.setValue(value.toString());
        return result;
    }


    /**
     * Create a custom command with a specified HEX-formatted string (with default characteristic and service UUID)
     *
     * @param hex The HEX-formatted string
     * @return The command
     */
    public static Command CreateCustomCommand(@NonNull final String hex) {
        return CreateCustomCommand(hex, Constants.UUID_SEND_COMMAND_CHARACTERISTIC);
    }

    /**
     * Create a custom command with a specified HEX-formatted string (with specified characteristic UUID and default service UUID)
     *
     * @param hex                The HEX-formatted string
     * @param characteristicUUID The string containing the characteristic UUID
     * @return The command
     */
    public static Command CreateCustomCommand(@NonNull final String hex, @NonNull final String characteristicUUID) {
        return CreateCustomCommand(hex, characteristicUUID, Constants.UUID_MAIN_SERVICE);
    }

    /**
     * Create a custom command with a specified HEX-formatted string (with specified characteristic UUID and service UUID)
     *
     * @param hex                The HEX-formatted string
     * @param characteristicUUID The string containing the characteristic UUID
     * @param serviceUUID        The string containing the service UUID
     * @return The command
     */
    public static Command CreateCustomCommand(@NonNull final String hex, @NonNull final String characteristicUUID, @NonNull final String serviceUUID) {
        String datatype = "";
        if (hex.length() >= 2)
            datatype = hex.substring(0, 2);

        String value = "";
        if (hex.length() > 2)
            value = hex.substring(2);

        return new Command(serviceUUID, characteristicUUID, datatype, value);
    }

    /**
     * Create a command from an object
     *
     * @param obj The object to convert
     * @return The command
     */
    public static Command CreateCommandFromObject(@NonNull final Object obj) {
        Command c = null;

        if (obj instanceof Command)
            c = (Command) obj;
        else if (obj instanceof String)
            c = Command.CreateCustomCommand((String) obj);

        return c;
    }

}
