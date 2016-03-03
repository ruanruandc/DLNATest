package com.meizu.ruandongchuan.dlnatest.engine;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.meizu.ruandongchuan.dlnatest.data.event.EventMain;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.view.DlnaApp;
import com.meizu.ruandongchuan.dlnatest.view.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by ruandongchuan on 16-1-6.
 */
public class LocalController {
    public static void play(String path, String metadata) {
        if (ControlPointContainer.getInstance().getSelectedDevice() == null){
            EventBus.getDefault().post(new EventMain(MainActivity.EVENT_SHOW));
        }else {
            new AsyncTask<String, Integer, Boolean>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected Boolean doInBackground(String... params) {
                    return ActionController.getInstance().play(
                            ControlPointContainer.getInstance().getSelectedDevice(),
                            DLNAUtil.getUrl(params[0]),
                            params[1]);
                }

                @Override
                protected void onProgressUpdate(Integer... progresses) {

                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        Toast.makeText(DlnaApp.getInstance().getApplicationContext(),
                                "播放成功",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DlnaApp.getInstance().getApplicationContext(),
                                "播放失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected void onCancelled() {
                }
            }.execute(path, metadata);
        }
    }
    public static void getPostionInfo(){
        new AsyncTask<String,Integer,String>(){

            @Override
            protected String doInBackground(String... params) {
                return ActionController.getInstance().getPositionInfo(ControlPointContainer.getInstance().getSelectedDevice());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("getPostionInfo",s == null ? "null" : s);
            }
        }.execute();
    }
    public static void getTranportInfo(){
        new AsyncTask<String,Integer,String>(){

            @Override
            protected String doInBackground(String... params) {
                ActionController.getInstance().getMediaDuration(ControlPointContainer.getInstance().getSelectedDevice());
                return ActionController.getInstance().getTransportInfo(ControlPointContainer.getInstance().getSelectedDevice());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("getTranportInfo",s == null ? "null" : s);
            }
        }.execute();
    }

    public static void autoIncreasing(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getPostionInfo();
                getTranportInfo();
                autoIncreasing();
            }
        },1000);
    }
}
