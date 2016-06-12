package com.lpc.simplemusicplayerdemo.bean;

/**
 * Description:
 * Created by lpc on 2016/6/12.
 */
public class MusicBean {

    private String fileName;
    private String title;
    private int duration;
    private String songAuthor;
    private String location;

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }

    public void setSongAuthor(String songAuthor){
        this.songAuthor = songAuthor;
    }

    public String getSongAuthor(){
        return songAuthor;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getLocation(){
        return location;
    }

}
