package it.bleb.dpi.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.bleb.dpi.R;
import it.bleb.dpi.utils.Common;
import it.bleb.blebandroid.Blebricks;

/**
 * Bleb Technology srl
 * https://bleb.it
 * <p>
 * 14 June 2019
 */

public class SelectDeviceDialogFragment extends DialogFragment implements Blebricks.OnAdvertiseReceivedFromScanListener {
    private static final String FRAGMENT_TAG = "fragment_dialog_select_device";
    private static final String ARGS_TITLE = "ARGS_TITLE";
    private static final String ARGS_EXCLUSIONS = "ARGS_EXCLUSIONS";

    private final ArrayList<Device> mDevices = new ArrayList<>();
    private final ArrayList<String> mExclusions = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean mEnabling = false;
    private String mSelectedAddress= null;

    private static OnDeviceSelectedListener mOnDeviceSelectedListener;
    private Activity activity;

    public interface OnDeviceSelectedListener {
        public void onDeviceSelected(String address);
    }

    public static SelectDeviceDialogFragment ShowInstance(FragmentManager fragmentManager, OnDeviceSelectedListener listener) {
        return ShowInstance(fragmentManager, listener, null, null);
    }

    public static SelectDeviceDialogFragment ShowInstance(FragmentManager fragmentManager, OnDeviceSelectedListener listener, String title) {
        return ShowInstance(fragmentManager, listener, title, null);
    }

    public static SelectDeviceDialogFragment ShowInstance(FragmentManager fragmentManager, OnDeviceSelectedListener listener, String[] exclusions) {
        return ShowInstance(fragmentManager, listener, null, exclusions);
    }

    public static SelectDeviceDialogFragment ShowInstance(FragmentManager fragmentManager, OnDeviceSelectedListener listener, String title, String[] exclusions) {
        mOnDeviceSelectedListener = listener;
        Blebricks.setHasToAnalyze(false);

        // remove previous instance
        Fragment frag = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (frag != null) fragmentManager.beginTransaction().remove(frag).commit();

        SelectDeviceDialogFragment dialogFragment = new SelectDeviceDialogFragment();

        Bundle args = new Bundle();
        if(title != null) args.putString(ARGS_TITLE, title);
        if(exclusions != null) args.putStringArray(ARGS_EXCLUSIONS, exclusions);
        dialogFragment.setArguments(args);

        dialogFragment.show(fragmentManager, FRAGMENT_TAG);

        return dialogFragment;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Blebricks.setHasToAnalyze(true);
        Common.StopScan();

        if(mOnDeviceSelectedListener != null) {
            mOnDeviceSelectedListener.onDeviceSelected(mSelectedAddress);
            mOnDeviceSelectedListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_device_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString(ARGS_TITLE, "Select a device from the list:");
        getDialog().setTitle(title);

        String[] exclusions = getArguments().getStringArray(ARGS_EXCLUSIONS);
        if(exclusions != null) {
            for(String e : exclusions) {
                if(e != null)
                    mExclusions.add(e);
            }
        }

        TextView txtTitle = view.findViewById(R.id.dialog_select_device_title);
        txtTitle.setText(title);

        mRecyclerView = view.findViewById(R.id.recycler_devices);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new DevicesAdapter(mDevices);
        mRecyclerView.setAdapter(mAdapter);

        Common.EnableAdaptersAndStartScan(null, getActivity(), new Blebricks.OnScanListener() {
            @Override
            public void OnScanStarted() {
            }

            @Override
            public void OnScanFailed(int errorCode) {
            }

            @Override
            public void OnScanStopped() {
            }
        }, null, this);
    }

    private View.OnTouchListener mDeviceTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                int itemPosition = mRecyclerView.getChildLayoutPosition(v);
                if(itemPosition >= 0 && itemPosition < mDevices.size()) {
                    Device selected = mDevices.get(itemPosition);
                    mSelectedAddress = selected.getAddress();
                    SelectDeviceDialogFragment.this.dismiss();
                    return false;
                }
            }
            return true;
        }
    };

    @Override
    public void OnAdvertiseReceived(String address, String name, int rssi, String manufacturerData, int battery, String rawData) {
        boolean found = false;
        for(Device d : mDevices) {
            if(d.getAddress().trim().toUpperCase().equals(address.trim().toUpperCase())) {
                d.setName(name);
                d.setDbm(rssi);
                found = true;
            }
        }
        for(String exclusion : mExclusions) {
            if(address.trim().toUpperCase().equals(exclusion.trim().toUpperCase()))
                found = true;
        }

        if(!found)
            mDevices.add(new Device(name, address, rssi));
        mAdapter.notifyDataSetChanged();
    }

    private class Device {
        private String name;
        private String address;
        private int dbm;

        Device(String name, String address, int dbm) {
            this.name = name;
            this.address = address;
            this.dbm = dbm;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        String getAddress() {
            return address;
        }

        void setAddress(String address) {
            this.address = address;
        }

        int getDbm() {
            return dbm;
        }

        void setDbm(int dbm) {
            this.dbm = dbm;
        }
    }

    private class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> {
        private ArrayList<Device> mDataset;

        class DevicesViewHolder extends RecyclerView.ViewHolder {
            private ConstraintLayout mLayout;
            private TextView mName;
            private TextView mAddress;
            private TextView mDbm;

            DevicesViewHolder(@NonNull final ConstraintLayout v) {
                super(v);
                mLayout = v;
                mName = mLayout.findViewById(R.id.di_desc);
                mAddress = mLayout.findViewById(R.id.txt_address);
                mDbm = mLayout.findViewById(R.id.txt_dbm);
            }

            void setName(String name) {
                mName.setText(name);
            }

            void setAddress(String address) {
                mAddress.setText(address);
            }

            void setDbm(int dbm) {
                mDbm.setText(getResources().getString(R.string.selection_dbm, dbm));
            }

        }

        DevicesAdapter(final ArrayList<Device> dataset) {
            mDataset = dataset;
        }

        @NonNull
        @Override
        public DevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_device_dialog, parent, false);
            v.setOnTouchListener(mDeviceTouchListener);
            return new DevicesAdapter.DevicesViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DevicesAdapter.DevicesViewHolder holder, int position) {
            holder.setName(mDataset.get(position).getName());
            holder.setAddress(mDataset.get(position).getAddress());
            holder.setDbm(mDataset.get(position).getDbm());

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
