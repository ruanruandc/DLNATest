package com.meizu.ruandongchuan.dlnatest.engine;


import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.util.LogUtil;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.controller.MediaController;

import java.util.ArrayList;
import java.util.List;

public class ControlPointContainer {
	private List<Device> mDevices;
	private Device mSelectedDevice;
	private DeviceChangeListener mDeviceChangeListener;
	private MediaController mControlPoint;
	private Device mSelectServer;
	private static final ControlPointContainer mDLNAContainer = new ControlPointContainer();

	private ControlPointContainer() {
		mDevices = new ArrayList<Device>();
	}

	public void setmControlPoint(MediaController mControlPoint) {
		this.mControlPoint = mControlPoint;
	}

	public MediaController getmControlPoint() {
		return mControlPoint;
	}

	public static ControlPointContainer getInstance() {
		return mDLNAContainer;
	}

	public synchronized void addDevice(Device device) {
		LogUtil.d("addDevice","add device" + device.getDeviceType() + device.getFriendlyName());
		if (!DLNAUtil.isMediaRenderer(device) && !DLNAUtil.isMediaServer(device)) {
			return;			
		}
		int size = mDevices.size();
		for (int i = 0; i < size; i++) {
			String udnString = mDevices.get(i).getUDN();
			if (device.getUDN().equalsIgnoreCase(udnString)) {
				return;
			}
		}
		
		mDevices.add(device);
		LogUtil.d("addDevice","Devices add a device" + device.getDeviceType() + device.getFriendlyName());
		if (mDeviceChangeListener != null) {
			mDeviceChangeListener.onDeviceChange(device);
		}
	}

	public synchronized void removeDevice(Device d) {
		if (!DLNAUtil.isMediaRenderer(d) && !DLNAUtil.isMediaServer(d)) {
			return;
		}
		int size = mDevices.size();
		for (int i = 0; i < size; i++) {
			String udnString = mDevices.get(i).getUDN();
			if (d.getUDN().equalsIgnoreCase(udnString)) {
				Device device = mDevices.remove(i);
				LogUtil.d("removeDevice","Devices remove a device "+device.getFriendlyName());

				if (mSelectedDevice != null && 
					mSelectedDevice.getUDN().equalsIgnoreCase(device.getUDN())) {
					mSelectedDevice = null;
				}
				if (mDeviceChangeListener != null) {
					mDeviceChangeListener.onDeviceChange(d);
				}
				break;
			}
		}
	}

	public void searchDevices() {
		//DLNAService.stopThread();
		//DLNAService.getInstance().startThread();
	}
	
	public synchronized void clear() {
		if (mDevices != null) {
			mDevices.clear();
			mSelectedDevice = null;
		}
	}

	public Device getSelectedDevice() {
		return mSelectedDevice;
	}

	public void setSelectedDevice(Device mSelectedDevice) {
		this.mSelectedDevice = mSelectedDevice;
	}

	public void setmSelectServer(Device mSelectServer) {
		this.mSelectServer = mSelectServer;
	}

	public Device getmSelectServer() {
		return mSelectServer;
	}

	public void setDeviceChangeListener(
			DeviceChangeListener deviceChangeListener) {
		mDeviceChangeListener = deviceChangeListener;
	}

	public List<Device> getDevices() {
		return mDevices;
	}

	public interface DeviceChangeListener {
		void onDeviceChange(Device device);
	}

}
