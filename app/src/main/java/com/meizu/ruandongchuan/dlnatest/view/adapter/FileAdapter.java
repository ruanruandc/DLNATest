package com.meizu.ruandongchuan.dlnatest.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.view.DlnaApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.cybergarage.upnp.std.av.server.DC;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;

import java.util.List;

/**
 * Created by ruandongchuan on 2/29/16.
 */
public class FileAdapter extends BaseAdapter {
    private List<ContentNode> mData;
    private ImageLoader mImageLoader;
    public FileAdapter(Context context,List<ContentNode> data){
        mData = data;
        mImageLoader = DlnaApp.getImageLoader(context.getApplicationContext());
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
        AudioHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio,parent,false);
            holder = new AudioHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (AudioHolder) convertView.getTag();
        }
        ContentNode bean = mData.get(position);
        //holder.tv_singer.setText(bean.getArtist());
        holder.tv_name.setText(bean.getProperty(DC.TITLE).getValue());
        String type = "";
        if (ContainerNode.isContainerNode(bean)){
            type = "directory";
        }else{
            type = "file";
        }
        holder.tv_singer.setText(type);
        //mImageLoader.displayImage(getContentUrl(bean), holder.iv_album);
        return convertView;
    }

    class AudioHolder {
        //private OnItemClickListener onclick;
        public ImageView iv_album;
        public TextView tv_name;
        public TextView tv_singer;

        public AudioHolder(View itemView) {
            iv_album = (ImageView) itemView.findViewById(R.id.iv_album);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_singer = (TextView) itemView.findViewById(R.id.tv_singer);
        }
    }
}
