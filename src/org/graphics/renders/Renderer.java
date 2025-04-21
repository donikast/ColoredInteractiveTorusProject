package org.graphics.renders;

import static org.lwjgl.opengl.GL46.*;

import org.graphics.utils.InputAction;
import org.graphics.utils.ShaderProgram;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Renderer {
    private int vaoID, vboID;
    private ShaderProgram shaderProgram;
    private float angleX=0f;
    private float angleY=0f;

    public void init() {
        glEnable(GL_DEPTH_TEST);

        shaderProgram = new ShaderProgram("res/shaders/vertexShader.vert", "res/shaders/fragmentShader.frag");

        float[] vertices = {
                // предна страна
                -0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,

                // задна страна
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 0.0f,

                // вляво
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 1.0f,

                // вдясно
                0.5f, -0.5f, -0.5f,  1.0f, 1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f, 0.0f,

                // горе
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 1.0f,

                // долу
                -0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 1.0f
        };

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Атрибут 0 - позиция
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Атрибут 1 - цвят
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);


        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderProgram.use();
        glBindVertexArray(vaoID);

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix
                .rotateY((float) Math.toRadians(angleY))
                .rotateX((float) Math.toRadians(angleX));

        shaderProgram.setUniform("modelMatrix", modelMatrix);

        Matrix4f viewMatrix = new Matrix4f().lookAt(0.0f, 0.0f, 3.0f,  0.0f, 0.0f, 0.0f,  0.0f, 1f, 0.0f);
        Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(45.0f), 1.0f, 0.1f, 100f);
        shaderProgram.setUniform("viewMatrix", viewMatrix);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);
    }

    public void handleInputAction(InputAction action) {
        switch (action) {
            case RIGHT -> setAngleY(angleY + 1f);
            case LEFT -> setAngleY(angleY - 1f);
            case UP -> setAngleX(angleX + 1f);
            case DOWN -> setAngleX(angleX - 1f);
        }
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
    }
}