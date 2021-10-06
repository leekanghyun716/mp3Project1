package or.kr.mrhi.mp3project;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class Fragment2 extends Fragment {
    myDBHelper myDBHelper;
    ListView listView2;
    Context context;
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    MyAdapter myAdapter;
    SQLiteDatabase sqlDB;

    public Fragment2(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment2, container, false);
        listView2 = view.findViewById(R.id.listView2);
        Bundle bundle=getArguments();
        arrayList = (ArrayList<MusicData>) bundle.getSerializable("arrayList");
        myAdapter = new MyAdapter(context, arrayList);
        myAdapter.notifyDataSetChanged();
        listView2.setAdapter(myAdapter);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent=new Intent(getActivity(),MusicActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("arrayList",arrayList);
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
    }
}

