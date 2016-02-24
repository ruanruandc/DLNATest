package com.meizu.ruandongchuan.dlnatest.view.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.data.FragmentImagePre;
import com.meizu.ruandongchuan.dlnatest.data.Image;
import com.meizu.ruandongchuan.dlnatest.data.event.EventMain;
import com.meizu.ruandongchuan.dlnatest.engine.ControlPointContainer;
import com.meizu.ruandongchuan.dlnatest.engine.LocalController;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.view.MainActivity;
import com.meizu.ruandongchuan.dlnatest.view.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment implements AdapterView.OnItemClickListener {

    private GridView mGvImage;
    private ImageAdapter mAdapter;
    private List<Image> mImages;

    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        mGvImage = (GridView) view.findViewById(R.id.gv_picture);
        initData();
        return view;
    }

    private void initData() {
        mImages = new ArrayList<>();
        mAdapter = new ImageAdapter(getContext(), mImages);
        mGvImage.setAdapter(mAdapter);
        mGvImage.setOnItemClickListener(this);
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                mImages.clear();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                //can not use images = ...
                mImages.addAll(FragmentImagePre.getImageFromSD(getContext()));
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... progresses) {

            }

            @Override
            protected void onPostExecute(Boolean result) {
                mAdapter.notifyDataSetChanged();
                //cancelBaseDialog();
            }

            @Override
            protected void onCancelled() {
                mAdapter.notifyDataSetChanged();
                //cancelBaseDialog();
            }
        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (ControlPointContainer.getInstance().getSelectedDevice() == null){
            EventBus.getDefault().post(new EventMain(MainActivity.EVENT_SHOW));
        }else {
            play(mImages.get(position).getDirectory(), DLNAUtil.getMetaData(mImages.get(position)));
        }
    }

    private void play(String path, String metaData) {
        LocalController.play(path, metaData);
    }
}
