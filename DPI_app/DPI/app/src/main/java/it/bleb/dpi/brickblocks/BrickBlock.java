package it.bleb.dpi.brickblocks;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import it.bleb.blebandroid.Blebricks;
import it.bleb.blebandroid.utils.Command;
import it.bleb.dpi.activities.HomeActivity;
import it.bleb.dpi.utils.Common;
import it.bleb.dpi.utils.DebugMessageManager;

public abstract class BrickBlock implements Blebricks.OnOneShotListener {
    protected static boolean mBlocked = false;
    private FragmentActivity mActivity;
    private String mAddress;
    private Handler mRemoverHandler = new Handler();
    private OnHasToScrollToBlockListener mOnHasToScrollToBlockListener;

    public BrickBlock(final FragmentActivity activity, final String address, final OnHasToScrollToBlockListener onHasToScrollToBlockListener) {
        mActivity = activity;
        mAddress = address;
        mOnHasToScrollToBlockListener = onHasToScrollToBlockListener;
    }

    public String getAddress() {
        return mAddress;
    }

    public FragmentActivity getActivity() {
        return mActivity;
    }

    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    public abstract void init();

    public abstract void onResume();

    public abstract void onPause();

    public void onActivityResult(final int requestCode, final int resultCode, final @Nullable Intent data) {

    }

    protected void executeOneShotCommand(final Command command) {
        if (!mBlocked) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beforeCommand();
                }
            });

            Blebricks.OneShotCommand(getAddress(), command, this);
        }
    }

    @Override
    public void OnOneShotCommandDone(String address, Command command) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                afterCommand();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Command successfully executed!", Toast.LENGTH_SHORT).show();
            }
        });

        DebugMessageManager.AddCommand(DebugMessageManager.CommandStatus.DONE, command.getDataType(), command.getValue());
    }

    @Override
    public void OnOneShotCommandFailed(String address, Command command) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                afterCommand();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Something went wrong, please try again..", Toast.LENGTH_SHORT).show();
            }
        });

        DebugMessageManager.AddCommand(DebugMessageManager.CommandStatus.FAILED, command.getDataType(), command.getValue());
    }

    @Override
    public void OnOneShotCommandStopped(String address, Command command) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                afterCommand();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Command stopped!", Toast.LENGTH_SHORT).show();
            }
        });

        DebugMessageManager.AddCommand(DebugMessageManager.CommandStatus.STOPPED, command.getDataType(), command.getValue());
    }


    protected void beforeCommand() {
        mBlocked = true;
        //HomeActivity.setStuckOnPage(0);
    }

    protected void afterCommand() {
        mBlocked = false;
        //HomeActivity.setStuckOnPage(-1);

        Common.EnableAdaptersAndStartScan(getAddress(), getActivity(), null, null, (Blebricks.OnAdvertiseReceivedFromScanListener) getActivity());
    }

    public void hideContainer() {
        mRemoverHandler.removeCallbacksAndMessages(null);
    }

    public void showContainer(final boolean withTimer) {
    }

    public interface OnHasToScrollToBlockListener {
        void onHasToScrollToBlock(final View container);
    }

    public OnHasToScrollToBlockListener getOnHasToScrollToBlockListener() {
        return mOnHasToScrollToBlockListener;
    }

    public void setOnHasToScrollToBlockListener(OnHasToScrollToBlockListener onHasToScrollToBlockListener) {
        mOnHasToScrollToBlockListener = onHasToScrollToBlockListener;
    }
}
