/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 14/06/2019 22:07
 */

package it.bleb.blebandroid;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Property;

public class BLEIRR extends Component {
    BLEIRR() {
        super("BLE-IRR", "Infrared receiver");
    }

    //region ScanEvents
    public final ScanEvents ScanEvents = new ScanEvents();

    public static class ScanEvents {
        private ScanEvents() {
        }

        private OnMessageListener mOnMessageListener;

        public OnMessageListener getOnMessageListener() {
            return mOnMessageListener;
        }

        public void setOnMessageListener(OnMessageListener onMessageListener) {
            mOnMessageListener = onMessageListener;
        }

        public interface OnMessageListener {
            void OnMessageReceived(final String address, final String messageHex, final String messageAscii);
        }
    }

    @Override
    void AnalyzeManufacturerData(final String address, final int dataType, final byte[] data, byte[] manufacturerData, int battery, int rssi) {
        switch (dataType) {
            case 0x92: {
                setConnected(true);
                String messageHex = BLEUtils.BytesToHex(data[0], data[1], data[2], data[3], data[4], data[5]);
                String messageAscii = BLEUtils.ASCIIBytesToString(data[0], data[1], data[2], data[3], data[4], data[5]);
                if (ScanEvents.getOnMessageListener() != null)
                    ScanEvents.getOnMessageListener().OnMessageReceived(address, messageHex, messageAscii);
            }
            break;
        }
    }
    //endregion

    //region Properties
    public final PropertiesContainer Properties = new PropertiesContainer();

    public static class PropertiesContainer {
        private final Property mMainValues = new Property("CEE2BB92-30B8-4A6B-913E-0EF628448151");

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
            void OnMainValuesReceived(final String address, final String messageHex, final String messageAscii);
        }
    }

    @Override
    void AnalyzeConnectionData(String address, Object property, byte[] data, boolean notification) {
        if (!(property instanceof Property))
            return;

        Property p = (Property) property;
        if (p.equals(Properties.getMainValues())) {
            if (ConnectionEvents.getOnMainValuesListener() != null) {
                ConnectionEvents.getOnMainValuesListener().OnMainValuesReceived(
                        address,
                        BLEUtils.BytesToHex(data[0], data[1], data[2], data[3], data[4], data[5]),
                        BLEUtils.ASCIIBytesToString(data[0], data[1], data[2], data[3], data[4], data[5])
                        );
            }
        }
    }
    //endregion
}
