package or.kr.mrhi.mp3project;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.chromium.base.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MusicData> arrayList;
    public static final int MAX_IMAGE_SIZE = 170;
    public static final BitmapFactory.Options options = new BitmapFactory.Options();

    public MyAdapter(Context context, ArrayList<MusicData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_item, viewGroup, false);

        }
        ImageView ivAlbum = view.findViewById(R.id.ivAlbum);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView TvArtist = view.findViewById(R.id.TvArtist);
        //Long.parseLong(arrayList.get(position).getAlbumId())로 해야한다 int안됨
        Bitmap bitmap = getAlbumImage(context, Long.parseLong(arrayList.get(position).getAlbumId()), MAX_IMAGE_SIZE);
        if (bitmap != null) {
            ivAlbum.setImageBitmap(bitmap);
        } else {
            ivAlbum.setImageResource(R.drawable.music_icon);
        }

        //가수의 이름 가수의 곡이름을 설정해준다.
        tvTitle.setText(arrayList.get(position).getTitle());
        TvArtist.setText(arrayList.get(position).getArtist());

        return view;
    }

    private Bitmap getAlbumImage(Context context, Long albumId, int maxImageSize) {
        //컨텐트리졸버 앨범이미지 아이디를 통해서 위치값 uri값을 가져온다.
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumId);

        if (uri != null) {
            //이미지를 가져오기위해 parcelFileDescriptor
            ParcelFileDescriptor pfd = null;
//            File file = null;
//            file = new File("abc.jpg","r");
            try {
                pfd = contentResolver.openFileDescriptor(uri, "r");
                //파일을 비트맵으로 변환한다.(bitmapFactory decode를 통해서 파일을 이미지로 전환
                BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);
                //OPTIONS.inJustDecodeBounds = true;로 이미지를 비트맵으로 변환해서 리턴하지않고
                //비트맵 부가적인 정보만(가로사이즈,세로사이즈 등정보) OPTIONS에 저장한다,=.
                options.inJustDecodeBounds = true;
                //비트맵 부가적인 정보를 통해서 비트맵 크기를 체크한다.정보는 OPTIONS에있다.
                int scale = 0;
                if (options.outHeight > maxImageSize || options.outWidth > maxImageSize) {
                    //우리가 원하는 이미지 사이즈로 전환하는 scale값을 구한다.
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }


                //비트맵을 가져온다
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);

                if (bitmap != null) {
                    if (options.outHeight != maxImageSize || options.outWidth != maxImageSize) {
                        Bitmap temBitmap = Bitmap.createScaledBitmap(bitmap, maxImageSize, maxImageSize, true);
                        bitmap.recycle();
                        bitmap = temBitmap;
                    }
                }
                return bitmap;
            } catch (FileNotFoundException e) {
                Log.d("음악플레이어", "비트맵 이미지 변환오류" + e.toString());
            } finally {
                if (pfd != null) {
                    try {
                        pfd.close();
                    } catch (IOException e) {
                        Log.d("음악플레이어", "ParcelFileDescriptor 이미지 변환오류" + e.toString());
                    }
                }
            }

        }


        return null;
    }
}
