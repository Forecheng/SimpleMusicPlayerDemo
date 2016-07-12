package com.lpc.simplemusicplayerdemo.upload;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Description: 上传文件线程
 * Created by lpc on 2016/7/12.
 */
public class UploadThread extends Thread {

    private String mFileName;
    private String mUrl;
    private String boundary = "------------------56434116466623";
    private String prefix = "--";
    private String end = "\r\n";

    public UploadThread(String url, String filename) {
        this.mFileName = filename;
        this.mUrl = url;
    }

    public void run() {
        try {
            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            //begin >>>>>>>>>>>>构建数据
            outputStream.writeBytes(prefix + boundary + end);    //边界符
            /**
             * 1.边界符
             * 2.数据
             * 3.回车换行
             * 4.回车换行
             * 5.边界符
             * */
            FileInputStream fileInputStream = new FileInputStream(new File(mFileName));
            byte[] data = new byte[1024*4];   //4kB
            int len;
            while((len = fileInputStream.read(data)) != -1){
                outputStream.write(data,0,len);
            }
            outputStream.writeBytes(end);
            outputStream.writeBytes(prefix+boundary+prefix+end);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb =  new StringBuffer();
            String str;
            while((str = reader.readLine())!= null){
                sb.append(str);
            }

            System.out.println("respond:"+ sb.toString());

            //end <<<<<<<<<<<<<构建数据

            if (reader != null){
                reader.close();
            }
            if (outputStream != null){
                outputStream.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //
        }
    }
}
