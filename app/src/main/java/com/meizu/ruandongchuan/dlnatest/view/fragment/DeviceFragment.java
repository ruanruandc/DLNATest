package com.meizu.ruandongchuan.dlnatest.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.engine.ControlPointContainer;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruandongchuan on 16-1-5.
 */
public class DeviceFragment extends DialogFragment{
    private ListView mLvRenderer,mLvServer;
    private ArrayAdapter<String> mRendererAdapter,mServerAdapter;
    private List<String> mRenderers,mServers;
    private List<Device> mRendererDevices,mServerDevices;
    private Handler mHandler = new Handler();
    private int mSelected = -1;

    @Override
    public void onResume() {
        super.onResume();
        if (ControlPointContainer.getInstance().getSelectedDevice() == null)
            return;
        for (int i =0 ;i< mRendererDevices.size() ;i++){
            if (mRendererDevices.get(i).getUUID().equals(
                    ControlPointContainer.getInstance().getSelectedDevice().getUUID())){
                mSelected = i;
            }
        }
        if (mSelected < 0)
            return;
        mLvRenderer.setItemChecked(mSelected, true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_deivce, null);
        mLvRenderer = (ListView) view.findViewById(R.id.lv_renderer);
        mLvServer = (ListView) view.findViewById(R.id.lv_server);
        mLvRenderer.setAdapter(mRendererAdapter);
        mLvServer.setAdapter(mServerAdapter);
        mLvRenderer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ControlPointContainer.getInstance().setSelectedDevice(mRendererDevices.get(position));
                mLvRenderer.setItemChecked(position, true);
                mSelected = position;
                //mLvRenderer.setSelection(position);
                dismiss();
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("设备列表")
                .create();
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRenderers = new ArrayList<>();
        mServers = new ArrayList<>();
        mRendererDevices = new ArrayList<>();
        mServerDevices = new ArrayList<>();
        initData();
        mServerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mServers);
        mRendererAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_single_choice, mRenderers);
        //mLvRenderer.setAdapter(mRendererAdapter);
        //mLvServer.setAdapter(mServerAdapter);

        ControlPointContainer.getInstance().setDeviceChangeListener(new ControlPointContainer.DeviceChangeListener() {
            @Override
            public void onDeviceChange(Device device) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
    }
    private void refresh(){
        initData();
        mServerAdapter.notifyDataSetChanged();
        mRendererAdapter.notifyDataSetChanged();
    }

    private void initData(){
        mRendererDevices.clear();
        mServerDevices.clear();
        mRenderers.clear();
        mServers.clear();
        List<Device> devices = ControlPointContainer.getInstance().getDevices();
        for (Device device : devices){
            if (DLNAUtil.isMediaRenderer(device)){
                mRendererDevices.add(device);
                mRenderers.add(device.getFriendlyName());
            }
            if (DLNAUtil.isMediaServer(device)){
                mServerDevices.add(device);
                mServers.add(device.getFriendlyName());
            }
        }
    }

}
