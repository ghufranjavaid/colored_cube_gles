package com.example.openglesfirstapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;
import android.opengl.GLES30;

public class Triangle implements Shape{

    private final String vertexShader =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShader =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle;
    private int colorHandle;
    private int mMVPMatrixHandle;

    static final int COORDS_PER_VERTEX = 3;

    static float vertices[] = {
            0.0f,  0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };

    private final int vertexCount = vertices.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    final int positionBuffer[] = new int[1];

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

    public Triangle()
    {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        // use the device hardware's native byte order
        byteBuffer.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = byteBuffer.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(vertices);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        mProgram = GLES30.glCreateProgram();
        createShaders(mProgram);

        GLES30.glGenBuffers(1, positionBuffer, 0);
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, positionBuffer[0]);

        // Transfer data from client memory to the buffer.
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4,//BYTES_PER_FLOAT,
                vertexBuffer, GLES30.GL_STATIC_DRAW);


        // get handle to vertex shader's vPosition member
        positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        if (positionHandle == -1)
        {
            throw new RuntimeException("vPosition attribute location invalid");
        }

        // get handle to fragment shader's vColor member
        colorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        if (colorHandle == -1)
        {
            throw new RuntimeException("vColor attribute location invalid");
        }

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        if (mMVPMatrixHandle == -1)
        {
            throw new RuntimeException("uMVPMatrix attribute location invalid");
        }
    }

    private void createShaders(int program)
    {
        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES30.GL_VERTEX_SHADER, this.vertexShader);
        int fragmentShader = MyGLRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, this.fragmentShader);

        GLES30.glAttachShader(program, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(program, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(program);                  // create OpenGL program executables
    }

    @Override
    public void draw(float[] vPMatrix)
    {
        GLES30.glUseProgram(mProgram);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, positionBuffer[0]);
        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, 0);

        // Set color for drawing the triangle
        GLES30.glUniform4fv(colorHandle, 1, color, 0);

        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(positionHandle);
    }

}
