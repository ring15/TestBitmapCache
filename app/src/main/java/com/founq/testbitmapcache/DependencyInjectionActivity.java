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
import com.founq.testbitmapcache.rxjava.Observable;
import com.founq.testbitmapcache.rxjava.Observer;
import com.founq.testbitmapcache.rxjava.User;
import com.founq.testbitmapcache.rxjava.WechatServer;

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
        client();
    }

    private void client() {
        //创建一个微信公众号（被观察者）
        Observable server = new WechatServer();
        //创建用户（观察者）
        Observer jett = new User("jett");
        Observer alven  = new User("alven");
        Observer lance  = new User("lance");
        //订阅
        server.add(jett);
        server.add(alven);
        server.add(lance);
        ((WechatServer)server).pushMessage("消息");
    }

    @OnLongClick({R.id.btn})
    public boolean onLongClick(View view) {
        Log.i("test", "长按");
        return false;
    }
}