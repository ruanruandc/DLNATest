package com.meizu.ruandongchuan.dlnatest.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.data.Image;
import com.meizu.ruandongchuan.dlnatest.view.DlnaApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader mImageLoader;
    private List<Image> data = new ArrayList<Image>();

    public ImageAdapter(Context context, List<Image> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        mImageLoader = DlnaApp.getImageLoader(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Image getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_picture, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mImageLoader.displayImage(("content://media/external/images/media/" + data.get(position).getId()),holder.iv_picture);
        return convertView;
    }

    public class ViewHolder {
        ImageView iv_picture;
        public ViewHolder(View view) {
            iv_picture = (ImageView) view.findViewById(R.id.iv_picture);
        }
    }
}
