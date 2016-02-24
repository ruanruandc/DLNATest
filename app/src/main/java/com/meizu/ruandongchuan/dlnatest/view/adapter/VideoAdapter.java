package com.meizu.ruandongchuan.dlnatest.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meizu.ruandongchuan.dlnatest.data.Video;

import java.util.List;

/**
 * Created by ruandongchuan on 16-1-6.
 */
public class VideoAdapter extends BaseAdapter {
    private List<Video> mData;
    private List<String> mVideos;
    public VideoAdapter(List<Video> mData,List<String> mVideos){
        this.mData = mData;
        this.mVideos = mVideos;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,
                    parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(mData.get(position).getTitle());
        //holder.tvName.setText(mVideos.get(position));
        return convertView;
    }
    class ViewHolder{
        public TextView tvName;
        public ViewHolder(View view){
            tvName = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}
