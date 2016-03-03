package com.meizu.ruandongchuan.dlnatest.data.event;

/**
 * Created by ruandongchuan on 16-1-5.
 */
public class EventMain {
    private int tag;
    private String msg;
    public EventMain(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public EventMain(int tag){
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
}
