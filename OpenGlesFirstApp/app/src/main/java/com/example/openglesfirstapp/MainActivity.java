package com.example.openglesfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private MyOpenGLSurfaceView openGlView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openGlView = (MyOpenGLSurfaceView) findViewById(R.id.myOpenGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        openGlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        openGlView.onPause();
    }
}