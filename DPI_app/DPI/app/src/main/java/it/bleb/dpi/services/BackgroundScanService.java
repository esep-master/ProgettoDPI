package it.bleb.dpi.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.bleb.blebandroid.BLEB;
import it.bleb.blebandroid.Blebricks;
import it.bleb.blebandroid.CTrace;
import it.bleb.blebandroid.DirectInteraction;
import it.bleb.blebandroid.utils.Logger;
import it.bleb.blebandroid.utils.Property;
import it.bleb.dpi.activities.SplashActivity;
import it.bleb.dpi.utils.CtraceCsvFile;
import it.bleb.dpi.utils.Prefs;
import it.bleb.dpi.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackgroundScanService extends Service {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int DELAY_AFTER_RESUME_DI = 3000;

    private Handler mDelayAfterResumeHandler = new Handler();

    private static BackgroundScanService instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public static String CHANNEL_ID = null;

    private int DELAY_DI_LOCK = 5000;

    private MediaPlayer mMediaPlayer;

    private boolean mVibrateLock = false;
    private Handler mVibrateHandler = new Handler();
    private boolean mPlaySoundLock = false;
    private Handler mPlaySoundHandler = new Handler();
    private boolean mStopSoundLock = false;
    private Handler mStopSoundHandler = new Handler();
    private boolean mSmsLock = false;
    private Handler mSmsHandler = new Handler();
    private boolean mCallLock = false;
    private Handler mCallHandler = new Handler();
    private boolean mRecordSoundLock = false;
    private Handler mRecordSoundHandler = new Handler();
    private boolean mPhotoLock = false;
    private Handler mPhotoHandler = new Handler();
    private boolean mVideoLock = false;
    private Handler mVideoHandler = new Handler();
    private boolean mPlayVideoLock = false;
    private Handler mPlayVideoHandler = new Handler();
    private boolean mIFTTTLock = false;
    private Handler mIFTTTHandler = new Handler();
    private OkHttpClient mHttpClient = new OkHttpClient();
    private Handler mRssiFromThisBlebHandler = new Handler();
    private boolean mPopupLock = false;
    private Handler mPopupHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(CHANNEL_ID == null) {
                if(Prefs.GetIsCTrace(getApplicationContext()))
                    CHANNEL_ID =  "MyProtector";
                else
                    CHANNEL_ID =  "MakeApp";
            }

            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Background Scan", NotificationManager.IMPORTANCE_MIN);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        if(CHANNEL_ID == null) {
            if(Prefs.GetIsCTrace(getApplicationContext()))
                CHANNEL_ID =  "MyProtector";
            else
                CHANNEL_ID =  "MakeApp";
        }

        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Scanning devices...")
                .setContentText("You can disable the background scanning from the top-right menu in the main screen of the application.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        mDelayAfterResumeHandler.removeCallbacksAndMessages(null);
        mDelayAfterResumeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVibrateLock = false;
                mPlaySoundLock = false;
                mStopSoundLock = false;
                mSmsLock = false;
                mCallLock = false;
                mRecordSoundLock = false;
                mPhotoLock = false;
                mVideoLock = false;
                mPlayVideoLock = false;
                mIFTTTLock = false;
                mPopupLock = false;
            }
        }, DELAY_AFTER_RESUME_DI);

        mVibrateHandler.removeCallbacksAndMessages(null);
        mVibrateLock = true;

        mPlaySoundHandler.removeCallbacksAndMessages(null);
        mPlaySoundLock = true;

        mStopSoundHandler.removeCallbacksAndMessages(null);
        mStopSoundLock = true;

        mSmsHandler.removeCallbacksAndMessages(null);
        mSmsLock = true;

        mCallHandler.removeCallbacksAndMessages(null);
        mCallLock = true;

        mRecordSoundHandler.removeCallbacksAndMessages(null);
        mRecordSoundLock = true;

        mPhotoHandler.removeCallbacksAndMessages(null);
        mPhotoLock = true;

        mVideoHandler.removeCallbacksAndMessages(null);
        mVideoLock = true;

        mPlayVideoHandler.removeCallbacksAndMessages(null);
        mPlayVideoLock = true;

        mIFTTTHandler.removeCallbacksAndMessages(null);
        mIFTTTLock = true;

        mPopupHandler.removeCallbacksAndMessages(null);
        mPopupLock = true;

        DELAY_DI_LOCK = Prefs.GetDirectInteractionDelay(this);
        scanForDirectInteraction();

        return START_NOT_STICKY;
    }

    private void scanForDirectInteraction() {
        Blebricks.StartGlobalScan(new Blebricks.OnScanListener() {
            @Override
            public void OnScanStarted() {

            }

            @Override
            public void OnScanFailed(int errorCode) {

            }

            @Override
            public void OnScanStopped() {

            }
        }, false);

        Blebricks.SetOnAdvertiseReceivedFromScanListener(new Blebricks.OnAdvertiseReceivedFromScanListener() {
            @Override
            public void OnAdvertiseReceived(final String address, final String name, final int rssi, final String manufacturerData, final int battery, final String rawData) {
                ArrayList<Prefs.RssiFromBlebStorage> rssiFromBlebStorageList = Prefs.GetRssiFromBlebCommands(BackgroundScanService.this, address);
                for (Prefs.RssiFromBlebStorage rssiFromBlebStorage : rssiFromBlebStorageList) {
                    Logger.Log(this, String.format(Locale.US, "RSSI: %d; Comparing to: %d; Comparator: %s; Command: %s; Key: %s", rssi, rssiFromBlebStorage.getRssi(), rssiFromBlebStorage.isEqual() ? "EQUAL" : rssiFromBlebStorage.isGreater() ? "GREATER" : rssiFromBlebStorage.isLess() ? "LESSER" : "", rssiFromBlebStorage.getCommand(), rssiFromBlebStorage.getKey()));
                    if ((rssiFromBlebStorage.isEqual() && rssi == rssiFromBlebStorage.getRssi()) ||
                            (rssiFromBlebStorage.isGreater() && rssi >= rssiFromBlebStorage.getRssi()) ||
                            (rssiFromBlebStorage.isLess() && rssi <= rssiFromBlebStorage.getRssi())) {
                        diExecute(rssiFromBlebStorage.getCommand(), rssiFromBlebStorage.getKey());
                    }
                }

                mRssiFromThisBlebHandler.removeCallbacksAndMessages(null);
                mRssiFromThisBlebHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OnAdvertiseReceived(address, name, -128, manufacturerData, battery, rawData);
                    }
                }, 5000);
            }
        });

        Blebricks.Components.BLEB.ScanEvents.setOnPersonalMessageListener(new BLEB.ScanEvents.OnPersonalMessageListener() {
            @Override
            public void OnPersonalMessageReceived(String address, String message) {
                ArrayList<Prefs.BlebMessageStorage> blebMessageCommands = Prefs.GetBlebMessageCommands(BackgroundScanService.this, address);
                for (Prefs.BlebMessageStorage blebMessageStorage : blebMessageCommands) {
                    Logger.Log(this, String.format(Locale.US, "Message: %s; Comparing to: %s; Command: %s; Key: %s", message, blebMessageStorage.getMessage(), blebMessageStorage.getCommand(), blebMessageStorage.getKey()));
                    if (blebMessageStorage.getMessage() != null && (blebMessageStorage.getMessage().equals("") || blebMessageStorage.getMessage().equals(message))) {
                        if (blebMessageStorage.isPopup()) {
                            mPopupHandler.removeCallbacksAndMessages(null);
                            mPopupHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mPopupLock = false;
                                }
                            }, DELAY_DI_LOCK);

                            if (!mPopupLock) {
                                mPopupLock = true;
                                Toast.makeText(BackgroundScanService.this, "Received message: " + message, Toast.LENGTH_LONG).show();
                            }
                        }

                        diExecute(blebMessageStorage.getCommand(), blebMessageStorage.getKey());
                    }
                }
            }
        });

        Blebricks.Components.DirectInteraction.ScanEvents.setOnDirectInteractionMessageListener(new DirectInteraction.ScanEvents.OnDirectInteractionMessageListener() {
            @Override
            public void OnDirectInteractionMessageReceived(String address, String target, String command) {
                Logger.Log(this, "Received Direct Interaction message.\nTarget: " + target + "\nCommand: " + command);
                if (target.toUpperCase().trim().equals("FF:FF:FF:FF:FF:FF")) {
                    String key = "";
                    if (command.length() > 2) {
                        if (command.length() > 12)
                            key = command.substring(2, 12);
                        else
                            key = command.substring(2);

                        command = command.substring(0, 2);
                    }

                    diExecute(command, key);
                }
            }
        });
    }

    private Handler mReadingHandler = new Handler();
    private boolean mReading = false;

    private synchronized void readLog(String address) {
        if (!mReading) {
            mReading = true;
            mReadingHandler.removeCallbacksAndMessages(null);

            Blebricks.ConnectTo(address, new Blebricks.OnConnectionListener() {
                @Override
                public void OnConnectionFailed(String address) {
                    onReadError("connection error");
                }

                @Override
                public void OnConnectionStopped(String address) {
                    onReadError("disconnected");
                }

                @Override
                public void OnConnectionDone(String address, String name) {
                    Blebricks.StartNotificationFromConnection(Blebricks.Components.CTrace.Properties.getLogValues(), new Blebricks.OnStartNotificationFromConnectionListener() {
                        @Override
                        public void OnStartNotificationFromConnectionDone(final String address, Property property) {
                            final ArrayList<CTrace.LogEvent> logs = new ArrayList<>();

                            final Runnable inCaseOfError = new Runnable() {
                                @Override
                                public void run() {
                                    Logger.Warn(this, "Timeout, sending anyway..");

                                    mReadingHandler.removeCallbacksAndMessages(null);
                                    onFinishReading(address, logs);
                                }
                            };
                            final int errorTimeout = 2000;
                            mReadingHandler.postDelayed(inCaseOfError, errorTimeout);

                            Blebricks.Components.CTrace.ConnectionEvents.setOnLogDataListener(new CTrace.ConnectionEvents.OnLogDataListener() {
                                @Override
                                public void OnLogDataReceived(String address, CTrace.LogEvent logEvent1, CTrace.LogEvent logEvent2) {
                                    if (logEvent1.getTimestamp() != 0xffffffff) {
                                        logs.add(logEvent1);
                                        Logger.Log(this, "LOG EVENT: " + logEvent1.toString());

                                        if (logEvent2.getTimestamp() != 0xffffffff) {
                                            logs.add(logEvent2);
                                            Logger.Log(this, "LOG EVENT: " + logEvent2.toString());

                                            mReadingHandler.removeCallbacksAndMessages(null);
                                            mReadingHandler.postDelayed(inCaseOfError, errorTimeout);
                                        } else {
                                            mReadingHandler.removeCallbacksAndMessages(null);
                                            onFinishReading(address, logs);
                                        }
                                    } else {
                                        mReadingHandler.removeCallbacksAndMessages(null);
                                        onFinishReading(address, logs);
                                    }
                                }
                            });
                        }

                        @Override
                        public void OnStartNotificationFromConnectionFailed(String address, Property property) {
                            onReadError("notification");
                        }
                    });
                }
            });
        }
    }

    private void onReadError(String motivation) {
        Logger.Warn(this, "Something went wrong reading ctrace log data (" + motivation + ")");
        Blebricks.Components.CTrace.ConnectionEvents.setOnLogDataListener(null);
        Blebricks.Disconnect();
        Blebricks.StartGlobalScan(new Blebricks.OnScanListener() {
            @Override
            public void OnScanStarted() {

            }

            @Override
            public void OnScanFailed(int errorCode) {

            }

            @Override
            public void OnScanStopped() {

            }
        }, false);
    }

    private void recursiveSendLog(final String address, final ArrayList<CTrace.LogEvent> logs, final int index, final int batch) {
        if (index < logs.size()) {
            ArrayList<CTrace.LogEvent> sendingLogs = new ArrayList<>();
            for(int i = index; i < logs.size() && i < index + batch; i++) {
                CTrace.LogEvent log = logs.get(i);
                Logger.Log(this, String.format(Locale.US, "CTRACE LOG = %d - %s : %d", log.getTimestamp(), log.getAddress(), log.getRssi()));
                sendingLogs.add(log);
            }

            JSONArray jsonArray = new JSONArray();
            for(CTrace.LogEvent log : sendingLogs) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("timestamp", log.getTimestamp());
                    jsonObject.put("address", log.getAddress());
                    jsonObject.put("rssi", log.getRssi());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    Logger.Warn(this, "Error creating JSON for CTRACE: " + e.toString());
                    return;
                }
            }

            String url = "https://api-bleb.it/api/v1/ctrace/" + address;
            String bodyString = jsonArray.toString();

            Logger.Log(this, "POST for CTRACE log: " + url + " " + bodyString);

            RequestBody body = RequestBody.create(bodyString, JSON);
            final Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            mHttpClient.newCall(request).enqueue(
                    new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Logger.Warn(this, "CTRACE call failed: " + e.toString());
                            recursiveSendLog(address, logs, index, batch);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            try {
                                Logger.Log(this, "CTRACE call done with status " + response.code() + ": " + response.body().string());
                                recursiveSendLog(address, logs, index + batch, batch);
                            } catch (IOException | NullPointerException ignore) {
                                Logger.Log(this, "CTRACE call done with status " + response.code());
                                recursiveSendLog(address, logs, logs.size(), batch);
                            }
                        }
                    }
            );
        } else {
            mReading = false;
            Blebricks.StartGlobalScan(new Blebricks.OnScanListener() {
                @Override
                public void OnScanStarted() {

                }

                @Override
                public void OnScanFailed(int errorCode) {

                }

                @Override
                public void OnScanStopped() {

                }
            }, false);
        }
    }

    private void onFinishReading(String address, ArrayList<CTrace.LogEvent> logs) {
        Blebricks.Components.CTrace.ConnectionEvents.setOnLogDataListener(null);
        Blebricks.Disconnect();

        recursiveSendLog(address, logs, 0, 1000);

        if (Prefs.GetCtraceSaveToDisk(getApplicationContext(), address)) {
            try{
                CtraceCsvFile.Init(address, "Protector");
                for (CTrace.LogEvent log : logs) {
                    CtraceCsvFile.Append(log.getTimestamp(), log.getAddress(), log.getRssi());
                }
            } finally {
                try {
                    CtraceCsvFile.Close();
                } catch(Exception ignore) {

                }
            }
        }
    }


    private void diExecute(String command, String key) {
        Logger.Log(this, "Executing D.I. Command: " + command + " (with key: " + key + ")");
        switch (command) {
            case "00":
                // Vibrate
                mVibrateHandler.removeCallbacksAndMessages(null);
                mVibrateHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVibrateLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mVibrateLock) {
                    mVibrateLock = true;
                    diVibrate();
                }
                break;
            case "01":
                // Play Sound
                mPlaySoundHandler.removeCallbacksAndMessages(null);
                mPlaySoundHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPlaySoundLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mPlaySoundLock) {
                    mPlaySoundLock = true;
                    diPlaySound(Prefs.GetSoundPath(getApplicationContext(), key));
                }
                break;
            case "02":
                // Stop Sound
                mStopSoundHandler.removeCallbacksAndMessages(null);
                mStopSoundHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mStopSoundLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mStopSoundLock) {
                    mStopSoundLock = true;
                    diStopSound();
                }
                break;
            case "03":
                // Send SMS
                mSmsHandler.removeCallbacksAndMessages(null);
                mSmsHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSmsLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mSmsLock) {
                    mSmsLock = true;
                    diSendSms(Prefs.GetSmsNumber(getApplicationContext(), key), Prefs.GetSmsMessage(getApplicationContext(), key));
                }
                break;
            case "04":
                // Make a call
                mCallHandler.removeCallbacksAndMessages(null);
                mCallHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCallLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mCallLock) {
                    mCallLock = true;
                    diCallPhone(Prefs.GetCallNumber(getApplicationContext(), key));
                }
                break;
            case "05":
                // Record sound
                mRecordSoundHandler.removeCallbacksAndMessages(null);
                mRecordSoundHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecordSoundLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mRecordSoundLock) {
                    mRecordSoundLock = true;
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        diRecordAudio(Prefs.GetAudioDuration(getApplicationContext(), key));
                }
                break;
            case "06":
                // Take a photo
                mPhotoHandler.removeCallbacksAndMessages(null);
                mPhotoHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mPhotoLock) {
                    mPhotoLock = true;
                    //if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        //diTakePhoto(Prefs.GetPhotoRearCamera(getApplicationContext(), key));
                }
                break;
            case "07":
                // Make a video
                mVideoHandler.removeCallbacksAndMessages(null);
                mVideoHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVideoLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mVideoLock) {
                    mVideoLock = true;
                    //if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        //diTakeVideo(Prefs.GetVideoDuration(getApplicationContext(), key), Prefs.GetVideoRearCamera(getApplicationContext(), key));
                }
                break;
            case "08":
                // Play Video
                mPlayVideoHandler.removeCallbacksAndMessages(null);
                mPlayVideoHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPlayVideoLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mPlayVideoLock) {
                    mPlayVideoLock = true;
                    diPlayVideo(Prefs.GetVideoPath(getApplicationContext(), key));
                }
                break;

            case "09":
                // IFTTT
                mIFTTTHandler.removeCallbacksAndMessages(null);
                mIFTTTHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIFTTTLock = false;
                    }
                }, DELAY_DI_LOCK);

                if (!mIFTTTLock) {
                    mIFTTTLock = true;
                    diIFTTT(Prefs.GetIFTTTKey(getApplicationContext(), key),
                            Prefs.GetIFTTTEvent(getApplicationContext(), key),
                            Prefs.GetIFTTTValue1(getApplicationContext(), key),
                            Prefs.GetIFTTTValue2(getApplicationContext(), key),
                            Prefs.GetIFTTTValue3(getApplicationContext(), key));
                }
                break;
        }
    }

    private void diVibrate() {
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {
                0, 500, 250, 500, 250, 1000
        };
        int[] amplitudes = new int[]{
                0, 255, 0, 255, 0, 255
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            v.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1));
        }else{
            v.vibrate(pattern, -1);
        }
        Toast.makeText(getApplicationContext(), "Vibrating!", Toast.LENGTH_SHORT).show();
    }

    private void diPlaySound(String path) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        Logger.Log(this, "Playing sound: " + path);

        File f = new File(path);
        Uri uri = null;
        if (f.exists() && f.canRead())
            uri = Uri.fromFile(f);

        if (uri == null)
            mMediaPlayer = null;//MediaPlayer.create(getApplicationContext(), R.raw.directinteraction);
        else
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;

                Toast.makeText(getApplicationContext(), "Sound executed!", Toast.LENGTH_SHORT).show();
            }
        });
        mMediaPlayer.start();
    }

    private void diStopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void diSendSms(String number, String message) {
        Logger.Log(this, "Sending sms to " + number + ": " + message);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("smsto:" + number));
        intent.putExtra("sms_body", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    private void diCallPhone(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    private void diRecordAudio(int time) {
        final Handler stopRecordingHandler = new Handler();
        final MediaRecorder recorder = new MediaRecorder();

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy-HHmmss", Locale.US);
        String filename = sdf.format(Calendar.getInstance().getTime()) + "-recording.3gp";

        File makeAppDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "MakeApp");
        if (!makeAppDir.exists())
            makeAppDir.mkdirs();
        File file = new File(makeAppDir, filename);

        Toast.makeText(getApplicationContext(), "Recording audio!", Toast.LENGTH_SHORT).show();
        stopRecordingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recorder.stop();
                recorder.release();
                Toast.makeText(getApplicationContext(), "Recording finished!", Toast.LENGTH_SHORT).show();
            }
        }, time);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(file.getAbsolutePath());
        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Logger.Err(this, "Error during recording: " + what + " - " + extra);
                stopRecordingHandler.removeCallbacksAndMessages(null);
                Toast.makeText(getApplicationContext(), "Something went wrong recording the audio..", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Logger.Err(this, e.toString());
        }
    }

    private void diPlayVideo(String path) {
        Logger.Log(this, "Playing video: " + path);

        File f = new File(path);
        Uri uri = null;
        if (f.exists() && f.canRead())
            uri = null;//FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", f);

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private void diIFTTT(String key, String event, String value1, String value2, String value3) {
        Logger.Log(this, "Calling IFTTT event: " + key + " " + event + " " + value1 + " " + value2 + " " + value3);
        RequestBody body;
        JSONObject jsonObject = new JSONObject();
        String url = "https://maker.ifttt.com/trigger/" + event + "/with/key/" + key;
        try {
            jsonObject.put("value1", value1);
            jsonObject.put("value2", value2);
            jsonObject.put("value3", value3);
            String bodyString = jsonObject.toString();
            Logger.Log(this, "POST for IFTTT event: " + url + " " + bodyString);

            body = RequestBody.create(bodyString, JSON);
        } catch (JSONException e) {
            Logger.Warn(this, "Error creating JSON for IFTTT: " + e.toString());
            return;
        }

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        mHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Logger.Log(this, "IFTTT call failed: " + e.toString());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            Logger.Log(this, "IFTTT call done with status " + response.code() + ": " + response.body().string());
                        } catch (IOException | NullPointerException ignore) {
                            Logger.Log(this, "IFTTT call done with status " + response.code());
                        }
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;

        mRssiFromThisBlebHandler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
