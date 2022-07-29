package it.bleb.dpi.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import it.bleb.dpi.R;
import it.bleb.dpi.activities.HomeActivity;

import static it.bleb.dpi.receivers.DpiScanReceiver.SEND_ERROR_ACTION;

public class DpiErrorManager {

    private int mId = 0;
    private boolean isErrorSent;
    private Context context;
    private Notification notification;
    private static final String TAG = "DpiErrorManager";
    private static final String CHANNEL_ID = "ErrorMessages";

    public DpiErrorManager(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public boolean isErrorSent() {
        return isErrorSent;
    }

    public void setErrorSent(boolean errorSent) {
        isErrorSent = errorSent;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    /**
     * Sending error message to another app
     * DPI status
     *
     * @param send
     */
    public void sendError(boolean send) {
        //Communication amongst apps
        context.sendBroadcast(new Intent().setAction(SEND_ERROR_ACTION)
                .putExtra("isError", send)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES));
        setErrorSent(send);
    }

    /**
     * Set smartphone notification
     */
    public void setNotification(String modelName, boolean noBeaconSignal) {
        Intent i = new Intent(context, HomeActivity.class);
        i//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("isError", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String nomeDpi = translateModelName(modelName);
        nomeDpi = nomeDpi.equals("") ? modelName : nomeDpi;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_ctrace_antipanic)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ctrace_antipanic))
                .setContentTitle(context.getResources().getString(R.string.warning_title))
                .setContentText(noBeaconSignal ? nomeDpi + " " + context.getResources().getString(R.string.text_notification_nosignal) : context.getResources().getString(R.string.text_notification) + " " + nomeDpi)
                .setSound(alarmSound)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(noBeaconSignal ? nomeDpi + " " + context.getResources().getString(R.string.text_notification_nosignal) : context.getResources().getString(R.string.text_notification) + " " + nomeDpi))
                .setAutoCancel(true);

        notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mId, notification);
        mId++;
    }

    public String translateModelName(String modelName) {
        String nomeDpi = null;
        switch (modelName) {
            case Constants.MODEL_HELMET:
                nomeDpi = getContext().getString(R.string.label_helmet);
                break;
            case Constants.MODEL_JACKET:
                nomeDpi = getContext().getString(R.string.label_jacket);
                break;
            case Constants.MODEL_LEFT_GLOVE:
                nomeDpi = getContext().getString(R.string.label_left_glove);
                break;
            case Constants.MODEL_RIGHT_GLOVE:
                nomeDpi = getContext().getString(R.string.label_right_glove);
                break;
            case Constants.MODEL_TROUSERS:
                nomeDpi = getContext().getString(R.string.label_trousers);
                break;
            case Constants.MODEL_LEFT_SHOE:
                nomeDpi = getContext().getString(R.string.label_left_shoe);
                break;
            case Constants.MODEL_RIGHT_SHOE:
                nomeDpi = getContext().getString(R.string.label_right_shoe);
                break;
            case Constants.MODEL_GOGGLES:
                nomeDpi = getContext().getString(R.string.label_goggles);
                break;
            case Constants.MODEL_BADGE:
                nomeDpi = getContext().getString(R.string.label_badge);
                break;
            case Constants.MODEL_EARMUFFS:
                nomeDpi = getContext().getString(R.string.label_ear_muffs);
                break;
            case Constants.MODEL_HARNESS:
                nomeDpi = getContext().getString(R.string.label_harness);
                break;
            default:
                nomeDpi = "";
        }
        return nomeDpi;
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DPI_notifiche";
            String description = "Alert_beacon";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
