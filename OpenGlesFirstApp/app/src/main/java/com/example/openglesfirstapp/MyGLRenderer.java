package com.example.openglesfirstapp;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer
{
    private Shape shape;

    // Model View Projection Matrix
    private final float[] vPMatrix = new float[16];

    private final float[] modelProjectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] rotationMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        shape = new Cube();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES30.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        Matrix.frustumM(modelProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        float[] newVPMatrix = new float[16];

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(modelViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);


        /** Create matrices from the euler angles */
        Matrix.setRotateM(modelViewMatrix, 0, getRotationAngle(), 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(rotationMatrix, 0, getRotationAngle(), 0.0f, 1.0f, 0.0f);

        /** Combine the matrices into the Modeling Matrix*/
        Matrix.multiplyMM(modelViewMatrix, 0, rotationMatrix, 0, modelViewMatrix, 0);

        Matrix.setRotateM(rotationMatrix, 0, getRotationAngle(), 0.0f, 1.0f, 0.0f);

        /** Combine the matrices into the Modeling Matrix*/
        Matrix.multiplyMM(modelViewMatrix, 0, rotationMatrix, 0, modelViewMatrix, 0);

        Matrix.multiplyMM(newVPMatrix, 0, vPMatrix, 0, modelViewMatrix, 0);

        // Draw the Shape
        shape.draw(newVPMatrix);
    }

    /** Returns a rotation angle */
    private float getRotationAngle()
    {
        long time = SystemClock.uptimeMillis() % 4000L;
        return 0.090f * ((int) time);
    }

    public static int loadShader(int type, String shaderCode)
    {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    public static void checkGlError(String glOperation)
    {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR)
        {
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
