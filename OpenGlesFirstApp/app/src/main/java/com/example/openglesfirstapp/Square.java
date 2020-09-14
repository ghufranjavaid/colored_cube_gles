package com.example.openglesfirstapp;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square implements Shape {

    private final String vertexShader =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
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
    private final ShortBuffer drawBuffer;

    private final short drawOrder[] = {0, 1, 2, 0, 2, 3};


    final int positionBuffer[] = new int[1];

    final int indexBuffer[] = new int[1];

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    static float vertices[] = {
            -0.5f, 0.311004243f, 0.0f,    // top left
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f,    // bottom right
            0.5f, 0.311004243f, 0.0f      // top right
    };

    private final int vertexCount = vertices.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

    public Square()
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

        ByteBuffer orderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2);
        orderBuffer.order(ByteOrder.nativeOrder());

        drawBuffer = orderBuffer.asShortBuffer();

        drawBuffer.put(drawOrder);
        drawBuffer.position(0);

        mProgram = GLES30.glCreateProgram();

        createShaders(mProgram);


        GLES30.glGenBuffers(1, positionBuffer, 0);
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, positionBuffer[0]);

        // Transfer data from client memory to the buffer.
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4,//BYTES_PER_FLOAT,
                vertexBuffer, GLES30.GL_STATIC_DRAW);



        GLES30.glGenBuffers(1, indexBuffer, 0);
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);

        // Transfer data from client memory to the buffer.
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, drawBuffer.capacity() * 2, // Multiply by 2 for short
                drawBuffer, GLES30.GL_STATIC_DRAW);

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
        int vertexShader = MyGLRenderer.loadShader(
                GLES30.GL_VERTEX_SHADER, this.vertexShader);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES30.GL_FRAGMENT_SHADER, this.fragmentShader);

        GLES30.glAttachShader(program, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(program, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(program);                  // create OpenGL program executables
    }

    @Override
    public void draw(float[] vPMatrix)
    {
        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram);

        // Bind the position and indices buffers
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, positionBuffer[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);

        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, 0);


        GLES30.glUniform4fv(colorHandle, 1, color, 0);

        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the Square
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.length, GLES30.GL_UNSIGNED_SHORT, 0);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(positionHandle);
    }

}
