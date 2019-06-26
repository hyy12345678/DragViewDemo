package com.ton.dragviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends AppCompatActivity {

    private DragView mDragView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化ImageLoader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(MainActivity.this));
        setContentView(R.layout.activity_main);
        mDragView = (DragView) findViewById(R.id.dragview);
//        mDragView.setImageResource(R.mipmap.dragon_y,R.mipmap.dragon_label_y);
        mDragView.setImageUrl("http://hyy12345678.uicp.net:8666/wp-content/uploads/2019/06/dragon_z.png",
                "http://hyy12345678.uicp.net:8666/wp-content/uploads/2019/06/dragon_label_z.png");
        mDragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Clicked me",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
