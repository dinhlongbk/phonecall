package com.dinhlong.mergecall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;

import com.dinhlong.mergecall.databinding.ActivityCallBinding;

import java.util.AbstractMap;

public class CallActivity extends AppCompatActivity {

    private static final String TAG = "CallActivity";

    private  ActivityCallBinding mActivityCallBinding;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_call);
//
        mActivityCallBinding = ActivityCallBinding.inflate(getLayoutInflater());
        rootView = mActivityCallBinding.getRoot();
        setContentView(rootView);
        //

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("ACTION_CALL");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        setupListener();

    }

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String id = bundle.getString("CALL_ID");
            int state = bundle.getInt("CALL_STATE_CHANGED");
            switch (state) {
                case Call.STATE_ACTIVE:
                    Log.w(TAG, "BroadcastReceiver-> callID=" + id + " Call.STATE_ACTIVE");
                    break;
                case Call.STATE_DISCONNECTED:
                    Log.w(TAG, "BroadcastReceiver->  callID=" + id + " Call.STATE_DISCONNECTED"
                            + " getSizeOfCallList()=" + CallManager.getInstance().getSizeOfCallList());
                    if (CallManager.getInstance().getSizeOfCallList() !=0) {
                        AbstractMap<String, Call> list = CallManager.getInstance().getCallList();
                        for (String idd : list.keySet()) {
                            Log.w(TAG, "callID in list: " + idd);
                        }
                    }
                    if (CallManager.getInstance().getSizeOfCallList() < 1) {
                        finish();
                    }
                    break;
                case Call.STATE_HOLDING:
                    Log.w(TAG, "BroadcastReceiver-> callID=" + id + " Call.STATE_HOLDING");
                    break;
            }
        }
    };

    private void setupListener() {
        mActivityCallBinding.addNumber11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mActivityCallBinding.number11.getText().toString();
                CallManager.getInstance().makePhoneCall(num, getApplicationContext());
            }
        });

        mActivityCallBinding.removeNumber11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mActivityCallBinding.number11.getText().toString();
                CallManager.getInstance().endCall(num);
            }
        });
        //
        mActivityCallBinding.addNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mActivityCallBinding.number2.getText().toString();
                CallManager.getInstance().makePhoneCall(num, getApplicationContext());
            }
        });
        mActivityCallBinding.removeNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mActivityCallBinding.number2.getText().toString();
                CallManager.getInstance().endCall(num);
            }
        });

        mActivityCallBinding.addNumber3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mActivityCallBinding.number3.getText().toString();
                CallManager.getInstance().makePhoneCall(num, getApplicationContext());
            }
        });
        mActivityCallBinding.removeNumber3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mActivityCallBinding.number3.getText().toString();
                CallManager.getInstance().endCall(num);
            }
        });

        mActivityCallBinding.mergeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallManager.getInstance().mergeCall();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }
}