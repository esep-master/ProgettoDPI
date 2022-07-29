/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

public class DoubleToByteConversions {
    public static int from20msTo1s(double val) {
        int result = 0;

        if (val > 0) {
            result = (int)(val * 100d);
        }

        return result;
    }

    public static int from20msTo10s(double val) {
        int result = 0;

        if (val > 0) {
            if (val <= 0.02d)
                result = 2;
            else if (val <= 1d)
                result = (int)BLEUtils.map(val, 0.03d, 1d, 3, 100);
            else
                result = (int)BLEUtils.map(val, 4d, 10.2d, 101, 255);

            if (result > 255)
                result = 255;
        }

        return result;
    }

    public static int from500msTo12days(double val) {
        int result = 0;

        if (val > 0) {
            if (val <= 0.500d)
                result = 50;
            else if (val <= 2.02d)
                result = (int)BLEUtils.map(val, 0.510d, 2.02d, 11, 202);
            else if(val <= 40d)
                result = (int)BLEUtils.map(val, 3d, 40d, 202, 240);
            else
                result = (int)BLEUtils.map(val, 60d, 1036800d, 241, 255);

            if (result > 255)
                result = 255;
        }

        return result;
    }

    public static int from100msTo12days(double val) {
        int result = 0;

        if (val > 0) {
            if (val <= 0.100d)
                result = 10;
            else if (val <= 2.02d)
                result = (int)BLEUtils.map(val, 0.110d, 2.02d, 11, 202);
            else if(val <= 40d)
                result = (int)BLEUtils.map(val, 3d, 40d, 202, 240);
            else
                result = (int)BLEUtils.map(val, 60d, 1036800d, 241, 255);

            if (result > 255)
                result = 255;
        }

        return result;
    }

    public static int from20msTo12days(double val) {
        int result = 0;

        if (val > 0) {
            if (val <= 0.02d)
                result = 2;
            else if (val <= 2.02d)
                result = (int)BLEUtils.map(val, 0.03d, 2.02d, 3, 202);
            else if(val <= 40d)
                result = (int)BLEUtils.map(val, 3d, 40d, 202, 240);
            else
                result = (int)BLEUtils.map(val, 60d, 1036800d, 241, 255);

            if (result > 255)
                result = 255;
        }

        return result;
    }

    public static int from10msTo12days(double val) {
        int result = 0;

        if (val > 0) {
            if (val <= 0.01d)
                result = 1;
            else if (val <= 2.02d)
                result = (int)BLEUtils.map(val, 0.02d, 2.02d, 2, 202);
            else if(val <= 40d)
                result = (int)BLEUtils.map(val, 3d, 40d, 202, 240);
            else
                result = (int)BLEUtils.map(val, 60d, 1036800d, 241, 255);

            if (result > 255)
                result = 255;
        }

        return result;
    }

    public static int from10msTo2550ms(double val) {
        int result = 0;

        if(val > 0) {
            result = (int) (val * 100d);

            if (result > 255)
                result = 255;
        }

        return result;
    }

    public static int from100msTo2550ms(double val) {
        int result = 0;

        if(val > 0) {
            result = (int) (val * 100d);

            if (result > 255)
                result = 255;
            if (result < 10)
                result = 10;
        }

        return result;
    }

    public static int from100msTo6553500ms(double val) {
        int result = 0;

        if(val > 0) {
            result = (int) (val * 10d);

            if (result > 65535)
                result = 65535;
            if (result < 1)
                result = 1;
        }

        return result;
    }

    public static int from100msTo25s(double val) {
        int result = 0;

        if(val > 0) {
            result = (int) (val * 10d);

            if (result > 255)
                result = 255;
        }

        return result;
    }
}
