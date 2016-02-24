package com.meizu.ruandongchuan.dlnatest.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.meizu.ruandongchuan.dlnatest.data.FragmentAudioPre;
import com.meizu.ruandongchuan.dlnatest.data.FragmentImagePre;
import com.meizu.ruandongchuan.dlnatest.data.FragmentVideoPre;
import com.meizu.ruandongchuan.dlnatest.engine.ActionController;
import com.meizu.ruandongchuan.dlnatest.engine.SearchThread;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.util.LogUtil;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.directory.file.FileDirectory;
import org.cybergarage.upnp.std.av.server.object.format.GIFFormat;
import org.cybergarage.upnp.std.av.server.object.format.ID3Format;
import org.cybergarage.upnp.std.av.server.object.format.JPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.MP3Format;
import org.cybergarage.upnp.std.av.server.object.format.MPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.PNGFormat;


/**
 * The service to search the DLNA Device in background all the time.
 * 
 * @author CharonChui
 * 
 */
public class DLNAService extends Service {
	private static final String TAG = "DLNAService";

	private ControlPoint mControlPoint;
	private MediaServer mMediaServer;
	private MediaRenderer mMediaRenderer;
	private SearchThread mSearchThread;
	private WifiStateReceiver mWifiStateReceiver;
	private static DLNAService mService;
	private WifiManager.MulticastLock mMulticastLock;

	public static DLNAService getInstance(){
		return mService;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unInit();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startThread();
		startMediaRenderer();
		startMediaServer();
		startMultcastLock();
		return super.onStartCommand(intent, flags, startId);
	}

	private void init() {
		mService = this;
		mControlPoint = new ControlPoint();
		mControlPoint.search();
		mSearchThread = new SearchThread(mControlPoint);

		registerWifiStateReceiver();
	}

	private void unInit() {
		stopThread();
		stopMediaRenderer();
		stopMediaServer();
		stopMultcastLock();
		unregisterWifiStateReceiver();
	}

	public void startMultcastLock(){
		WifiManager wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		mMulticastLock=wifiManager.createMulticastLock("MediaRender");
		if (mMulticastLock != null){
			mMulticastLock.setReferenceCounted(true);
			mMulticastLock.acquire();
		}
	}

	public void stopMultcastLock(){
		if (mMulticastLock != null){
			mMulticastLock.release();
			mMulticastLock = null;
		}
	}

	/**
	 * 开启MediaServer
	 */
	public void startMediaServer() {
		new Thread() {
			public void run() {
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

					mMediaServer.start();
				} catch (InvalidDescriptionException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 停止MediaServer
	 */
	public void stopMediaServer(){
		new Thread() {
			public void run() {
				if(null != mMediaServer) {
					mMediaServer.stop();
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
					mMediaRenderer.start();
				} catch (InvalidDescriptionException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * stop DMR
	 */
	public void stopMediaRenderer() {
		new Thread() {
			public void run() {
				if(null != mMediaRenderer) {
					mMediaRenderer.stop();
					//mMediaRenderer.byebye();
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
	}

	public void stopThread() {
		if (mSearchThread != null) {
			mSearchThread.stopThread();
			mControlPoint.stop();
			mSearchThread = null;
			mControlPoint = null;
			LogUtil.w(TAG, "stop dlna service");
		}
	}

	private void registerWifiStateReceiver() {
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
	}

	private class WifiStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context c, Intent intent) {
			Bundle bundle = intent.getExtras();
			int statusInt = bundle.getInt("wifi_state");
			switch (statusInt) {
			case WifiManager.WIFI_STATE_UNKNOWN:
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				LogUtil.e(TAG, "wifi enable");
				startThread();
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				LogUtil.e(TAG, "wifi disabled");
				break;
			default:
				break;
			}
		}
	}

	public void play(String url, String metadata) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, DLNAUtil.getMimeType(url));
		//LogUtil.i("playlog","url="+url+"metadata="+metadata);
		Log.i("playmetadata", metadata+"-"+DLNAUtil.getMimeType(url));
		Log.i("playurl",url);
		//Toast.makeText(getApplicationContext(),metadata,Toast.LENGTH_SHORT).show();
		if(metadata.contains("image")) {
			//Bundle bundle = new Bundle();
			//bundle.putString("url", url);
			//intent.setClass(getApplicationContext(), ImageActivity.class);
			//intent.putExtras(bundle);
		}else if(metadata.contains("audio")) {

		}else if(metadata.contains("video")) {
			//intent.setType("video/*");
			//intent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		startActivity(intent);
	}
	public void setBrightness(final Device device, final String progress){
		new Thread(){
			@Override
			public void run() {
				super.run();
				ActionController.getInstance().setBrightness(device, progress);
			}
		}.start();
	}

}