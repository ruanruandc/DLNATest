package com.meizu.ruandongchuan.dlnatest.util;


import android.os.Handler;

import com.meizu.ruandongchuan.dlnatest.view.DlnaApp;


public class HandlerController {
    private String mName;
    private Handler mHandler;
    public static final int STOP = 100;
    public static final int PAUSE = 101;
    public static final int PLAY = 102;
    public static final int SEEK = 103;

    public HandlerController(String name, Handler handler)
    {
        DlnaApp.getInstance().getHandlerMap().put(name, handler);
        this.mName = name;
        this.mHandler = handler;
    }

    public void destroy()
    {
        DlnaApp.getInstance().getHandlerMap().remove(mName);
    }
}
