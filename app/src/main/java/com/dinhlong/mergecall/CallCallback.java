package com.dinhlong.mergecall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dinhlong.mergecall.utils.Constant;

public class CallCallback extends Call.Callback{

    private static final String TAG = CallCallback.class.getName();
    private final Context mContext;
    private final String mCallId;
    private final Call mCall;

    public CallCallback(Context context, String callId, Call call) {
        mContext= context;
        mCallId = callId;
        mCall = call;
    }

    public String getCallId() {
        return mCallId;
    }


    public void registerCallback() {
        mCall.registerCallback(this);
    }

    public void unregisterCallback() {
        mCall.unregisterCallback(this);
    }

    private void sendCallAction(Call call, int state) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CALL_ID, mCallId);
        bundle.putInt(Constant.CALL_STATE_CHANGED, state);
        Intent intent = new Intent(Constant.ACTION_CALL);
        intent.putExtras(bundle);
        localBroadcastManager.sendBroadcast(intent);

    }

    @Override
    public void onStateChanged(Call call, int state) {
        super.onStateChanged(call, state);
        sendCallAction(call, state);
    }
}
