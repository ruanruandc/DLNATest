package com.meizu.ruandongchuan.dlnatest.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.engine.ActionController;
import com.meizu.ruandongchuan.dlnatest.engine.ControlPointContainer;
import com.meizu.ruandongchuan.dlnatest.service.DLNAService;

import org.cybergarage.upnp.Device;

public class ControlActivity extends AppCompatActivity {
    private SeekBar mSbControl;
    private TextView mTvControl;
    private Device mCurrentDevice;
    private ActionController mActionController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (ControlPointContainer.getInstance().getSelectedDevice() != null){
            mCurrentDevice = ControlPointContainer.getInstance().getSelectedDevice();
            toolbar.setTitle(mCurrentDevice.getFriendlyName());
        }
        setSupportActionBar(toolbar);
        initView();
        initData();
    }
    private void initView(){
        mSbControl = (SeekBar) findViewById(R.id.sb_control);
        mTvControl = (TextView) findViewById(R.id.tv_control);
    }
    private void initData(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final String brightness = ActionController.getInstance().getBrightness(mCurrentDevice);
                Log.i("getBrightness", brightness == null ? "null" : brightness);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSbControl.setProgress(Integer.parseInt(brightness));
                    }
                });
            }
        }.start();
        mSbControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("onProgressChanged",String.valueOf(progress));
                mTvControl.setText(String.valueOf(progress));
                DLNAService.getInstance().setBrightness(mCurrentDevice, String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
