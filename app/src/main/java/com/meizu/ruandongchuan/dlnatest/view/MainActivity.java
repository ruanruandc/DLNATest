package com.meizu.ruandongchuan.dlnatest.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.data.event.EventBrightness;
import com.meizu.ruandongchuan.dlnatest.data.event.EventMain;
import com.meizu.ruandongchuan.dlnatest.service.DLNAService;
import com.meizu.ruandongchuan.dlnatest.view.adapter.FragmentPagerAdapterImpl;
import com.meizu.ruandongchuan.dlnatest.view.fragment.AudioFragment;
import com.meizu.ruandongchuan.dlnatest.view.fragment.DeviceFragment;
import com.meizu.ruandongchuan.dlnatest.view.fragment.ImageFragment;
import com.meizu.ruandongchuan.dlnatest.view.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MainActivity extends AppCompatActivity{
    public static final int EVENT_SHOW = 100;
    public static final int EVENT_SELECT_DEVICE = 101;
    //private ListView mLvDevices;
    private List<String> mDecices = new ArrayList<>();
    private FragmentPagerAdapterImpl mAdapter;
    private DeviceFragment mDeviceFragment;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initData();
    }
    @Subscribe
    public void onEventMainThread(EventBrightness value){
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =  (Float.parseFloat(value.getBrightness())*0.01f);
        Log.i("onEventMainThread", value.getBrightness());
        Log.i("currentThread", Thread.currentThread().getName());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().setAttributes(lp);
            }
        });

        //Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }
    @Subscribe
    public void onEventMainThread(EventMain value){
        Log.i("onEvent-currentThread", Thread.currentThread().getName());
        switch (value.getTag()){
            case EVENT_SHOW:
                showDeviceFragment();
                break;
            case EVENT_SELECT_DEVICE:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refresh();
        if (DLNAService.getInstance() != null) {
            DLNAService.getInstance().startThread();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDLNAService();
        EventBus.getDefault().unregister(this);
    }

    private void initView(){
        mTabLayout = (TabLayout) findViewById(R.id.tab_main);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);
    }

    private void initData(){
        mAdapter = new FragmentPagerAdapterImpl(getSupportFragmentManager());
        mAdapter.addFragment("image",new ImageFragment());
        mAdapter.addFragment("audio",new AudioFragment());
        mAdapter.addFragment("video",new VideoFragment());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
        mDeviceFragment = new DeviceFragment();
        startDLNAService();
        EventBus.getDefault().register(this);
        //refresh();
    }

    private void startDLNAService() {
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        startService(intent);
    }

    private void stopDLNAService(){
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_device) {
            //refresh();
            //DLNAService.getInstance().startThread();
            showDeviceFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeviceFragment(){
        DLNAService.getInstance().startThread();
        mDeviceFragment.show(getSupportFragmentManager(),"DeviceFragment");
    }



}
