package or.kr.mrhi.mp3project;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private ImageView album, previous, play, pause, next, heart,suffle;
    private SeekBar seekBar;
    private ContentResolver res;
    //쓰레드를 상속받는 클래스
    private ProgressUpdate progressUpdate;
    private int position;
    private ArrayList<MusicData> list;
    private ArrayList<MusicData> list2;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;
    myDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    boolean suffleFlag = false;
    String id= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //인텐트를 통해 건너온 position 값과 arraylist값을 불러온다
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        list = (ArrayList<MusicData>) intent.getSerializableExtra("arrayList");
        list2 = (ArrayList<MusicData>) intent.getSerializableExtra("id");


        title = (TextView) findViewById(R.id.title);
        album = (ImageView) findViewById(R.id.album);
        seekBar = (SeekBar) findViewById(R.id.seekbar);


        previous = (ImageView) findViewById(R.id.pre);
        suffle = (ImageView) findViewById(R.id.suffle);
        heart = (ImageView) findViewById(R.id.heart);
        play = (ImageView) findViewById(R.id.play);
        pause = (ImageView) findViewById(R.id.pause);
        next = (ImageView) findViewById(R.id.next);



        myDBHelper = new myDBHelper(this);
        //초기화
        //이미지위치값을 가져오기위하여 설정함
        mediaPlayer = new MediaPlayer();
        res = getContentResolver();
        //음악을 시작함
        playMusic(list.get(position));
        //시크바도 동시에 시작하기위하여
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();
        getDBData();
        if(list.get(position).getId().equals(id)){
            heart.setImageResource(R.drawable.heart);
        }
        //이벤트등록및 설정
        previous.setOnClickListener(this);
        suffle.setOnClickListener(this);
        heart.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if (seekBar.getProgress() > 0 && play.getVisibility() == View.GONE) {
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (position + 1 < list.size()) {
                    position++;
                    playMusic(list.get(position));
                }
            }
        });

    }

    public void playMusic(MusicData musicDto) {

        try {
            seekBar.setProgress(0);
            title.setText(musicDto.getArtist() + " - " + musicDto.getTitle());
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicDto.getId());

            mediaPlayer.reset();

            //듣고자하는 파일을 프로바이더가 가져온다.
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            if (mediaPlayer.isPlaying()) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            } else {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

            String result = getCoverArtPath(Long.parseLong(musicDto.getAlbumId()), getApplication());

            if (result != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(result);
                album.setImageBitmap(bitmap);
            } else {
                album.setImageResource(R.drawable.mp3);
            }

        } catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
    }

    //앨범이 저장되어 있는 경로를 리턴합니다.
    private static String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        Log.d("음악플레이어", "음악파일이미지 경로" + result);
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                Log.d("음악",list.get(position).getId()+id);
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();

                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;
            case R.id.pre:
                if (position - 1 >= 0) {
                    position--;
                    playMusic(list.get(position));
                    seekBar.setProgress(0);
                    if(list.get(position).getId().equals(id)){
                        heart.setImageResource(R.drawable.heart);
                    }else{
                        heart.setImageResource(R.drawable.heart1);
                    }
                }
                break;
            case R.id.next:
                if(suffleFlag==false){

                    if (position + 1 < list.size()) {
                        position++;
                        playMusic(list.get(position));
                        seekBar.setProgress(0);
                        if(list.get(position).getId().equals(id)){
                            heart.setImageResource(R.drawable.heart);
                        }else{
                            heart.setImageResource(R.drawable.heart1);
                        }
                    }
                }else{

                    if (position + 1 < list.size()) {
                        position=(int)(Math.random()*(list.size()-1+1)+1);
                        playMusic(list.get(position));
                        seekBar.setProgress(0);
                        if(list.get(position).getId().equals(id)){
                            heart.setImageResource(R.drawable.heart);
                        }else{
                            heart.setImageResource(R.drawable.heart1);
                        }
                    }
                }

                break;
            case R.id.heart:
                if (list.get(position).getId().equals(id)) {
                    sqlDB = myDBHelper.getWritableDatabase();
                    sqlDB.execSQL("DELETE FROM groupTBL where id ='" + list.get(position).getId() + "';");
                    heart.setImageResource(R.drawable.heart1);
                    Log.d("삭제", "삭제완료" + list.get(position).getId());
                } else {
                    sqlDB = myDBHelper.getWritableDatabase();
                    sqlDB.execSQL("INSERT INTO groupTBL VALUES ( '" + list.get(position).getId() + "' , '" + list.get(position).getAlbumId() + "','" + list.get(position).getTitle() + "','" + list.get(position).getArtist() + "','ok');");
                    sqlDB.close();
                    heart.setImageResource(R.drawable.heart);
                    Toast.makeText(getApplicationContext(), "입력됨", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.suffle:
                if(suffleFlag==false){
                    suffle.setImageResource(R.drawable.suffle1);
                    suffleFlag = true;
                }else{
                    suffle.setImageResource(R.drawable.suffle);
                    suffleFlag = false;
                }
                break;
        }
    }

    //쓰래드 1번방식
    class ProgressUpdate extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(300);
                    if (mediaPlayer != null) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seekBar.setProgress((int) mediaPlayer.getCurrentPosition());
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("ProgressUpdate", e.getMessage());
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void getDBData() {
        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        list2 = new ArrayList<>();
        cursor = sqlDB.rawQuery("SELECT * FROM groupTBL where id = '"+list.get(position).getId()+"';",null);

        while (cursor.moveToNext()){
            id = cursor.getString(0);
        }
        cursor.close();
        sqlDB.close();
    }
}