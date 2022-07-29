/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

import android.util.Log;

public final class Logger {
    private static int mLogLevel = 1;

    /**
     * Get the current level of severity allowed to appear in Logcat
     * @return The current level
     */
    public static int getLogLevel() {
        return mLogLevel;
    }

    /**
     * Set which level of severity you want to see in Logcat
     * @param logLevel 0 (none), 1 (Errors), 2 (Errors and Warnings), 3 (All)
     */
    public static void setLogLevel(final int logLevel) {
        if(logLevel < 0)
            mLogLevel = 0;
        else if(logLevel > 3)
            mLogLevel = 3;
        else
            mLogLevel = logLevel;
    }

    /**
     * Log a message (Log Level >= 3 needed)
     * @param o The object/class which is sending the message
     * @param message The message you want to send in Logcat
     */
    public static void Log(Object o, String message) {
        if(getLogLevel() >= 3) Log.i(GetTag(o), message);
    }

    /**
     * Log a warning message (Log Level >= 2 needed)
     * @param o The object/class which is sending the message
     * @param message The warning message you want to send in Logcat
     */
    public static void Warn(Object o, String message) {
        if(getLogLevel() >= 2) Log.w(GetTag(o), message);
    }

    /**
     * Log an error message (Log Level >= 1 needed)
     * @param o The object/class which is sending the message
     * @param message The error message you want to send in Logcat
     */
    public static void Err(Object o, String message) {
        if(getLogLevel() >= 1) Log.e(GetTag(o), message);
    }

    private static String GetTag(Object o) {
        String classname;
        if (o == null)
            classname = "null";
        else if (o instanceof String)
            classname = (String) o;
        else {
            if (o instanceof Class)
                classname = ((Class) o).getName();
            else
                classname = o.getClass().getName();

            if (classname.lastIndexOf('.') >= 0)
                classname = classname.substring(classname.lastIndexOf('.') + 1);

            if (classname.trim().isEmpty())
                classname = "none";
        }

        return "[BLEB]" + classname;
    }
}
