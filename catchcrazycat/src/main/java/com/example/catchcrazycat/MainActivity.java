package com.example.catchcrazycat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(new Playground(this));
//        TextView tv_one =findViewById(R.id.tv_one);
//        tv_one.setText("红色点表示神经猫，白色点表示空位，黄色点表示路障"+
//                "游戏目的：阻止红点逃脱地图" +
//                "游戏玩法：每回合玩家可以选择一个空位放置路障，神经猫则会移动一位");
    }
}