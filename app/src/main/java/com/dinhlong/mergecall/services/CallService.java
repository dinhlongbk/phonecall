package com.dinhlong.mergecall.services;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.util.Log;

import com.dinhlong.mergecall.CallActivity;
import com.dinhlong.mergecall.CallCallback;
import com.dinhlong.mergecall.CallManager;
import com.dinhlong.mergecall.utils.Constant;

import java.util.List;

public class CallService extends InCallService {
    private static final String INTENT_PHONE_NUMBER = "PHONE_NUMBER";
    private static final String TAG = "CallService";


    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        String number = CallManager.getInstance().getPhoneNumber(call);
        if (CallManager.getInstance().isConferenceCall(call)) {
            number = Constant.CONFERENCE_CALL;

        }
        List<Call> list = call.getChildren();
        Log.e(TAG, "size of ListGetChildren=" + list.size());
        for (Call c : list) {
            Log.w(TAG, "list of getChildren: " + c.getDetails());
        }
        list = call.getConferenceableCalls();
        for (Call c : list) {
            Log.w(TAG, "list of getConferenceableCalls: " + c.getDetails());
        }
        Log.w(TAG, "getParent=" + call.getParent());
        Log.i(TAG, "---onCallAdded: " + number);
        CallManager.getInstance().addRawCallList(call);
        CallManager.getInstance().addCall(number, call);
        CallManager.getInstance().addCallCallback(new CallCallback(this, number,call));
        if (CallManager.getInstance().getSizeOfCallList() == 1) {
            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(INTENT_PHONE_NUMBER, call.getDetails().getHandle().getSchemeSpecificPart());
            startActivity(intent);
            Log.w(TAG, "Start CallActivity");
        } else {
            Log.w(TAG, "No need to start CallActivity, it already started");
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        String number = CallManager.getInstance().getPhoneNumber(call);
        if (CallManager.getInstance().isConferenceCall(call)) {
            number = "ConferenceCall";
        }
        Log.i(TAG, "---onCallRemoved: " + number);
        CallManager.getInstance().removeRawCallList(call);
        CallManager.getInstance().removeCallCallback(number);
        CallManager.getInstance().removeCall(number);
    }

    @Override
    public void onConnectionEvent(Call call, String event, Bundle extras) {
        super.onConnectionEvent(call, event, extras);
        Log.w(TAG, "onConnectionEvent");
    }

    @Override
    public void onCanAddCallChanged(boolean canAddCall) {
        super.onCanAddCallChanged(canAddCall);
        Log.w(TAG, "onCanAddCallChanged: " + canAddCall);
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState audioState) {
        super.onCallAudioStateChanged(audioState);
        Log.w(TAG, "onCallAudioStateChanged: " + audioState);
    }

    @Override
    public void onBringToForeground(boolean showDialpad) {
        super.onBringToForeground(showDialpad);
        Log.w(TAG, "onBringToForeground");
    }


}
