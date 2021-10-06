package or.kr.mrhi.mp3project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 2; //페이지 수
    private ViewPager2 pager;
    private FragmentStateAdapter pagerAdapter;
    private TabLayout tabLayout;
    final List<String> tabElement = Arrays.asList("Total", "Like");
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    private ArrayList<MusicData> arrayList2 = new ArrayList<>();
    Cursor cursor;
    SQLiteDatabase sqlDB;
    myDBHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);

        pagerAdapter = new ScreeSlidePagerAdapter(this);
        pagerAdapter.notifyDataSetChanged();
        pager.setAdapter(pagerAdapter);
        myDBHelper = new myDBHelper(this);
        getMusicList();
        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(MainActivity.this);
                textView.setGravity(Gravity.CENTER);
                textView.setText(tabElement.get(position));
                tab.setCustomView(textView);
            }
        }).attach();

    }


    private void getDBData() {

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        arrayList2 = new ArrayList<>();
        cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;",null);

        String id= null;
        String albumId = null;
        String title = null;
        String artist = null;
        String ok= null;

        while (cursor.moveToNext()){
            id = cursor.getString(0);
            albumId = cursor.getString(1);
            title = cursor.getString(2);
            artist = cursor.getString(3);
            ok = cursor.getString(4);
            MusicData musicData=new MusicData(id,albumId,title,artist,ok);
            arrayList2.add(musicData);
            Log.d("음악",id+albumId+title+artist+ok);
        }
        pagerAdapter.notifyDataSetChanged();
        cursor.close();
        sqlDB.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDBData();
        pagerAdapter = new ScreeSlidePagerAdapter(this);
        pagerAdapter.notifyDataSetChanged();
        pager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();
        Log.d("음악","재실행");
    }


    private class ScreeSlidePagerAdapter extends FragmentStateAdapter {

        public ScreeSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                Fragment fragment1 = new Fragment1(MainActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("id",arrayList2);
                bundle.putSerializable("arrayList",arrayList);
                fragment1.setArguments(bundle);

                return fragment1;
            } else if (position == 1){
                Fragment fragment2 = new Fragment2(MainActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("arrayList",arrayList2);
                fragment2.setArguments(bundle);
                return fragment2;

            }

            return new Fragment2(MainActivity.this);

        }


        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }//end of adapter
    private void getMusicList() {
        //외장메모리를 사용하기위한 퍼미션
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);
        //정보를 담을 컬럼
        String[] colums = new String[]{MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST};
        try {
            //쿼리문
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, colums, null, null, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                //위의 데이터로 MusicData 작성
                MusicData musicData = new MusicData(id, albumId, title, artist);
                //MusicData 리스트에 추가
                arrayList.add(musicData);
            }
        }catch(Exception e) {
            Log.d("음악플레이어","음악컨텐트프로바이더 mp3로딩 오류발생"+e.toString());
        }finally{
            if(cursor!=null){
                cursor.close();
            }
        }



    }//end of musicList

}
