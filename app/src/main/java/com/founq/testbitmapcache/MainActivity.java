package com.founq.testbitmapcache;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //没什么重要的东西
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(new MyAdapter(this));

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
////        i(bitmap);//image:5040*2835  memory:57153600
////        Bitmap optimizeBitmap = ImageResize.resizeBitmap(getApplicationContext(), R.drawable.test, 80, 80, false);
////        i(optimizeBitmap);//image:315*176  memory:110880
//
////        Bitmap bitmap = null;
////        for (int i = 0; i < 100; i++) {
////            bitmap = ImageResize.resizeBitmap(getApplicationContext(), R.drawable.test, 80, 80, false);//会一直gc
////        }
////        i(bitmap);
//        Bitmap reusableBitmap = ImageResize.resizeBitmap(getApplicationContext(), R.drawable.test, 80, 80, false, bitmap);//如果图片变化就会覆盖
//        i(bitmap);
//        i(reusableBitmap);
    }

    void i(Bitmap bitmap) {
        Log.i("testMainActivity", "image:" + bitmap.getWidth() + "*" + bitmap.getHeight() + "  memory:" +
                bitmap.getByteCount());
    }
}