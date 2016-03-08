package com.meizu.ruandongchuan.dlnatest.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.meizu.ruandongchuan.dlnatest.data.FragmentAudioPre;
import com.meizu.ruandongchuan.dlnatest.data.FragmentImagePre;
import com.meizu.ruandongchuan.dlnatest.data.FragmentVideoPre;
import com.meizu.ruandongchuan.dlnatest.engine.ActionController;
import com.meizu.ruandongchuan.dlnatest.engine.ControlPointContainer;
import com.meizu.ruandongchuan.dlnatest.engine.SearchThread;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.util.LogUtil;
import com.meizu.ruandongchuan.dlnatest.view.activity.FullscreenActivity;
import com.meizu.ruandongchuan.dlnatest.view.activity.VideoActivity;
import com.meizu.ruandongchuan.dlnatest.view.fragment.DeviceFragment;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.std.av.controller.MediaController;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.directory.file.FileDirectory;
import org.cybergarage.upnp.std.av.server.object.format.GIFFormat;
import org.cybergarage.upnp.std.av.server.object.format.ID3Format;
import org.cybergarage.upnp.std.av.server.object.format.JPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.MP3Format;
import org.cybergarage.upnp.std.av.server.object.format.MPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.PNGFormat;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.cybergarage.util.DeviceUtil;


/**
 * The service to search the DLNA Device in background all the time.
 *
 * @author CharonChui
 */
public class DLNAService extends Service {
    public static final String TAG = "DLNAService";

    private MediaController mControlPoint;
    private MediaServer mMediaServer;
    private MediaRenderer mMediaRenderer;
    private SearchThread mSearchThread;
    //private WifiStateReceiver mWifiStateReceiver;
    private static DLNAService mService;
    private WifiManager.MulticastLock mMulticastLock;
    private DeviceFragment mDeviceFragment;
    private DLNABinder mDlnaBinder;

    public static DLNAService getInstance() {
        return mService;
    }

    public MediaServer getmMediaServer() {
        return mMediaServer;
    }

    public MediaRenderer getmMediaRenderer() {
        return mMediaRenderer;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mDlnaBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DLNAService", "onCreate");
        init();
    }

    @Override
    public void onDestroy() {

        Log.i("DLNAService", "onDestroy");
        unInit();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DLNAService", "onStartCommand");
        start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        mService = this;
        mControlPoint = new MediaController();
        mSearchThread = new SearchThread(mControlPoint);
        mDeviceFragment = new DeviceFragment();
        ControlPointContainer.getInstance().setmControlPoint(mControlPoint);
        mDlnaBinder = new DLNABinder();
        //registerWifiStateReceiver();
    }

    private void start() {
        startMultcastLock();
        startMediaRenderer();
        startControlPoint();
        startThread();

        //startMediaServer();

    }


    private void unInit() {
        stopThread();
        stopMediaRenderer();
        stopMediaServer();
        //stopControlPoint();
        stopMultcastLock();
        //ControlPointContainer.getInstance().setmControlPoint(null);
        //ControlPointContainer.getInstance().getDevices().clear();
        //unregisterWifiStateReceiver();
    }

    public void startMultcastLock() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mMulticastLock = wifiManager.createMulticastLock("MediaRender");
        if (mMulticastLock != null) {
            mMulticastLock.setReferenceCounted(true);
            mMulticastLock.acquire();
        }
    }

    public void stopMultcastLock() {
        if (mMulticastLock != null) {
            mMulticastLock.release();
            mMulticastLock = null;
        }
    }

    public void startControlPoint(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (mControlPoint == null) {
                    mControlPoint = new MediaController();
                    mControlPoint.setNMPRMode(true);
                    mControlPoint.search();
                }
                Log.i(TAG, "startControlPoint");
            }
        }.start();
    }

    public void stopControlPoint(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (mControlPoint != null) {
                    mControlPoint.stop();
                    Log.i(TAG, "stopControlPoint");
                }
            }
        }.start();
    }

    /**
     * 开启MediaServer
     */
    public void startMediaServer() {
        new Thread() {
            public void run() {

                if (mMediaServer != null){
                    mMediaServer.stop();
                }
                try {
                    mMediaServer = new MediaServer(getApplicationContext());

                    mMediaServer.addPlugIn(new JPEGFormat());
                    mMediaServer.addPlugIn(new PNGFormat());
                    mMediaServer.addPlugIn(new GIFFormat());
                    mMediaServer.addPlugIn(new MPEGFormat());
                    mMediaServer.addPlugIn(new ID3Format());
                    mMediaServer.addPlugIn(new MP3Format());

                    mMediaServer.addContentDirectory(
                            new FileDirectory("Image",
                                    FragmentImagePre.getImagePathList(getApplication())));
                    mMediaServer.addContentDirectory(
                            new FileDirectory("Video",
                                    FragmentVideoPre.getVideoPathList(getApplication())));
                    mMediaServer.addContentDirectory(
                            new FileDirectory("Audio",
                                    FragmentAudioPre.getAudioPathList(getApplication())));
                    if (mMediaServer != null && !mMediaServer.isRunning()) {
                        mMediaServer.start();
                    }
                    Log.i(TAG, "startMediaServer");
                } catch (InvalidDescriptionException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 停止MediaServer
     */
    public void stopMediaServer() {
        new Thread() {
            public void run() {
                if (null != mMediaServer && mMediaRenderer.isRunning()) {
                    mMediaServer.stop();
                    mMediaServer = null;
                    Log.i(TAG, "stopMediaServer");
                }
            }
        }.start();
    }

    /**
     * start DMR
     */
    public void startMediaRenderer() {
        new Thread() {
            public void run() {
                try {
                    mMediaRenderer = new MediaRenderer(getApplicationContext());
                } catch (InvalidDescriptionException e) {
                    e.printStackTrace();
                }
                mMediaRenderer.setFriendlyName(DeviceUtil.getFriendlyName(getApplicationContext(),MediaRenderer.MEDIARENDERDER));
                mMediaRenderer.start();
                Log.i(TAG, "startMediaRenderer ");
            }
        }.start();
    }

    /**
     * stop DMR
     */
    public void stopMediaRenderer() {
        new Thread() {
            public void run() {
                if (null != mMediaRenderer) {
                    mMediaRenderer.stop();
                    Log.i(TAG,"stopMediaRenderer");
                }
            }
        }.start();
    }

    /**
     * Make the thread start to search devices.
     */
    public void startThread() {
        if (mSearchThread != null) {
            LogUtil.d(TAG, "thread is not null");
            mSearchThread.setSearcTimes(0);
        } else {
            LogUtil.d(TAG, "thread is null, create a new thread");
            mSearchThread = new SearchThread(mControlPoint);
        }

        if (mSearchThread.isAlive()) {
            LogUtil.d(TAG, "thread is alive");
            mSearchThread.awake();
        } else {
            LogUtil.d(TAG, "start the thread");
            mSearchThread.start();
        }
        Log.i(TAG,"startThread");
    }

    public void stopThread() {
        if (mSearchThread != null) {
            mSearchThread.stopThread();
            mSearchThread = null;
        }
        LogUtil.w(TAG, "stopThread");
    }

    public class DLNABinder extends Binder{
        public DLNAService getService(){
            return DLNAService.this;
        }
    }

    /*private void registerWifiStateReceiver() {
        if (mWifiStateReceiver == null) {
            mWifiStateReceiver = new WifiStateReceiver();
            registerReceiver(mWifiStateReceiver, new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void unregisterWifiStateReceiver() {
        if (mWifiStateReceiver != null) {
            unregisterReceiver(mWifiStateReceiver);
            mWifiStateReceiver = null;
        }
    }*/

    /*private class WifiStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals( ConnectivityManager.CONNECTIVITY_ACTION)){
                NetworkInfo netInfo = ( NetworkInfo ) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                ConnectivityManager cMgr = ( ConnectivityManager ) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo active = cMgr.getActiveNetworkInfo();
                if ( ( active != null  && active.getType() == ConnectivityManager.TYPE_WIFI && !active.isConnectedOrConnecting() )
                        || ( ( active != null ) && active.getType() == ConnectivityManager.TYPE_ETHERNET && !active.isConnectedOrConnecting() ) ){
                    stop();
                    Log.i("Network:", "wifi网络连接断开");
                }
                if ( (netInfo != null) && (netInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) ){
                    Log.i("Network:","连接到网络"+netInfo.getTypeName());
                    stop();
                    start();
                }
            }

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
                System.out.println("网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                stop();
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    System.out.println("wifi网络连接断开");
                    //stop();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    start();
                    //获取当前wifi名称
                    System.out.println("连接到网络 " + wifiInfo.getSSID());

                }
            }
        }
    }*/

    public void play(String url, String metadata) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("metadata", metadata);
        String type = "";
        ItemNode itemNode = DLNAUtil.parseMetaData(metadata);
        if (itemNode != null){
            if ( itemNode.isAudioClass() ) {
                type = "audio/*";
                intent.setClass(getApplicationContext(), VideoActivity.class);
            }else
            if ( itemNode.isImageClass() ) {
                type = "image/*";
                intent.setClass(getApplicationContext(), FullscreenActivity.class);
            }else
            if ( itemNode.isMovieClass() ){
                type = "video/*";
                intent.setClass(getApplicationContext(), VideoActivity.class);
            }
        }
        //intent.setData(uri);
        intent.setDataAndType(uri, type);
        Log.i("type=", intent.getType());
        //LogUtil.i("playlog","url="+url+"metadata="+metadata);
        Log.i("playmetadata", metadata + "-" + itemNode.getUPnPClass()+itemNode.isImageClass());
        Log.i("playurl", url);
        //Toast.makeText(getApplicationContext(),metadata,Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void setBrightness(final Device device, final String progress) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                ActionController.getInstance().setBrightness(device, progress);
            }
        }.start();
    }

}