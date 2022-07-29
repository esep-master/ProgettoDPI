package it.bleb.dpi.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import it.bleb.blebandroid.utils.BLEUtils;
import it.bleb.blebandroid.utils.Logger;
import it.bleb.dpi.BuildConfig;

public final class Prefs {
    private static SharedPreferences mSharedPreferences = null;
    private static String PREFERENCE_KEY = null;
    private static final Gson GSON = new Gson();


    private static SharedPreferences GetSharedPreferences(Context context) {
        if (PREFERENCE_KEY == null) {
            if (Prefs.GetIsCTrace(context))
                PREFERENCE_KEY = "Blebricks MyProtector Prefs";
            else
                PREFERENCE_KEY = "Blebricks DPI Prefs";
        }
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);

        return mSharedPreferences;
    }

    public static class GatewaySettings {
        private String mServerUri;
        private String mUsername;
        private String mPassword;
        private String mAppId;
        private int mInterval;

        public GatewaySettings(String serverUri, String username, String password, String appId, int interval) {
            mServerUri = serverUri;
            mUsername = username;
            mPassword = password;
            mAppId = appId;
            mInterval = interval;
        }

        public String getServerUri() {
            return mServerUri;
        }

        public void setServerUri(String serverUri) {
            mServerUri = serverUri;
        }

        public String getUsername() {
            return mUsername;
        }

        public void setUsername(String username) {
            mUsername = username;
        }

        public String getPassword() {
            return mPassword;
        }

        public void setPassword(String password) {
            mPassword = password;
        }

        public String getAppId() {
            return mAppId;
        }

        public void setAppId(String appId) {
            mAppId = appId;
        }

        public int getInterval() {
            return mInterval;
        }

        public void setInterval(int interval) {
            mInterval = interval;
        }
    }

    public static class CmsSettings {
        private boolean mGenericSensingMode;
        private String mParameterName;
        private String mUnitName;
        private int mCurrentMinValue;
        private int mOutputMinValue;
        private int mCurrentMaxValue;
        private int mOutputMaxValue;


        public CmsSettings(boolean genericSensingMode, String parameterName, String unitName, int currentMinValue, int outputMinValue, int currentMaxValue, int outputMaxValue) {
            mGenericSensingMode = genericSensingMode;
            mParameterName = parameterName;
            mUnitName = unitName;
            mCurrentMinValue = currentMinValue;
            mOutputMinValue = outputMinValue;
            mCurrentMaxValue = currentMaxValue;
            mOutputMaxValue = outputMaxValue;
        }

        public boolean isGenericSensingMode() {
            return mGenericSensingMode;
        }

        public void setGenericSensingMode(boolean genericSensingMode) {
            mGenericSensingMode = genericSensingMode;
        }

        public String getParameterName() {
            return mParameterName;
        }

        public void setParameterName(String parameterName) {
            mParameterName = parameterName;
        }

        public String getUnitName() {
            return mUnitName;
        }

        public void setUnitName(String unitName) {
            mUnitName = unitName;
        }

        public int getCurrentMinValue() {
            return mCurrentMinValue;
        }

        public void setCurrentMinValue(int currentMinValue) {
            mCurrentMinValue = currentMinValue;
        }

        public int getOutputMinValue() {
            return mOutputMinValue;
        }

        public void setOutputMinValue(int outputMinValue) {
            mOutputMinValue = outputMinValue;
        }

        public int getCurrentMaxValue() {
            return mCurrentMaxValue;
        }

        public void setCurrentMaxValue(int currentMaxValue) {
            mCurrentMaxValue = currentMaxValue;
        }

        public int getOutputMaxValue() {
            return mOutputMaxValue;
        }

        public void setOutputMaxValue(int outputMaxValue) {
            mOutputMaxValue = outputMaxValue;
        }
    }

    public static class RmsSettings {
        private int mBusVoltage;
        private double mCurrentMultiplier;

        public RmsSettings(int busVoltage, double currentMultiplier) {
            mBusVoltage = busVoltage;
            mCurrentMultiplier = currentMultiplier;
        }

        public int getBusVoltage() {
            return mBusVoltage;
        }

        public void setBusVoltage(int busVoltage) {
            mBusVoltage = busVoltage;
        }

        public double getCurrentMultiplier() {
            return mCurrentMultiplier;
        }

        public void setCurrentMultiplier(int currentMultiplier) {
            mCurrentMultiplier = currentMultiplier;
        }
    }


    public static class BlebMessageStorage {
        private String mMessage;
        private String mKey;
        private String mCommand;
        private boolean mPopup;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlebMessageStorage that = (BlebMessageStorage) o;
            return
                    Objects.equals(mMessage, that.mMessage) &&
                            Objects.equals(mKey, that.mKey) &&
                            Objects.equals(mCommand, that.mCommand);
        }

        public BlebMessageStorage(String message, String key, String command, boolean popup) {
            mMessage = message;
            mKey = key;
            mCommand = command;
            mPopup = popup;
        }

        public boolean isPopup() {
            return mPopup;
        }

        public void setPopup(boolean popup) {
            mPopup = popup;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }

        public String getKey() {
            return mKey;
        }

        public void setKey(String key) {
            mKey = key;
        }

        public String getCommand() {
            return mCommand;
        }

        public void setCommand(String command) {
            mCommand = command;
        }
    }


    public static class RssiFromBlebStorage {
        private int mRssi;
        private String mKey;
        private String mCommand;
        private boolean mLess;
        private boolean mEqual;
        private boolean mGreater;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RssiFromBlebStorage that = (RssiFromBlebStorage) o;
            return mRssi == that.mRssi &&
                    mLess == that.mLess &&
                    mEqual == that.mEqual &&
                    mGreater == that.mGreater &&
                    Objects.equals(mKey, that.mKey) &&
                    Objects.equals(mCommand, that.mCommand);
        }

        public RssiFromBlebStorage(int rssi, String key, String command, boolean less, boolean equal, boolean greater) {
            mRssi = rssi;
            mKey = key;
            mCommand = command;
            mLess = less;
            mEqual = equal;
            mGreater = greater;
        }

        public int getRssi() {
            return mRssi;
        }

        public void setRssi(int rssi) {
            mRssi = rssi;
        }

        public String getKey() {
            return mKey;
        }

        public void setKey(String key) {
            mKey = key;
        }

        public String getCommand() {
            return mCommand;
        }

        public void setCommand(String command) {
            mCommand = command;
        }

        public boolean isLess() {
            return mLess;
        }

        public void setLess(boolean less) {
            mLess = less;
            if (mLess) {
                mGreater = false;
                mEqual = false;
            }
        }

        public boolean isEqual() {
            return mEqual;
        }

        public void setEqual(boolean equal) {
            mEqual = equal;
            if (mEqual) {
                mGreater = false;
                mLess = false;
            }
        }

        public boolean isGreater() {
            return mGreater;
        }

        public void setGreater(boolean greater) {
            mGreater = greater;
            if (mGreater) {
                mEqual = false;
                mLess = false;
            }
        }
    }

    public static class PrintSettings {
        private boolean mIsLogging;
        private String mLogName;
        private String mIp;
        private int mNumberOfPages;
        private int mNumberOfBytes;
        private int mType;

        public PrintSettings(boolean isLogging, String logName, String ip, int numberOfPages, int numberOfBytes, int type) {
            mIsLogging = isLogging;
            mLogName = logName;
            mIp = ip;
            mNumberOfPages = numberOfPages;
            mNumberOfBytes = numberOfBytes;
            mType = type;
        }

        public int getNumberOfPages() {
            return mNumberOfPages;
        }

        public void setNumberOfPages(int numberOfPages) {
            mNumberOfPages = numberOfPages;
        }

        public int getNumberOfBytes() {
            return mNumberOfBytes;
        }

        public void setNumberOfBytes(int numberOfBytes) {
            mNumberOfBytes = numberOfBytes;
        }

        public boolean isLogging() {
            return mIsLogging;
        }

        public void setLogging(boolean logging) {
            mIsLogging = logging;
        }

        public String getLogName() {
            return mLogName;
        }

        public void setLogName(String logName) {
            mLogName = logName;
        }

        public String getIp() {
            return mIp;
        }

        public void setIp(String ip) {
            mIp = ip;
        }

        public int getType() {
            return mType;
        }

        public void setType(int type) {
            mType = type;
        }
    }

    public static boolean GetCtraceSaveToDisk(Context context, String key) {
        return GetSharedPreferences(context).getBoolean(key + "_CTRACE_SAVE_TO_DISK", false);
    }

    public static void SetCtraceSaveToDisk(Context context, String key, boolean saveToDisk) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putBoolean(key + "_CTRACE_SAVE_TO_DISK", saveToDisk);
        editor.apply();
    }

    public static boolean GetIsCTrace(Context context) {
        return BuildConfig.APPLICATION_ID.equals("it.bleb.dpi");
    }

    public static boolean GetDataloggerActivated(Context context, String address) {
        return GetSharedPreferences(context).getBoolean(address + "_DATALOGGER", false);
    }

    public static void SetDataloggerActivated(Context context, String address, boolean activated) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putBoolean(address + "_DATALOGGER", activated);
        editor.apply();
    }

    public static ArrayList<RssiFromBlebStorage> GetRssiFromBlebCommands(Context context, String address) {
        String json = GetSharedPreferences(context).getString(address + "_RSSI_FROM_BLEB", "");
        ArrayList<RssiFromBlebStorage> list;
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<RssiFromBlebStorage>>() {
            }.getType();
            list = GSON.fromJson(json, type);
        }

        return list;
    }


    public static void RemoveRssiFromBlebCommand(Context context, String address, String key) {
        if (key == null)
            return;

        ArrayList<RssiFromBlebStorage> list = GetRssiFromBlebCommands(context, address);
        boolean found = false;
        for (int i = 0; i < list.size() && !found; i++) {
            if (key.equals(list.get(0).getKey())) {
                list.remove(i);
                found = true;
            }
        }

        String json = GSON.toJson(list);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(address + "_RSSI_FROM_BLEB", json);
        editor.apply();
    }


    public static void DeleteAllRssiFromBlebCommand(Context context, String address) {
        String json = GSON.toJson(new ArrayList<RssiFromBlebStorage>());
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(address + "_RSSI_FROM_BLEB", json);
        editor.apply();
    }

    public static void AddRssiFromBlebCommand(Context context, String address, RssiFromBlebStorage command) {
        ArrayList<RssiFromBlebStorage> list = GetRssiFromBlebCommands(context, address);

        if (list.indexOf(command) < 0)
            list.add(command);

        String json = GSON.toJson(list);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(address + "_RSSI_FROM_BLEB", json);
        editor.apply();
    }

    //region BlebMessage
    public static ArrayList<BlebMessageStorage> GetBlebMessageCommands(Context context, String address) {
        String json = GetSharedPreferences(context).getString(address + "_BLEB_MESSAGE", "");
        ArrayList<BlebMessageStorage> list;
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<BlebMessageStorage>>() {
            }.getType();
            list = GSON.fromJson(json, type);
        }

        return list;
    }

    public static void RemoveBlebMessageCommand(Context context, String address, String key) {
        if (key == null)
            return;

        ArrayList<BlebMessageStorage> list = GetBlebMessageCommands(context, address);
        boolean found = false;
        for (int i = 0; i < list.size() && !found; i++) {
            if (key.equals(list.get(0).getKey())) {
                list.remove(i);
                found = true;
            }
        }

        String json = GSON.toJson(list);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(address + "_BLEB_MESSAGE", json);
        editor.apply();
    }


    public static void DeleteAllBlebMessageCommand(Context context, String address) {
        String json = GSON.toJson(new ArrayList<BlebMessageStorage>());
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(address + "_BLEB_MESSAGE", json);
        editor.apply();
    }

    public static void AddBlebMessageCommand(Context context, String address, BlebMessageStorage command) {
        ArrayList<BlebMessageStorage> list = GetBlebMessageCommands(context, address);

        if (list.indexOf(command) < 0)
            list.add(command);

        String json = GSON.toJson(list);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(address + "_BLEB_MESSAGE", json);
        editor.apply();
    }
    //endregion

    //region Gateway settings
    public static void SetGatewayAppId(Context context, String appId) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString("APP_ID", appId);
        editor.apply();
    }

    public static String GenerateGatewayAppId(Context context) {
        String appId = GetSharedPreferences(context).getString("APP_ID", null);

        if (appId == null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String imei = null; //telephonyManager.getImei();
                        if (imei != null) {
                            appId = "I_" + imei;
                        }
                    } else {
                        String id = ""; //telephonyManager.getDeviceId();
                        if (id != null) {
                            appId = "D_" + id;
                        }
                    }
                }
            }

            if (appId != null) {
                appId = appId.trim().toUpperCase();
            }

            if (appId == null || appId.length() == 0) {
                Random rnd = new Random();
                appId = String.format("R_%06x", rnd.nextInt(0x1000000)) + String.format("%06x", rnd.nextInt(0x1000000)) + String.format("%06x", rnd.nextInt(0x1000000));
                appId = appId.trim().toUpperCase();
            }

            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
                digest.update(appId.getBytes(), 0, appId.length());
                appId = BLEUtils.BytesToHex(digest.digest()).toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                Logger.Err(Prefs.class, "No MD5 algorithm!?");
            }

            Prefs.SetGatewayAppId(context, appId);
        }

        return appId;
    }
    //endregion

    public static int GetDirectInteractionDelay(Context context) {
        return GetSharedPreferences(context).getInt("DI_DELAY", 5000);
    }

    public static void SetDirectInteractionDelay(Context context, int delay) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putInt("DI_DELAY", delay);
        editor.apply();
    }

    public static String GetCallNumber(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_CALL_NUMBER", "");
    }

    public static void SetCallNumber(Context context, String key, String number) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_CALL_NUMBER", number);
        editor.apply();
    }

    public static boolean IsAdvancedMode(Context context) {
        if (Prefs.GetIsCTrace(context))
            return true;
        else
            return GetSharedPreferences(context).getBoolean("ADVANCED_MODE", false);
    }

    public static void SetAdvancedMode(Context context, boolean isAdvancedMode) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putBoolean("ADVANCED_MODE", isAdvancedMode);
        editor.apply();
    }

    public static boolean IsBackgroundScanEnabled(Context context) {
        return !Prefs.GetIsCTrace(context) && GetSharedPreferences(context).getBoolean("BACKGROUND_SCAN_ENABLED", false);
    }

    public static void SetBackgroundScanEnabled(Context context, boolean backgroundScanEnabled) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putBoolean("BACKGROUND_SCAN_ENABLED", backgroundScanEnabled);
        editor.apply();
    }

    public static String GetSoundPath(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_SOUND_PATH", "");
    }

    public static void SetSoundPath(Context context, String key, String path) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_SOUND_PATH", path);
        editor.apply();
    }

    public static String GetSmsNumber(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_SMS_NUMBER", "");
    }

    public static void SetSmsNumber(Context context, String key, String number) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_SMS_NUMBER", number);
        editor.apply();
    }

    public static String GetSmsMessage(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_SMS_MESSAGE", "");
    }

    public static void SetSmsMessage(Context context, String key, String message) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_SMS_MESSAGE", message);
        editor.apply();
    }

    public static int GetAudioDuration(Context context, String key) {
        return GetSharedPreferences(context).getInt(key + "_AUDIO_DURATION", 1000);
    }

    public static void SetAudioDuration(Context context, String key, int duration) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putInt(key + "_AUDIO_DURATION", duration);
        editor.apply();
    }


    public static boolean GetPhotoRearCamera(Context context, String key) {
        return GetSharedPreferences(context).getBoolean(key + "_PHOTO_REAR_CAMERA", true);
    }

    public static void SetPhotoRearCamera(Context context, String key, boolean rearCamera) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putBoolean(key + "_PHOTO_REAR_CAMERA", rearCamera);
        editor.apply();
    }

    public static int GetVideoDuration(Context context, String key) {
        return GetSharedPreferences(context).getInt(key + "_VIDEO_DURATION", 1000);
    }

    public static void SetVideoDuration(Context context, String key, int duration) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putInt(key + "_VIDEO_DURATION", duration);
        editor.apply();
    }

    public static boolean GetVideoRearCamera(Context context, String key) {
        return GetSharedPreferences(context).getBoolean(key + "_VIDEO_REAR_CAMERA", true);
    }

    public static void SetVideoRearCamera(Context context, String key, boolean rearCamera) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putBoolean(key + "_VIDEO_REAR_CAMERA", rearCamera);
        editor.apply();
    }

    public static String GetVideoPath(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_VIDEO_PATH", "");
    }

    public static void SetVideoPath(Context context, String key, String path) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_VIDEO_PATH", path);
        editor.apply();
    }

    public static String GetIFTTTKey(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_IFTTT_KEY", "");
    }

    public static void SetIFTTTKey(Context context, String key, String iftttKey) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_IFTTT_KEY", iftttKey);
        editor.apply();
    }

    public static String GetIFTTTEvent(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_IFTTT_EVENT", "");
    }

    public static void SetIFTTTEvent(Context context, String key, String event) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_IFTTT_EVENT", event);
        editor.apply();
    }

    public static String GetIFTTTValue1(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_IFTTT_VALUE_1", "");
    }

    public static void SetIFTTTValue1(Context context, String key, String value1) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_IFTTT_VALUE_1", value1);
        editor.apply();
    }

    public static String GetIFTTTValue2(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_IFTTT_VALUE_2", "");
    }

    public static void SetIFTTTValue2(Context context, String key, String value3) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_IFTTT_VALUE_2", value3);
        editor.apply();
    }

    public static String GetIFTTTValue3(Context context, String key) {
        return GetSharedPreferences(context).getString(key + "_IFTTT_VALUE_3", "");
    }

    public static void SetIFTTTValue3(Context context, String key, String value3) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString(key + "_IFTTT_VALUE_3", value3);
        editor.apply();
    }

    public static String GetIFTTTDefaultKey(Context context) {
        return GetSharedPreferences(context).getString("IFTTT_DEFAULT_KEY", "");
    }

    public static void SetIFTTTDefaultKey(Context context, String iftttKey) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString("IFTTT_DEFAULT_KEY", iftttKey);
        editor.apply();
    }


    public static ArrayList<IFTTTModel> GetIFTTTPresets(Context context) {
        String json = GetSharedPreferences(context).getString("IFTTT_PRESET", "");
        ArrayList<IFTTTModel> list;
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<IFTTTModel>>() {
            }.getType();
            list = GSON.fromJson(json, type);
        }

        return list;
    }

    public static void DeleteIFTTTPreset(Context context, IFTTTModel preset) {
        ArrayList<IFTTTModel> list = GetIFTTTPresets(context);
        list.remove(preset);

        String json = GSON.toJson(list);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString("IFTTT_PRESET", json);
        editor.apply();
    }

    public static void AddIFTTTPreset(Context context, IFTTTModel preset) {
        ArrayList<IFTTTModel> list = GetIFTTTPresets(context);

        if (list.indexOf(preset) < 0)
            list.add(preset);

        String json = GSON.toJson(list);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString("IFTTT_PRESET", json);
        editor.apply();
    }

    public static void DeleteByTrigger(Context context, String trigger) {
        trigger = trigger.toUpperCase().trim();

        final SharedPreferences.Editor editor = GetSharedPreferences(context).edit();

        final Map<String, ?> allEntries = GetSharedPreferences(context).getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String triggerOfKey = entry.getKey().substring(0, 4).toUpperCase().trim();
            if (triggerOfKey.equals(trigger))
                editor.remove(entry.getKey());
        }

        editor.apply();
    }

    public static void DeleteAllTriggers(Context context) {
        final SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }


    public static void SetPrintSettings(Context context, PrintSettings settings) {
        String json = GSON.toJson(settings);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString("PRINT_SETTINGS", json);
        editor.apply();
    }

    public static PrintSettings GetPrintSettings(Context context) {
        String json = GetSharedPreferences(context).getString("PRINT_SETTINGS", "");
        PrintSettings printSettings;
        if (json.isEmpty()) {
            printSettings = new PrintSettings(false, "", "0.0.0.0", 1, Prefs.GetIsCTrace(context) ? 5 : 6, 0);
        } else {
            Type type = new TypeToken<PrintSettings>() {
            }.getType();
            printSettings = GSON.fromJson(json, type);
        }

        return printSettings;
    }


    public static void SetVidWifiNetworkId(Context context, int networkId) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putInt("VID_WIFI_NETWORK_ID", networkId);
        editor.apply();
    }

    public static int GetVidWifiNetworkId(Context context) {
        return GetSharedPreferences(context).getInt("VID_WIFI_NETWORK_ID", -1);
    }

    public static void SetEspWifiNetworkId(Context context, int networkId) {
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putInt("ESP_WIFI_NETWORK_ID", networkId);
        editor.apply();
    }

    public static int GetEspWifiNetworkId(Context context) {
        return GetSharedPreferences(context).getInt("ESP_WIFI_NETWORK_ID", -1);
    }


    public static void SetRmsSettings(Context context, RmsSettings settings) {
        String json = GSON.toJson(settings);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString("RMS_SETTINGS", json);
        editor.apply();
    }

    public static RmsSettings GetRmsSettings(Context context) {
        String json = GetSharedPreferences(context).getString("RMS_SETTINGS", "");
        RmsSettings settings;
        if (json.isEmpty()) {
            settings = new RmsSettings(220000, 1);
        } else {
            Type type = new TypeToken<RmsSettings>() {
            }.getType();
            settings = GSON.fromJson(json, type);
        }
        return settings;
    }

    public static void SetCmsSettings(Context context, CmsSettings settings) {
        String json = GSON.toJson(settings);
        SharedPreferences.Editor editor = GetSharedPreferences(context).edit();
        editor.putString("CMS_SETTINGS", json);
        editor.apply();
    }

    public static CmsSettings GetCmsSettings(Context context) {
        String json = GetSharedPreferences(context).getString("CMS_SETTINGS", "");
        CmsSettings settings;
        if (json.isEmpty()) {
            settings = new CmsSettings(false, "Generic", "%", -30000000, 0, 30000000, 100);
        } else {
            Type type = new TypeToken<CmsSettings>() {
            }.getType();
            settings = GSON.fromJson(json, type);
        }
        return settings;
    }


}

