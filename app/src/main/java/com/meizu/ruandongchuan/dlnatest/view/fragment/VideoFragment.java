package com.meizu.ruandongchuan.dlnatest.view.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.data.FragmentVideoPre;
import com.meizu.ruandongchuan.dlnatest.data.Video;
import com.meizu.ruandongchuan.dlnatest.engine.LocalController;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.view.adapter.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView mListView;
    private VideoAdapter mAdapter;
    private List<Video> mData;
    private List<String> mVideos;
    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_video);
        initData();
        return view;
    }
    private void initData(){
        mData = new ArrayList<>();
        mVideos = new ArrayList<>();
        mAdapter = new VideoAdapter(mData,mVideos);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                mData.addAll(FragmentVideoPre.getVideoFromSD(getContext()));
                mVideos.addAll(FragmentVideoPre.getVideoPathList(getContext()));
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocalController.play(mVideos.get(position), DLNAUtil.getMetaData(mData.get(position)));
    }
}
