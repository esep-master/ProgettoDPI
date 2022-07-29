/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

import androidx.annotation.NonNull;

public class Property {
    private String mServiceUUID;
    private String mCharacteristicUUID;

    public Property(String serviceUUID, String characteristicUUID) {
        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;
    }

    public Property(String characteristicUUID) {
        this(Constants.UUID_MAIN_SERVICE, characteristicUUID);
    }

    public Property() {
        this(Constants.UUID_MAIN_SERVICE, Constants.UUID_SEND_COMMAND_CHARACTERISTIC);
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

    @Override
    public String toString() {
        return "Service UUID: '" + mServiceUUID + '\'' +
                ", Characteristic UUID: '" + mCharacteristicUUID + '\'';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Property))
            return false;

        Property prop = (Property) obj;
        return mServiceUUID.equals(prop.getServiceUUID()) && mCharacteristicUUID.equals(prop.getCharacteristicUUID());
    }

    /**
     * Create a custom property with a specified characteristic UUID (default service UUID)
     * @param characteristicUUID the characteristic UUID
     * @return The property
     */
    public static Property CreateCustomProperty(@NonNull final String characteristicUUID) {
        return CreateCustomProperty(Constants.UUID_MAIN_SERVICE, characteristicUUID);
    }

    /**
     * Create a custom property with a specified service UUID and characteristic UUID
     * @param serviceUUID the service UUID
     * @param characteristicUUID the characteristic UUID
     * @return The property
     */
    public static Property CreateCustomProperty(@NonNull final String serviceUUID, @NonNull final String characteristicUUID) {
        return new Property(serviceUUID, characteristicUUID);
    }

    /**
     * Create a property from an object
     * @param obj The object to convert
     * @return The property
     */
    public static Property CreatePropertyFromObject(@NonNull final Object obj) {
        Property p = null;

        if (obj instanceof Property)
            p = (Property) obj;
        else if (obj instanceof String)
            p = CreateCustomProperty((String) obj);

        return p;
    }

}
