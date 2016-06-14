package com.lpc.simplemusicplayerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //组件
    private Button previousSong ;
    private Button nextSong;
    private Button pause;
    private Button play;
    private Button close;
    private Button exitApp;
    private ListView listView;

    private List<Object> musicLists = new ArrayList<Object>();
    private List<Map<String,Object>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
