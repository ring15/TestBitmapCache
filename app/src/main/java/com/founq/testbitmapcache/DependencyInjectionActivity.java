package com.founq.testbitmapcache;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.founq.testbitmapcache.inject.InjectLayout;
import com.founq.testbitmapcache.inject.InjectManager;
import com.founq.testbitmapcache.inject.InjectView;
import com.founq.testbitmapcache.inject.OnClick;
import com.founq.testbitmapcache.inject.OnLongClick;

/**
 * 依赖注入
 */
@InjectLayout(value = R.layout.activity_dependency_injection)
public class DependencyInjectionActivity extends AppCompatActivity {

    @InjectView(value = R.id.btn)
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dependency_injection);
        InjectManager.inject(this);
//        mButton = findViewById(R.id.btn);

//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("test", "点击");
//            }
//        });

//        mButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Log.i("test", "长按");
//                return false;
//            }
//        });
    }

    @OnClick(value = {R.id.btn})
    public void onClick(View view) {
        Log.i("test", "点击");
    }

    @OnLongClick({R.id.btn})
    public boolean onLongClick(View view) {
        Log.i("test", "长按");
        return false;
    }
}