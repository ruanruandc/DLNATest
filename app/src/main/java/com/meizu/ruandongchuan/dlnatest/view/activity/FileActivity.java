package com.meizu.ruandongchuan.dlnatest.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.engine.ControlPointContainer;
import com.meizu.ruandongchuan.dlnatest.engine.LocalController;
import com.meizu.ruandongchuan.dlnatest.service.DLNAService;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.view.adapter.FileAdapter;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.controller.MediaController;
import org.cybergarage.upnp.std.av.server.ContentDirectory;
import org.cybergarage.upnp.std.av.server.DC;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.DIDLLite;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.cybergarage.upnp.std.av.server.object.item.file.FileItemNode;

import java.util.ArrayList;
import java.util.List;

public class FileActivity extends BaseActivity {
    private ListView mListView;
    private MediaController mControlPoint;
    private Device mServer;
    private FileAdapter mAdapter;
    private List<ContentNode> mData;
    private BrowseThread browseThread;
    private ContentDirectory contentDirectory;
    private ProgressBar mProgressBar;
    public static final int UPDATE = 100;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE:
                    mAdapter.notifyDataSetChanged();
                    hide();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (ControlPointContainer.getInstance().getmSelectServer() != null){
            toolbar.setTitle(ControlPointContainer.getInstance().getmSelectServer().getFriendlyName());
        }
        setSupportActionBar(toolbar);
        initView();
        initData();
        browseThread = new BrowseThread("0");
        browseThread.start();
    }
    private void initView(){
        mListView = (ListView) findViewById(R.id.lv_file);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_file);
    }
    private void show(){
        mProgressBar.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }
    private void hide(){
        mProgressBar.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }
    private void initData(){
        Intent intent = getIntent();
        contentDirectory = DLNAService.getInstance().getmMediaServer().getContentDirectory();
        mData = new ArrayList<>();
        mAdapter = new FileAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        //String name = intent.getStringExtra("name");
        //Log.i("name=",name);
        mControlPoint = ControlPointContainer.getInstance().getmControlPoint();
        mServer = ControlPointContainer.getInstance().getmSelectServer();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContentNode contentNode = mData.get(position);
                String metadata = "";
                if (ContainerNode.isContainerNode(contentNode)){
                    String contentID = contentNode.getID();
                    Log.i("contentID=",contentID);
                    show();
                    browseThread = new BrowseThread(contentID);
                    browseThread.start();
                }else if (ItemNode.isItemNode(contentNode)){
                    ItemNode itemNode = (ItemNode) contentNode;
                    String url = itemNode.getFirstResource().getValue();
                    if (url.contains("/ExportContent?id=")) {
                        ContentNode temp = contentDirectory.findContentNodeByID(contentNode.getID());
                        if (temp instanceof FileItemNode) {
                            FileItemNode fileItemNode = (FileItemNode) temp;
                            url = fileItemNode.getFile().getPath();
                        }
                    }
                    DIDLLite didlLite = new DIDLLite();
                    didlLite.setContentNode(contentNode);
                    metadata = didlLite.toString();
                    Log.i("url=", url);
                    Log.i("metadata=", metadata);
                    LocalController.play(DLNAUtil.getUrl(url), metadata);
                }
            }
        });

    }

    private void getList(String objectID){
        ContainerNode containerNode = mControlPoint.browse(mServer,objectID);
        mData.clear();
        if (!objectID.equals("0")){
            ContainerNode backNode = new ContainerNode();
            backNode.setProperty(DC.TITLE,"back");
            backNode.setID(containerNode.getID());
            mData.add(backNode);
        }
        for (int i = 0; i < containerNode.getChildCount(); i++){
            ContentNode contentNode = containerNode.getContentNode(i);
            Log.i("contentnode=",contentNode.getProperty(DC.TITLE).getValue());
            mData.add(contentNode);
        }
        handler.sendEmptyMessage(UPDATE);
        Log.i("List=", containerNode.getChildCount()+"");
    }
    class BrowseThread extends Thread {
        private String objectID;
        public BrowseThread(String objectID){
            this.objectID = objectID;
        }
        @Override
        public void run() {
            getList(objectID);
        }
    }

}
