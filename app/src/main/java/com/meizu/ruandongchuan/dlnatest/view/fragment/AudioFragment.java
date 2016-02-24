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
import com.meizu.ruandongchuan.dlnatest.data.Audio;
import com.meizu.ruandongchuan.dlnatest.data.FragmentAudioPre;
import com.meizu.ruandongchuan.dlnatest.engine.LocalController;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.view.adapter.AudioAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ListView mLvAudio;
    private AudioAdapter mAdapter;
    private List<Audio> mData;
    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        mLvAudio = (ListView) view.findViewById(R.id.lv_audio);
        initData();
        return view;
    }
    private void initData(){
        mData = new ArrayList<>();
        mAdapter = new AudioAdapter(getContext(), mData);
        mLvAudio.setAdapter(mAdapter);
        mLvAudio.setOnItemClickListener(this);
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                mData.clear();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                mData.addAll(FragmentAudioPre.getAudios(getContext()));
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... progresses) {

            }

            @Override
            protected void onPostExecute(Boolean result) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onCancelled() {
            }
        }.execute("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocalController.play(mData.get(position).getPath(), DLNAUtil.getMetaData(mData.get(position)));
    }
}
