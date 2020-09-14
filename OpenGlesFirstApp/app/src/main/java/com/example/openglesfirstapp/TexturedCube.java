package com.example.openglesfirstapp;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class TexturedCube implements  Shape
{
        private final String vertexShader =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "attribute vec3 av3colour;"+
                        "varying vec3 vv3colour;" +
                        "void main() {" +
                        "  vv3colour = av3colour;" +
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";

        private final String fragmentShader =
                "precision mediump float;" +
                        "varying vec3 vv3colour;" +
                        "void main() {" +
                        "  gl_FragColor = vec4(vv3colour, 1.0);" +
                        "}";

        private final FloatBuffer vertexBuffer;

        private final FloatBuffer colors;

        private final int mProgram;
        private int positionHandle;
        private int colorHandle;
        private int mMVPMatrixHandle;

        private final ShortBuffer drawBuffer;

        private final short drawOrder[] = {
                0, 1, 2, 				/* 1  */
                2, 1, 3, 				/* 2  */
                8, 5, 9, 				/* 3  */
                9, 5, 7, 				/* 4  */
                10, 11, 12, 			/* 5  */
                12, 11, 13, 			/* 6  */
                14, 4, 15, 				/* 7  */
                14, 15, 16, 			/* 8  */
                17, 18, 6, 				/* 9  */
                6, 18, 19, 				/* 10 */
                20, 21, 5, 				/* 11 */
                20, 5, 22 				/* 12 */
        };

        final int positionBuffer[] = new int[1];

        final int indexBuffer[] = new int[1];

        private int colorbuffer[] = new int [1];

        static final int COORDS_PER_VERTEX = 3;

        static float vertices[] = {
                -0.5f, -0.5f,  0.5f, 	/* 0  */
                0.5f, -0.5f,  0.5f, 	/* 1  */
                -0.5f,  0.5f,  0.5f, 	/* 2  */
                0.5f,  0.5f,  0.5f, 	/* 3  */
                -0.5f, -0.5f, -0.5f, 	/* 4  */
                0.5f, -0.5f, -0.5f, 	/* 5  */
                -0.5f,  0.5f, -0.5f, 	/* 6  */
                0.5f,  0.5f, -0.5f, 	/* 7  */
                0.5f, -0.5f,  0.5f, 	/* 8  */
                0.5f,  0.5f,  0.5f, 	/* 9  */
                0.5f, -0.5f, -0.5f, 	/* 10 */
                -0.5f, -0.5f, -0.5f, 	/* 11 */
                0.5f,  0.5f, -0.5f, 	/* 12 */
                -0.5f,  0.5f, -0.5f, 	/* 13 */
                -0.5f,  0.5f, -0.5f, 	/* 14 */
                -0.5f, -0.5f,  0.5f, 	/* 15 */
                -0.5f,  0.5f,  0.5f, 	/* 16 */
                -0.5f,  0.5f,  0.5f, 	/* 17 */
                0.5f,  0.5f,  0.5f, 	/* 18 */
                0.5f,  0.5f, -0.5f, 	/* 19 */
                -0.5f, -0.5f,  0.5f, 	/* 20 */
                -0.5f, -0.5f, -0.5f, 	/* 21 */
                0.5f, -0.5f,  0.5f 	/* 22 */
        };

        private final int vertexCount = vertices.length / COORDS_PER_VERTEX;
        private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        static float vertexColors[] = {
                0.583f,  0.771f,  0.014f,
                0.609f,  0.115f,  0.436f,
                0.327f,  0.483f,  0.844f,
                0.822f,  0.569f,  0.201f,
                0.435f,  0.602f,  0.223f,
                0.310f,  0.747f,  0.185f,
                0.597f,  0.770f,  0.761f,
                0.559f,  0.436f,  0.730f,
                0.359f,  0.583f,  0.152f,
                0.483f,  0.596f,  0.789f,
                0.559f,  0.861f,  0.639f,
                0.195f,  0.548f,  0.859f,
                0.014f,  0.184f,  0.576f,
                0.771f,  0.328f,  0.970f,
                0.406f,  0.615f,  0.116f,
                0.676f,  0.977f,  0.133f,
                0.971f,  0.572f,  0.833f,
                0.140f,  0.616f,  0.489f,
                0.997f,  0.513f,  0.064f,
                0.945f,  0.719f,  0.592f,
                0.543f,  0.021f,  0.978f,
                0.279f,  0.317f,  0.505f,
                0.167f,  0.620f,  0.077f,
                0.347f,  0.857f,  0.137f,
                0.055f,  0.953f,  0.042f,
                0.714f,  0.505f,  0.345f,
                0.783f,  0.290f,  0.734f,
                0.722f,  0.645f,  0.174f,
                0.302f,  0.455f,  0.848f,
                0.225f,  0.587f,  0.040f,
                0.517f,  0.713f,  0.338f,
                0.053f,  0.959f,  0.120f,
                0.393f,  0.621f,  0.362f,
                0.673f,  0.211f,  0.457f,
                0.820f,  0.883f,  0.371f,
                0.982f,  0.099f,  0.879f
        };

        public TexturedCube()
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

            ByteBuffer clBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2);
            clBuffer.order(ByteOrder.nativeOrder());

            ByteBuffer colorByteBUffer = ByteBuffer.allocateDirect(vertexColors.length * 4);
            colorByteBUffer.order(ByteOrder.nativeOrder());

            colors = colorByteBUffer.asFloatBuffer();
            colors.put(vertexColors);
            colors.position(0);

            GLES30.glGenBuffers(1, colorbuffer, 0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, colorbuffer[0]);

            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, colors.capacity() * 4,//BYTES_PER_FLOAT,
                    colors, GLES30.GL_STATIC_DRAW);

            mProgram = GLES30.glCreateProgram();
            createShaders(mProgram);

            // get handle to vertex shader's vPosition member
            positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
            if (positionHandle == -1)
            {
                throw new RuntimeException("vPosition attribute location invalid");
            }

            // get handle to fragment shader's vColor member
            colorHandle = GLES30.glGetAttribLocation(mProgram, "av3colour");
            if (colorHandle == -1)
            {
                throw new RuntimeException("av3colour attribute location invalid");
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
            // Add program to OpenGL environment
            GLES30.glUseProgram(mProgram);

            // Bind the position and indices buffers
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, positionBuffer[0]);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);

            GLES30.glEnableVertexAttribArray(positionHandle);
            GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, 0);

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, colorbuffer[0]);
            GLES30.glEnableVertexAttribArray(colorHandle);
            GLES30.glVertexAttribPointer(colorHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, 0);

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

