package com.alarm.qwerty.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import com.alarm.qwerty.Adapter.MusicAdapter;
import com.alarm.qwerty.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends Activity {

    private static final String MUSIC_FILE_NAME = "music.txt";

    private List<MusicName> Musics = new ArrayList<>();

    private ListView music_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);
        File file = Environment.getExternalStorageDirectory();
        if (fileIsExists()){
            try {
                WriteToMusics();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            getName(file);
            for(MusicName musicName: Musics){
                Save(MUSIC_FILE_NAME, musicName.getMusicName());
            }
        }
        MusicAdapter musicAdapter = new MusicAdapter(this, R.layout.music_lv_item, Musics);
        music_lv = (ListView) findViewById(R.id.music_lv);
        music_lv.setAdapter(musicAdapter);
    }

/*
* by.qwerty
* 使用递归的方法遍历SD卡中的所有文件夹和文件
* 如果是文件夹，则将文件夹中的文件取出后，进一步判断
* 如果是文件则判断是否以.mp3结尾
***********************************************************************************
* 使用中碰到的问题：File[] files = file.listFiles();这一步需要有相应的权限才可以，
* 比如我的代码中读SD卡，没有权限，则files一直为null，每次都闪退
* */
    public void getName(File file){
        File[] files = file.listFiles();
        if(files.length > 0){
            for(int i = 0; i < files.length; ++i){
                if (files[i].isDirectory()){
                    getName(files[i]);
                }else if (files[i].isFile()){
                    if (files[i].getName().endsWith(".mp3")){
                        Musics.add(new MusicName(files[i].getName()));
                    }
                }
            }
        }else {
            return;
        }
    }

    public class MusicName{
        private String name;

        public MusicName(String name){
            this.name = name;
        }
        public String getMusicName(){
            return name;
        }
    }

    /*
    * by.qwerty
    * 向文件中写入数据
    * */
    public void Save(String FileName, String data){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            out = openFileOutput(FileName, Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
            writer.write("\r\n");
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if (writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
/*
* by.qwerty
* 判断文件是否存在
*
* 遇到的问题，使用file1 == MUSIC_FILE_NAME时，每次都报不相等，使用equals后才正常
* */
    public boolean fileIsExists(){
        try{
            File file = this.getFilesDir();
            String[] files = file.list();
            for (String file1:files){
                if (file1.equals(MUSIC_FILE_NAME)){
                    return true;
                }
            }
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }
/*
* by.qwerty
* 取出文件中的数据，逐行读出并放入Musics中，这样读起来速度非常快，比递归快很多倍
*
* 碰到的问题：中文乱码问题。之前的方法是使用DataInputStream方法读出数据，不能更正编码。需要注意
* */
    public void WriteToMusics() throws IOException{
        FileInputStream fileInputStream = openFileInput(MUSIC_FILE_NAME);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
//        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            MusicName musicName = new MusicName(line);
            Musics.add(musicName);
        }
        bufferedReader.close();
        fileInputStream.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}