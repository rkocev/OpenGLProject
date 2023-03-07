package First.AirHockey1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetActiveAttrib;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_LINES;
import static javax.microedition.khronos.opengles.GL10.GL_POINTS;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import First.AirHockey1.utils.LoggerConfig;
import First.AirHockey1.utils.ShaderHelper;
import First.AirHockey1.utils.TextResourceReader;
import First.OpenGLProject.R;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private final Context context;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private int uColorLocation;
    private int aPositionLocation;
    private int uSize;

    public AirHockeyRenderer(Context context){
        this.context = context;

        float[] tableVerticesWithTriangles = {
                // Triangle 1
                -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,

                // Triangle 2
                -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,

                // Line 1
                -0.5f, 0f, 0.5f, 0f,

                // Mallets
                0f, -0.25f, 0f, 0.25f,

                // Puck
                0.0f, 0.0f,

                // Bottom Line Border
                -0.5f, -0.5f, 0.5f, -0.5f,

                // Bottom Line Border
                -0.5f, 0.5f, 0.5f, 0.5f,

                // Left Line Border
                -0.5f, 0.5f, -0.5f, -0.5f,

                // Right Line Border
                0.5f, 0.5f, 0.5f, -0.5f

        };

        vertexData = ByteBuffer.allocateDirect(
                tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(
                context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(
                context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if(LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, "u_Color");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        uSize = glGetUniformLocation(program,"u_Size");

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Set the OpenGl viewport to fill the entire surface.
        glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        //glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Triangles
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // Line 1
        glLineWidth(5f);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_LINES, 6, 2);

        // Draw the first mallet blue.
        glUniform1f(uSize, 30.0f);
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        // Draw the second malled red.
        glUniform1f(uSize, 30.0f);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);



        // Red puck in the center
        glUniform1f(uSize, 15.0f);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 10, 1);


        // Bottom Border Line
        glLineWidth(10f);
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_LINES, 11, 2);

        // Top Border Line
        glLineWidth(10f);
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_LINES, 13, 2);

        // Left Border Line
        glLineWidth(10f);
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_LINES, 15, 2);

        // Right Border Line
        glLineWidth(10f);
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_LINES, 17, 2);
    }


}