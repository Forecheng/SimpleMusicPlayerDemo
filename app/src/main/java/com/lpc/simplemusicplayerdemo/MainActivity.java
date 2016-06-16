package com.lpc.simplemusicplayerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lpc.simplemusicplayerdemo.bean.MusicBean;
import com.lpc.simplemusicplayerdemo.service.MusicService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //界面组件
    private Button previousSong ;
    private Button nextSong;
    private Button pause;
    private Button play;
    private Button close;
    private Button exitApp;
    private ListView listView;

    private List<Object> musicLists = new ArrayList<Object>();
    private List<Map<String,Object>> list = new ArrayList<>();

    private int mState = Constant.STATUS_STOP;
    private Intent serviceIntent;
    private ActivityReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();

        musicList();
        receiver = new ActivityReceiver();
        IntentFilter intentFilter =  new IntentFilter();
        intentFilter.addAction(Constant.UPDATE_ACTION);
        registerReceiver(receiver,intentFilter);
        serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
    }

    /**
     * 初始化界面组件
     * */
    public void initViews(){
        previousSong = (Button)findViewById(R.id.previous_music);
        nextSong = (Button)findViewById(R.id.next_music);
        pause = (Button)findViewById(R.id.pause_music);
        play = (Button)findViewById(R.id.play_music);
        close = (Button)findViewById(R.id.close_player);
        exitApp = (Button)findViewById(R.id.exitApp);
        listView = (ListView)findViewById(R.id.music_listView);
    }

    /**
     * 组件监听器
     * */
    public void initEvents(){
        previousSong.setOnClickListener(this);
        nextSong.setOnClickListener(this);
        pause.setOnClickListener(this);
        play.setOnClickListener(this);
        close.setOnClickListener(this);
        exitApp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Constant.CTRL_Action);
        switch (v.getId()){
            case R.id.play_music:
                intent.putExtra("control",1);
                break;
            case R.id.pause_music:
                intent.putExtra("control",2);
                break;
            case R.id.previous_music:
                intent.putExtra("control",3);
                break;
            case R.id.next_music:
                intent.putExtra("control",4);
                break;
            case R.id.close_player:
                stopService(serviceIntent);
                break;
            case R.id.exitApp:
                stopService(serviceIntent);
                finish();
                break;
            default:
                break;

        }
        sendBroadcast(intent);
    }

    //接收播放状态改变时的广播
    public class ActivityReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //播放状态的更新
            int update = intent.getIntExtra("update",-1);
            switch (update){
                case Constant.STATUS_STOP:
                    play.setText("播放");
                    mState = Constant.STATUS_STOP;
                    break;
                case Constant.STATUS_PLAY:
                    play.setText("暂停");
                    mState = Constant.STATUS_PLAY;
                    break;
                case Constant.STATUS_PAUSE:
                    play.setText("播放");
                    mState = Constant.STATUS_PAUSE;
                    break;
            }
        }
    }

    /* 播放列表 */
    public void musicList() {
        // 取得指定位置的文件设置显示到播放列表
        String[] music = new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA };
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                music, null, null, null);
        while (cursor.moveToNext()) {
            MusicBean temp = new MusicBean();
            temp.setFileName(cursor.getString(1));
            temp.setTitle(cursor.getString(2));
            temp.setDuration(cursor.getInt(3));
            temp.setSongAuthor(cursor.getString(4));
            temp.setData(cursor.getString(5));
            musicLists.add(temp);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", cursor.getString(1));
            map.put("artist", cursor.getString(4));
            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.music_item, new String[] { "name", "artist" },
                new int[] { R.id.song_name, R.id.song_author });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int current, long id) {

                Intent intent=new Intent(Constant.CTRL_Action);
                intent.putExtra("control", 5);
                intent.putExtra("current", current);
                sendBroadcast(intent);
            }
        });
    }
}
