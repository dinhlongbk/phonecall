package com.dinhlong.mergecall;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;

import com.dinhlong.mergecall.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID = 990;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 111;
    private ActivityMainBinding mActivityBinding;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        rootView = mActivityBinding.getRoot();
        setContentView(rootView);
        //

        requestRole();
        requestMultiPermissions();
        setupListener();

    }

    private void setupListener() {
        mActivityBinding.callNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mActivityBinding.number1.getText().toString();
                CallManager.getInstance().makePhoneCall( num, getApplicationContext());
            }
        });
    }


    public void requestMultiPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
//        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
    }


    public void requestRole() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
            Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
            startActivityForResult(intent, REQUEST_ID);
        } else {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                Log.w(TAG, "Selected default phone app");
            } else {
                Log.e(TAG, "Please select this app as default phone call");
            }
        }
    }
}