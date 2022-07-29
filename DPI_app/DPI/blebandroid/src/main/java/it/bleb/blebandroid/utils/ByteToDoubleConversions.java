/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

public class ByteToDoubleConversions {

    public static double from20msTo10s(int val) {
        double result = 0;

        if (val > 0) {
            if (val <= 2)
                result = 0.02d;
            else if (val <= 100)
                result = val / 100d;
            else
                result = 4d * val / 100d;

            if (result > 10.24d)
                result = 10.24d;
        }

        return result;
    }


    public static double from10msTo12days(int val) {
        double result = 0;

        if (val > 0) {
            if (val <= 202)
                result = val / 100d;
            else if (val <= 240)
                result = (val - 200);
            else
                result = (Math.pow(2, val - 240 + 15)) / 1000d;
        }

        return result;
    }

    public static double from20msTo12days(int val) {
        double result = 0;

        if (val > 0) {
            if (val <= 2)
                result = 0.02d;
            else if (val <= 202)
                result = val / 100d;
            else if (val <= 240)
                result = (val - 200);
            else
                result = (Math.pow(2, val - 240 + 15)) / 1000d;
        }

        return result;
    }

    public static double from100msTo12days(int val) {
        double result = 0;

        if (val > 0) {
            if (val <= 10)
                result = 0.1d;
            else if (val <= 202)
                result = val / 100d;
            else if (val <= 240)
                result = (val - 200);
            else
                result = (Math.pow(2, val - 240 + 15)) / 1000d;
        }

        return result;
    }

    public static double from500msTo12days(int val) {
        double result = 0;

        if (val > 0) {
            if (val <= 50)
                result = 0.5d;
            else if (val <= 202)
                result = val / 100d;
            else if (val <= 240)
                result = (val - 200);
            else
                result = (Math.pow(2, val - 240 + 15)) / 1000d;
        }

        return result;
    }

    public static double from10msTo2550ms(int val) {
        double result = 0;

        if (val > 0)
            result = val / 100d;

        return result;
    }

    public static double from100msTo25s(int val) {
        double result = 0;

        if (val > 0)
            result = val / 10d;

        return result;
    }

    public static double from100msTo6553500ms(int val) {
        double result = 0;

        if (val > 0)
            result = val / 10d;

        return result;
    }

    public static double from150msBy10ms(int val) {
        double result = 0;

        if (val / 100d > 0.15d)
            result = val / 100d;
        else
            result = 0.15d;

        return result;
    }
}
