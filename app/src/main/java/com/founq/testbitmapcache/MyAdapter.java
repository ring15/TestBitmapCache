package com.founq.testbitmapcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by ring on 2021/3/8.
 */
public class MyAdapter extends BaseAdapter {

    private Context mContext;

    public MyAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);
            holder.imageView = convertView.findViewById(R.id.img_bitmap);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //从内存
        Bitmap bitmap = ImageCache.getInstance().getBitmapFormMemory(String.valueOf(position));
        if (bitmap == null) {
            //从复用池
            Bitmap reusable = ImageCache.getInstance().getReusable(80, 80, 8);
            //从磁盘
            bitmap = ImageCache.getInstance().getBitmapFromDisk(String.valueOf(position), reusable);
            if (bitmap == null) {
                //从网络
                bitmap = ImageResize.resizeBitmap(mContext, R.drawable.test, 80, 80, false, null);
                ImageCache.getInstance().putBitmapToMemory(String.valueOf(position), bitmap);
                ImageCache.getInstance().putBitmapToDisk(String.valueOf(position), bitmap);
                Log.i("test", "网络获取了图片");
            } else {
                Log.i("test", "磁盘获取了图片");
            }
        } else {
            Log.i("test", "内存获取了图片");
        }

        holder.imageView.setImageBitmap(bitmap);

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
