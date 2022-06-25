package com.dinhlong.mergecall;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

public class CallService extends InCallService {
    private static final String INTENT_PHONE_NUMBER = "PHONE_NUMBER";
    private static final String TAG = "CallService";


    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        String number = CallManager.getInstance().getPhoneNumber(call);
        if (CallManager.getInstance().isConferenceCall(call)) {
            number = "ConferenceCall";
        }
        Log.i(TAG, "---onCallAdded: " + number);
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
        CallManager.getInstance().removeCallCallback(number);
        CallManager.getInstance().removeCall(number);

    }

}
