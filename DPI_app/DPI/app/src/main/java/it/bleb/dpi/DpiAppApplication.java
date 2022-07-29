package it.bleb.dpi;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import java.util.HashMap;

import it.bleb.dpi.activities.ErrorActivity;
import it.bleb.dpi.brickblocks.BrickBlock;
import it.bleb.dpi.services.AppCloseDetectorService;
import it.bleb.dpi.services.BackgroundScanService;
import it.bleb.dpi.utils.Prefs;

public class DpiAppApplication  extends Application {
    private static DpiAppApplication mInstance;
    private static int mActivityVisible;
    private static Handler mServiceDelayedStartHandler = new Handler();

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private final HashMap<String, BrickBlock> mBrickBlocks = new HashMap<>();

    public static final boolean DEBUG_MODE = false;
    public static final boolean EASY_MODE = true;

    public DpiAppApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mUncaughtExceptionHandler);
    }

    public static DpiAppApplication getInstance() {
        return mInstance;
    }

    public HashMap<String, BrickBlock> getBrickBlocks() {
        return mBrickBlocks;
    }


    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
            intent.putExtra(ErrorActivity.EXTRA_ERROR_TITLE, "Error!");
            intent.putExtra(ErrorActivity.EXTRA_ERROR_MESSAGE, e.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
        }
    };

    public static boolean isActivityVisible() {
        return mActivityVisible > 0;
    }

    public static void activityResumed() {
        mActivityVisible += 1;
        mServiceDelayedStartHandler.removeCallbacksAndMessages(null);

        if(BackgroundScanService.isInstanceCreated()) {
            Intent serviceIntent = new Intent(mInstance, BackgroundScanService.class);
            mInstance.stopService(serviceIntent);
        }

        if(!AppCloseDetectorService.isInstanceCreated()) {
            Intent intent = new Intent(mInstance, AppCloseDetectorService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(mInstance, intent);
            } else {
                mInstance.startService(intent);
            }

        }
    }

    public static void activityPaused() {
        mActivityVisible -= 1;

        if (!isActivityVisible() && Prefs.IsBackgroundScanEnabled(mInstance) && !BackgroundScanService.isInstanceCreated()) {
            mServiceDelayedStartHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isActivityVisible()) {
                        Intent serviceIntent = new Intent(mInstance, BackgroundScanService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(mInstance, serviceIntent);
                        } else {
                            mInstance.startService(serviceIntent);
                        }

                    }
                }
            }, 1000);
        }
    }
}

