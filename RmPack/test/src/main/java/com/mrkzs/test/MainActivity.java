package com.mrkzs.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

   /* static {
        System.loadLibrary("untitled_arm64-v8a");
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.e("aaa","tttttttt "+add(1,2));

    }

//    static native int add(int x, int y);
}
