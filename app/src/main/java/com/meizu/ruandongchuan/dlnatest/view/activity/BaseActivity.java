package com.meizu.ruandongchuan.dlnatest.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.meizu.ruandongchuan.dlnatest.view.fragment.DeviceFragment;

/**
 * Created by ruandongchuan on 2/29/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private DeviceFragment deviceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceFragment = new DeviceFragment();
    }
    public void showDeviceFragment(){
        if ( !deviceFragment.isAdded() ) {
            deviceFragment.show(getSupportFragmentManager(), "DeviceFragment");
        }
    }
}
