package or.kr.mrhi.mp3project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class Fragment1 extends Fragment{
    ListView listView1;
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    private ArrayList<MusicData> arrayList2 = new ArrayList<>();
    MyAdapter myAdapter;
    Cursor cursor;
    Context context;

    public Fragment1(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1, container, false);
        listView1 = view.findViewById(R.id.listView1);
        Bundle bundle=getArguments();
        arrayList = (ArrayList<MusicData>) bundle.getSerializable("arrayList");
        arrayList2 = (ArrayList<MusicData>) bundle.getSerializable("id");
        myAdapter = new MyAdapter(context, arrayList);
        myAdapter.notifyDataSetChanged();
        listView1.setAdapter(myAdapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent=new Intent(getActivity(),MusicActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("arrayList",arrayList);
                intent.putExtra("id",arrayList2);
                startActivity(intent);
            }
        });


        myAdapter.notifyDataSetChanged();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        myAdapter.notifyDataSetChanged();
        Log.d("로그","떠라");
    }


}

