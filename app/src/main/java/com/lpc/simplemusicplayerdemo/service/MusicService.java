package com.lpc.simplemusicplayerdemo.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.support.annotation.Nullable;

import com.lpc.simplemusicplayerdemo.Constant;
import com.lpc.simplemusicplayerdemo.bean.MusicBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: music play service class
 * Created by lpc on 2016/6/12.
 */
public class MusicService extends Service {

    private MediaPlayer mPlayer;
    private List<MusicBean> musicLists = new ArrayList<>();
    private List<Map<String,Object>> list = new ArrayList<>();

    /**
     * 播放状态：
     * 0x11:停止状态
     * 0x12:播放状态
     * 0x13:暂停状态
     * */
    private int mStatus = 0x11;   //当前的播放状态
    private int mCurrent = 0;     //表示播放过的文件数
    private  int count = 0;
    private int flag = 0;

    private MyReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        flag = 1;

        showMusicLists();
        count = musicLists.size();
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(Constant.CTRL_ACTION);
        registerReceiver(receiver,intentFilter);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCurrent ++;
                if (mCurrent > count){
                    mCurrent = 0;
                }
                playMusicFile(musicLists.get(mCurrent).getData());
            }
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (flag == 2){
            Intent sendIntent = new Intent(Constant.UPDATE_ACTION);
            sendIntent.putExtra("update",mStatus);
            sendBroadcast(sendIntent);
        }
        flag = 2;
    }

    /**
     * 显示音乐文件
     * */
    public void showMusicLists() {
        //到外存储设备取文件，并将文件信息显示
        String[] music = new String[]{Audio.Media._ID, Audio.Media.DISPLAY_NAME, Audio.Media.TITLE,
                Audio.Media.DURATION, Audio.Media.ARTIST, Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI,music,null,null,null);
        while (cursor.moveToNext()){
            MusicBean bean = new MusicBean();
            bean.setFileName(cursor.getString(1));
            bean.setTitle(cursor.getString(2));
            bean.setDuration(cursor.getInt(3));
            bean.setSongAuthor(cursor.getString(4));
            bean.setData(cursor.getString(5));
            musicLists.add(bean);

            Map<String,Object> map = new HashMap<>();
            map.put("name",cursor.getString(1));    //歌名
            map.put("artist",cursor.getString(4));   //歌手
            list.add(map);
        }
    }

    /**
     * 播放音乐文件
     * @param path file path
     * */
    public void playMusicFile(String path){
        try{
            //重置MediaPlayer
            mPlayer.reset();
            //设置要播放文件的路径
            mPlayer.setDataSource(path);
            //
            mPlayer.prepare();
            //开始播放
            mPlayer.start();
            //播放完成的动作监听
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完成后，接着播放下一首
                    mCurrent--;

                    if (mCurrent < 0){
                        mCurrent = count;    //全都播放过了之后，重新来一遍，也就是：未播放的文件数=总的文件数
                    }
                    String fileName = musicLists.get(mCurrent).getData();
                    playMusicFile(fileName);
                    mStatus = 0x12;
                }
            });
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = 0;
        //当activity销毁的时候，释放掉播放器
        if (mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
        }

        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //接收歌曲控制的广播
    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control",-1);
            switch (control){
                case 1:
                    //处于停止状态
                    if (mStatus == Constant.STATUS_STOP){
                        playMusicFile(musicLists.get(mCurrent).getData());
                        mStatus = Constant.STATUS_PLAY;
                    }
                    //处于播放状态
                    else if (mStatus == Constant.STATUS_PLAY){
                        mPlayer.pause();
                        mStatus = Constant.STATUS_PAUSE;
                    }
                    //处于暂停状态
                    else if (mStatus == Constant.STATUS_PAUSE){
                        mPlayer.start();
                        mStatus = Constant.STATUS_PLAY;
                    }
                    break;
                case 2:
                    if (mStatus == Constant.STATUS_PLAY || mStatus == Constant.STATUS_PAUSE){
                        mPlayer.stop();
                        mStatus = Constant.STATUS_STOP;
                    }
                    break;
                case 3:
                    mCurrent--;
                    if (mCurrent < 0){
                        mCurrent = count;
                    }
                    playMusicFile(musicLists.get(mCurrent).getData());
                    mStatus = Constant.STATUS_PLAY;
                    break;
                case 4:
                    mCurrent++;
                    if (mCurrent > count){
                        mCurrent = 0;
                    }
                    playMusicFile(musicLists.get(mCurrent).getData());
                    mStatus = Constant.STATUS_PLAY;
                    break;
                case 5:
                    mCurrent = intent.getIntExtra("current",-1);
                    playMusicFile(musicLists.get(mCurrent).getData());
                    mStatus = Constant.STATUS_PLAY;
                    break;

            }

            Intent sendIntent = new Intent(Constant.UPDATE_ACTION);
            sendIntent.putExtra("update",mStatus);    //传递当前的状态
            sendBroadcast(sendIntent);
        }
    }
}
