package com.example.openglesfirstapp;

import android.opengl.GLES30;

import javax.microedition.khronos.opengles.GL10;

/**
 * Interface for any drawable shape
 */
public interface Shape
{
    public void draw(float[] vPMatrix);
}
