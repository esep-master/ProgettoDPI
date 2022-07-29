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

public class LogFile {
    public static boolean mIsInit = false;
    public static FileOutputStream mOutputStream;

    public static void Init(final String address, final String postfix) {
        if (!mIsInit) {
            mIsInit = true;

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            String date = df.format(Calendar.getInstance().getTime());

            String filename = address.replace(":", "") + "-" + date + "-" + postfix + ".txt";

            File makeAppDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "MakeApp");
            if (!makeAppDir.exists())
                makeAppDir.mkdirs();

            File file = new File(makeAppDir, filename);

            Logger.Log(LogFile.class, "Saving " + postfix + " to: " + file.getAbsolutePath());

            try {
                mOutputStream = new FileOutputStream(file, true);
            } catch (IOException e) {
                mIsInit = false;
                Logger.Err(LogFile.class, e.toString());
                return;
            }

        }
    }


    public static void Close() {
        if (mIsInit) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                mIsInit = false;
                Logger.Err(LogFile.class, e.toString());
                return;
            }

            mOutputStream = null;
            mIsInit = false;
        }
    }

    public static void Append(final String text) {
        if (mIsInit) {
            String message = "";

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
            message += sdf.format(Calendar.getInstance().getTime());
            message += ": ";
            message += text;
            message += "\n";

            try {
                mOutputStream.write(message.getBytes());
            } catch (IOException e) {
                mIsInit = false;
                Logger.Err(LogFile.class, e.toString());
                return;
            }
        }
    }
}

