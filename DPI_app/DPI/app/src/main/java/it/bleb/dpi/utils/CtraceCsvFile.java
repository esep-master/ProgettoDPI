package it.bleb.dpi.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.bleb.blebandroid.utils.Logger;

public class CtraceCsvFile {
    public static boolean mIsInit = false;
    public static FileOutputStream mOutputStream;

    public static synchronized void Init(final String address, final String postfix) {
        if (!mIsInit) {
            mIsInit = true;

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            String date = df.format(Calendar.getInstance().getTime());

            String filename = address.replace(":", "") + "-" + date + "-" + postfix + ".csv";

            File makeAppDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "MakeApp");
            if (!makeAppDir.exists())
                makeAppDir.mkdirs();

            File file = new File(makeAppDir, filename);

            Logger.Log(CtraceCsvFile.class, "Saving " + postfix + " to: " + file.getAbsolutePath());

            try {
                mOutputStream = new FileOutputStream(file, true);
            } catch (IOException e) {
                mIsInit = false;
                Logger.Err(CtraceCsvFile.class, e.toString());
                return;
            }

            String header = "timestamp,address,rssi\n";
            try {
                mOutputStream.write(header.getBytes());
            } catch (IOException e) {
                mIsInit = false;
                Logger.Err(CtraceCsvFile.class, e.toString());
                return;
            }
        }
    }


    public static synchronized void Close() {
        if (mIsInit) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                mIsInit = false;
                Logger.Err(CtraceCsvFile.class, e.toString());
                return;
            }

            mOutputStream = null;
            mIsInit = false;
        }
    }

    public static synchronized void Append(final int timestamp, final String address, final int rssi) {
        if (mIsInit) {
            String message = "";

            message += String.format(Locale.US, "%d", timestamp);
            message += ",";
            message += address;
            message += ",";
            message += String.format(Locale.US, "%d", rssi);
            message += "\n";

            try {
                mOutputStream.write(message.getBytes());
            } catch (IOException e) {
                mIsInit = false;
                Logger.Err(CtraceCsvFile.class, e.toString());
                return;
            }
        }
    }
}

