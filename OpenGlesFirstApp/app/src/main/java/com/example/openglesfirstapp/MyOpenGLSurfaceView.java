package com.example.openglesfirstapp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyOpenGLSurfaceView extends GLSurfaceView
{

    private MyGLRenderer renderer;
    public MyOpenGLSurfaceView(Context context)
    {
        super(context);
        init(context);
    }

    public MyOpenGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public void init(Context context)
    {
        setEGLContextClientVersion(3);
        renderer = new MyGLRenderer();
        setRenderer(renderer);

    }
}
