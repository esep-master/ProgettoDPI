package it.bleb.dpi.utils;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.bleb.dpi.R;

public class DebugMessageManager {
    private static final ArrayList<DebugMessage> mManufacturerData = new ArrayList<>();
    private static final RecyclerView.Adapter mManufacturerDataAdapter = new DebugMessageAdapter(mManufacturerData);

    private static final ArrayList<DebugMessage> mCommands = new ArrayList<>();
    private static final RecyclerView.Adapter mCommandsAdapter = new DebugMessageAdapter(mCommands);

    private static WeakReference<Activity> mActivity = null;

    public static class DebugMessage {
        private String mMessage;
        private String mTime;

        public DebugMessage(String message) {
            this.mMessage = message;

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            this.mTime = "[" + sdf.format(Calendar.getInstance().getTime()) + "]";
        }

        public String getTime() {
            return mTime;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            this.mMessage = message;
        }
    }

    public static class DebugMessageAdapter extends RecyclerView.Adapter<DebugMessageAdapter.DebugMessageViewHolder> {
        private ArrayList<DebugMessage> mDataset;

        class DebugMessageViewHolder extends RecyclerView.ViewHolder {
            private ConstraintLayout mLayout;
            private TextView mMessage;
            private TextView mTime;

            DebugMessageViewHolder(@NonNull final ConstraintLayout v) {
                super(v);
                mLayout = v;
                //mMessage = mLayout.findViewById(R.id.debug_message);
                //mTime = mLayout.findViewById(R.id.debug_time);
            }

            void setMessage(String message) {
                mMessage.setText(message);
            }

            void setTime(String time) {
                mTime.setText(time);
            }
        }

        DebugMessageAdapter(final ArrayList<DebugMessage> dataset) {
            mDataset = dataset;
        }

        @NonNull
        @Override
        public DebugMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final ConstraintLayout v = null;//(ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_debug, parent, false);
            return new DebugMessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DebugMessageViewHolder holder, int position) {
            holder.setMessage(mDataset.get(position).getMessage());
            holder.setTime(mDataset.get(position).getTime());
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public static void setActivity(Activity activity) {
        if (activity != null && (mActivity == null || mActivity.get() == null || mActivity.get() != activity))
            mActivity = new WeakReference<>(activity);
    }

    public static RecyclerView.Adapter getManufacturerDataAdapter() {
        return mManufacturerDataAdapter;
    }

    public static RecyclerView.Adapter getCommandsAdapter() {
        return mCommandsAdapter;
    }

    public static void AddManufacturerData(String manufacturerData) {
        while (mManufacturerData.size() > 150)
            mManufacturerData.remove(mManufacturerData.size() - 1);

        mManufacturerData.add(0, new DebugMessage(manufacturerData));

        if (mActivity != null && mActivity.get() != null) {
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mManufacturerDataAdapter.notifyDataSetChanged();
                }
            });
        } else
            mManufacturerDataAdapter.notifyDataSetChanged();
    }

    public enum CommandStatus {
        DONE, FAILED, STOPPED
    }

    public static void AddCommand(CommandStatus status, String datatype, String value) {
        while (mCommands.size() > 30)
            mCommands.remove(mCommands.size() - 1);

        String statusStr;
        switch (status) {
            case DONE:
                statusStr = "Done";
                break;
            case FAILED:
                statusStr = "Failed";
                break;
            case STOPPED:
                statusStr = "Stopped";
                break;
            default:
                statusStr = "";
                break;
        }

        mCommands.add(0, new DebugMessage(statusStr + ": " + datatype + value));

        if (mActivity != null && mActivity.get() != null) {
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCommandsAdapter.notifyDataSetChanged();
                }
            });
        } else
            mCommandsAdapter.notifyDataSetChanged();
    }

    private static double mLatitude = 0;
    private static double mLongitude = 0;
    private static double mAltitude = 0;

    public static double getLatitude() {
        return mLatitude;
    }

    public static double getLongitude() {
        return mLongitude;
    }

    public static double getAltitude() {
        return mAltitude;
    }

    public static void SetCoordinate(double latitude, double longitude, double altitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        mAltitude = altitude;

        if (mOnCoordinateUpdateListener != null) {
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnCoordinateUpdateListener.OnCoordinateUpdate(mLatitude, mLongitude, mAltitude);
                    }
                });
            } else
                mOnCoordinateUpdateListener.OnCoordinateUpdate(mLatitude, mLongitude, mAltitude);
        }
    }

    private static OnCoordinateUpdateListener mOnCoordinateUpdateListener = null;

    public static void setOnCoordinateUpdateListener(OnCoordinateUpdateListener onCoordinateUpdateListener) {
        mOnCoordinateUpdateListener = onCoordinateUpdateListener;
    }

    public interface OnCoordinateUpdateListener {
        void OnCoordinateUpdate(double latitude, double longitude, double altitude);
    }
}
