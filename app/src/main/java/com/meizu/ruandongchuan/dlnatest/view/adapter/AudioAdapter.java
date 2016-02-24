package com.meizu.ruandongchuan.dlnatest.view.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.data.Audio;
import com.meizu.ruandongchuan.dlnatest.view.DlnaApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by ruandongchuan on 15-12-16.
 */
public class AudioAdapter extends BaseAdapter{
    private List<Audio> mData;
    private ImageLoader mImageLoader;
    public AudioAdapter(Context context,List<Audio> data){
        mData = data;
        mImageLoader = DlnaApp.getImageLoader(context.getApplicationContext());
    }
    public static String getContentUrl(Audio bean){
        String url = "";
        if (bean.getAlbumId() < 0) {
            Uri uri = Uri.parse("content://media/external/audio/media/" + bean.getId() + "/albumart");
            url = uri.toString();
        } else {
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), bean.getAlbumId());
            url = uri.toString();
        }
        Log.i("Uri",url);
        return url;
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
        Audio bean = mData.get(position);
        holder.tv_singer.setText(bean.getArtist());
        holder.tv_name.setText(bean.getTilte());
        mImageLoader.displayImage(getContentUrl(bean), holder.iv_album);
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
