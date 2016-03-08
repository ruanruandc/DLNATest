package com.meizu.ruandongchuan.dlnatest.view;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.meizu.ruandongchuan.dlnatest.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.concurrent.ConcurrentHashMap;

import io.vov.vitamio.Vitamio;

/**
 * Created by ruandongchuan on 16-1-5.
 */
public class DlnaApp extends Application {
    private static DlnaApp instance;
    private static ImageLoader imageLoader;
    private ConcurrentHashMap<String, Handler> mHandlerMap = new ConcurrentHashMap<String, Handler>();

    public static DlnaApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Vitamio.initialize(this);
    }

    public static ImageLoader getImageLoader(Context context) {
        if (null == imageLoader) {
            DisplayImageOptions defaultOptions
                    = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageOnLoading(R.mipmap.ic_launcher)
                    .showImageForEmptyUri(R.mipmap.ic_launcher)
                    .showImageOnFail(R.mipmap.ic_launcher)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoaderConfiguration config
                    = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .memoryCache(new WeakMemoryCache())
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
        }
        return imageLoader;
    }

    public ConcurrentHashMap<String, Handler> getHandlerMap() {
        return mHandlerMap;
    }

    public static void broadcastMessage(Message msg) {
        for (Handler handler : getInstance().getHandlerMap().values()) {
            handler.sendMessage(Message.obtain(msg));
        }
    }
}
