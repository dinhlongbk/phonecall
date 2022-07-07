package com.dinhlong.mergecall;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.dinhlong.mergecall.utils.Constant;

import java.util.AbstractMap;
import java.util.HashMap;

public class CallManager {
    private static final String TAG = CallManager.class.getName();

    private CallManager () {}
    private static CallManager mCallManager = null;
    private Call mCall;
    //private ArrayList<CallCallback> callCallbackList = new ArrayList<>();
    private HashMap<String, CallCallback> callCallbackList = new HashMap<>();
    private HashMap<String, Call> callList = new HashMap<>();

    public static CallManager getInstance() {
        if (mCallManager == null) {
            mCallManager = new CallManager();
        }
        return mCallManager;
    }

    public AbstractMap<String, Call> getCallList() {
        return callList;
    }

    public int getSizeOfCallList() {
        return callList.size();
    }

    public void addCall(String callId, Call call) {
        callList.put(callId, call);
    }

    public void removeCall(String callId) {
        callList.remove(callId);
    }

    public void clearCallList() {
        callList.clear();
    }

    public void addCallCallback(CallCallback callback) {
        Log.i(TAG, "addCallCallback, callId=" + callback.getCallId());
        callback.registerCallback();
        callCallbackList.put(callback.getCallId(), callback);
    }

    public void removeCallCallback(String callId) {
        Log.i(TAG, "removeCallCallback, callId=" + callId);
        CallCallback callback = callCallbackList.get(callId);
        if( callback != null) {
            callback.unregisterCallback();
            callCallbackList.remove(callId);
        }
    }


    public void clearCallCallback() {
        callCallbackList.clear();
    }

    public void makePhoneCall(String phoneNumber, Context context) {
        TelecomManager mTelecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (mTelecomManager == null) {
            Log.v(TAG, "----TelecomManager is not available----");
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = Uri.fromParts("tel", phoneNumber, null);
            Bundle extras = new Bundle();
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true);
            Log.i (TAG, "Calling " + phoneNumber);
            mTelecomManager.placeCall(uri, extras);
        } else {
            Log.e(TAG, "-----Permission ANSWER_PHONE_CALLS was not granted---");
        }
    }

    public Call getActiveCall() {
        Call resCall = null;
        for (Call c : callList.values()) {
            if (c.getState() == Call.STATE_ACTIVE) {
                resCall = c;
                if (isConferenceCall(c)) {
                    Log.i(TAG,"active call: Conference");
                } else {
                    Log.i(TAG,"active call: " + getPhoneNumber(c));
                }
                return resCall;
            }
        }
        return resCall;
    }

    public void mergeCall () {
        Call activeCall = getActiveCall();
        String callId = getPhoneNumber(activeCall);
        if (activeCall != null) {
            Call conferenceCall = callList.get(Constant.CONFERENCE_CALL);
            if (conferenceCall != null) {
                activeCall.conference(callList.get(Constant.CONFERENCE_CALL));
                Log.i(TAG, "merge " + callId + " to ConferenceCall");
            } else {
                for (String id : callList.keySet()) {
                    if(!id.equals(callId)) {
                        activeCall.conference(callList.get(id));
                        Log.w(TAG, "make a conference call: " + callId + " and " + id);
                    }
                }
            }
        } else {
            Log.e(TAG, "mergeCall failed, call=null");
        }
    }

    public void endCall (String callId) {
        Call call = callList.get(callId);
        if (call != null) {
            call.disconnect();
        }
    }

    public boolean isHoldCall(String callId) {
        Call call = callList.get(callId);
        if (call != null) {
            return call.getState() == Call.STATE_HOLDING;
        }
        return false;
    }

    public void holdCall(String callId) {
        Call call = callList.get(callId);
        if (call != null) {
            call.hold();
        }
    }

    public void unholdCall(String callId) {
        Call call = callList.get(callId);
        if (call != null) {
            call.unhold();
        }
    }


    public String getPhoneNumber(Call call) {
        if (call == null) return null;
        if (isConferenceCall(call)) {
            return Constant.CONFERENCE_CALL;
        }
        if (call.getDetails().getGatewayInfo() != null) {
            return call.getDetails().getGatewayInfo()
                    .getOriginalAddress().getSchemeSpecificPart();
        }
        return call.getDetails().getHandle() == null ? null : call.getDetails().getHandle().getSchemeSpecificPart();
    }

    public boolean isConferenceCall(Call call) {
        if (call == null) return false;
        return call.getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE);
    }

}
