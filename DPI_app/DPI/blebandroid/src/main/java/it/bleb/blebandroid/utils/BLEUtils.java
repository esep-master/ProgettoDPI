/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import it.bleb.blebandroid.BLEGPS;

public final class BLEUtils {

    public enum PeriodUnitEnum {
        SECONDS, MINUTES, HOURS
    }

    public static int GetCodedPeriod(final int period, final PeriodUnitEnum unit) {
        int periodCoded = period;
        switch (unit) {
            case SECONDS:
                periodCoded = periodCoded < 1 ? 1 : (periodCoded > 59 ? 59 : periodCoded);
                break;
            case MINUTES:
                periodCoded = periodCoded < 1 ? 1 : (periodCoded > 59 ? 59 : periodCoded);
                periodCoded += 60;
                break;
            case HOURS:
                periodCoded = periodCoded < 1 ? 1 : (periodCoded > 12 ? 12 : periodCoded);
                periodCoded += 120;
                break;
        }
        return periodCoded;
    }

    public static Pair<Integer, PeriodUnitEnum> DecodePeriod(int period) {
        PeriodUnitEnum unit = PeriodUnitEnum.SECONDS;
        if (period > 120) {
            period -= 120;
            unit = PeriodUnitEnum.HOURS;
        } else if (period > 60) {
            period -= 60;
            unit = PeriodUnitEnum.MINUTES;
        }

        return new Pair<>(period, unit);
    }


    /**
     * Check if the device is compatible with Bluetooth Low Energy
     *
     * @param packageManager The package manager obtained from the activity (see Activity.getPackageManager())
     * @return true if is compatible with BLE, false otherwise
     */
    public static boolean IsBLECompatible(final PackageManager packageManager) {
        if (packageManager == null)
            return false;

        final boolean validSdkLevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        if (!validSdkLevel)
            Logger.Err(BLEUtils.class, "Bluetooth Low Energy requires an SDK version higher or equal to Android Lollipop (21).");

        final boolean compatible = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (!compatible)
            Logger.Err(BLEUtils.class, "Bluetooth Low Energy is NOT supported on this hardware.");

        return (validSdkLevel && compatible);
    }

    private static final Pattern UUID_FORMAT = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");

    /**
     * Check if a string UUID is a valid UUID
     *
     * @param UUID
     * @return
     */
    public static boolean IsValidUUID(final String UUID) {
        return UUID_FORMAT.matcher(UUID).find();
    }

    /**
     * Get all the characteristics by a specific Service and Characteristic UUID
     *
     * @param gatt               The bluetooth gatt connection
     * @param serviceUUID        The service UUID
     * @param characteristicUUID The characteristic UUID
     * @return A list of characteristics with the selected UUID
     */
    public static List<BluetoothGattCharacteristic> GetCharacteristicsByUUID(final BluetoothGatt gatt, final String serviceUUID, final String characteristicUUID) {
        if (!IsValidUUID(serviceUUID) || !IsValidUUID(characteristicUUID))
            return new ArrayList<>();

        return GetCharacteristicsByUUID(gatt, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
    }

    /**
     * Get all the characteristics by a specific Service and Characteristic UUID
     *
     * @param gatt               The bluetooth gatt connection
     * @param serviceUUID        The service UUID
     * @param characteristicUUID The characteristic UUID
     * @return A list of characteristics with the selected UUID
     */
    public static List<BluetoothGattCharacteristic> GetCharacteristicsByUUID(final BluetoothGatt gatt, final UUID serviceUUID, final UUID characteristicUUID) {
        List<BluetoothGattCharacteristic> characteristics = new ArrayList<BluetoothGattCharacteristic>();

        if (gatt != null) {
            for (BluetoothGattService service : gatt.getServices()) {
                if (service.getUuid().equals(serviceUUID)) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        if (characteristic.getUuid().equals(characteristicUUID)) {
                            characteristics.add(characteristic);
                        }
                    }
                }
            }
        }

        return characteristics;
    }

    /**
     * Remove all the colons (':') from the mac address
     *
     * @param macAddress The address
     * @return The sanitized address
     */
    public static String MacAddressRemoveColons(final String macAddress) {
        return SanitizeHex(macAddress);
    }

    /**
     * Remove all the non-HEX symbols from the string
     *
     * @param s the String
     * @return The sanitized string
     */
    public static String SanitizeHex(final String s) {
        return s.trim().replaceAll("[^a-fA-F0-9]", "");
    }

    /**
     * Convert a byte array to a mac address reppresentation
     *
     * @param bytes The address in byte array
     * @return The mac address in "00:00:00:00:00:00" format
     */
    public static String BytesToMacAddress(final byte[] bytes) {
        String result = BytesToHex(bytes);
        if(result.length() > 12)
            result = result.substring(0, 12);
        result = result.replaceAll("..(?!$)", "$0:");

        return result;
    }


    /**
     * Convert a byte array to a coordinate (latitude/longitude)
     *
     * @param bytes The coordinate in byte array
     * @return The coordinate in "12.34567" format
     */
    public static String BytesToCoordinate(final byte... bytes) {
        String str = BLEUtils.BytesToHex(bytes).toLowerCase();

        if (str.contains("f")) {
            String base = str.substring(0, str.indexOf('f'));
            String dec = str.substring(str.indexOf('f') + 1);
            return base + "." + dec;
        }
        return str;
    }


    /**
     * Convert a hex value string to a integer value (eg. "010A" => 266)
     *
     * @param hex The hex-formatted string
     * @return The converted integer value
     */
    public static int HexToInt(final String hex) {
        if (hex != null) {
            String hexTmp = hex.trim();
            if (hexTmp.startsWith("0x"))
                hexTmp = hexTmp.substring(2);

            int res = 0;
            try {
                res = Integer.parseInt(hexTmp, 16);
            } catch (NumberFormatException e) {
                Logger.Warn(BLEUtils.class, "Error converting Hex to Integer. String is not in a correct hex format: " + hex + ". Returning 0.");
            }

            return res;
        } else
            return 0;
    }

    /**
     * Convert an integer value to a string with the hex representation of it
     *
     * @param value  The integer value to convert
     * @param nBytes The number of bytes you want as an output (if the number is less than the required bytes to represent the integer passed as an argument, the result will be truncated and you'll obtain the less significant bytes)
     * @return The string, with a length equals to the parameter nBytes, containing the hex representation of the integer passed as an argument
     */
    public static String IntToHex(final long value, final int nBytes) {
        byte[] result = new byte[nBytes];
        for (int i = 0; i < nBytes; i++)
            result[nBytes - 1 - i] = (byte) (value >>> (8 * i));

        return BytesToHex(result);
    }

    /**
     * Convert a hex value into a byte array (eg. "010A" => [ 1, 10 ])
     *
     * @param hex The hex-formatted string
     * @return The converted byte array
     */
    public static byte[] HexToBytes(final String hex) {
        String tmpHex = ((hex.length() % 2) > 0 ? "0" : "") + hex;

        int len = tmpHex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(tmpHex.charAt(i), 16) << 4) + Character.digit(tmpHex.charAt(i + 1), 16));

        return data;
    }

    /**
     * Convert an array of bytes to the equivalent hex representation in a String (eg. [ 64, 3 ] => "4003"
     *
     * @param bytes An array of bytes to convert
     * @return The string containing the hex representation of the bytes passed as argument
     */
    public static String BytesToHex(final byte... bytes) {
        if (bytes != null) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } else
            return "";
    }

    /**
     * Convert a byte array to a single unsigned int (eg. [ 2, 0 ] => 512)
     *
     * @param bytes From 0 to 4 byte values composing the unsigned integer
     * @return The equivalent unsigned integer value composed by putting every byte one after the others
     */
    public static int UnsignedBytesToInt(final byte... bytes) {
        switch (bytes.length) {
            case 0:
                return 0;
            case 1:
                return bytes[0] & 0x000000FF;
            case 2:
                return (bytes[0] << 8 & 0x0000FF00) | (bytes[1] & 0x000000FF);
            case 3:
                return (bytes[0] << 16 & 0x00FF0000) | (bytes[1] << 8 & 0x0000FF00) | (bytes[2] & 0x000000FF);
            case 4:
            default:
                return (bytes[0] << 24 & 0xFF000000) | (bytes[1] << 16 & 0x00FF0000) | (bytes[2] << 8 & 0x0000FF00) | (bytes[3] & 0x000000FF);
        }
    }

    /**
     * Convert a byte array to a single signed int (eg. [ 255, 254 ] => -2)
     *
     * @param bytes From 0 to 4 byte values composing the signed integer
     * @return The equivalent signed integer value composed by putting every byte one after the others
     */
    public static int SignedBytesToInt(final byte... bytes) {
        switch (bytes.length) {
            case 0:
                return 0;
            case 1:
                return bytes[0];
            case 2:
                return (bytes[0] << 8 & 0xFFFFFF00) | (bytes[1] & 0x000000FF);
            case 3:
                return (bytes[0] << 16 & 0xFFFF0000) | (bytes[1] << 8 & 0x0000FF00) | (bytes[2] & 0x000000FF);
            case 4:
            default:
                return (bytes[0] << 24 & 0xFF000000) | (bytes[1] << 16 & 0x00FF0000) | (bytes[2] << 8 & 0x0000FF00) | (bytes[3] & 0x000000FF);
        }
    }

    /**
     * Convert an array of bytes containing ASCII values to the equivalent string
     *
     * @param bytes Bytes containing the ASCII values
     * @return The string with the equivalent characters of the bytes passed as parameter
     */
    public static String ASCIIBytesToString(final byte... bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            int i = UnsignedBytesToInt(b);
            if (i > 0)
                builder.append((char) i);
        }
        return builder.toString();
    }

    /**
     * Convert a string to an array of bytes containing the equivalent ASCII values
     *
     * @param s The string to convert
     * @return The byte array with the equivalent ASCII values
     */
    public static byte[] StringToASCIIBytes(final String s) {
        return s.getBytes();
    }

    /**
     * Get a specific bit in a byte
     *
     * @param b the byte
     * @param i the index of which bit you want to check
     * @return true if the selected bit is 1, false otherwise
     */
    public static boolean GetBit(final byte b, final int i) {
        return ((b >> i) & 1) != 0;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Get the length (in bytes) of the value of a specific data type inside the manufacturer data
     *
     * @param dataType the data type to consider
     * @return the number of bytes
     */
    public static int GetLengthByDataType(final byte dataType) {
        int d = UnsignedBytesToInt(dataType);

        if (d > 0) {
            if (d <= 0x1C) return 1;
            else if (d <= 0x38) return 2;
            else if (d <= 0x54) return 3;
            else if (d <= 0x70) return 4;
            else if (d <= 0x8C) return 5;
            else if (d <= 0xA8) return 6;
            else if (d <= 0xC4) return 7;
            else if (d <= 0xE0) return 8;
            else return 9;
        }

        return 0;
    }

    /**
     * Map a value from a starting range to a new range (eg. 7.5d, 5, 10, 0, 100 => 50d)
     *
     * @param value  The value to consider
     * @param istart The start of the initial range
     * @param istop  The end of the initial range
     * @param ostart The start of the result range
     * @param ostop  The finish of the result range
     * @return The mapped  value inside the result range
     */
    public static double map(final double value, final double istart, final double istop, final double ostart, final double ostop) {
        double inRangeValue = value;
        if (inRangeValue < istart)
            inRangeValue = istart;
        if (inRangeValue > istop)
            inRangeValue = istop;
        return ostart + (ostop - ostart) * ((inRangeValue - istart) / (istop - istart));
    }
}
