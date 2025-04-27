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
    private float angleX = 0f;
    private float angleY = 0f;
    private int torusVertexCount;

    public void init() {
        glEnable(GL_DEPTH_TEST);

        shaderProgram = new ShaderProgram("res/shaders/vertexShader.vert", "res/shaders/fragmentShader.frag");


        int numMajor = 32;  // Разделения по големия кръг
        int numMinor = 16;  // Разделения по малкия кръг
        float majorRadius = 0.7f; // Радиус на големия кръг
        float minorRadius = 0.3f; // Радиус на сечението

        float[] vertices = generateTorusVertices(majorRadius, minorRadius, numMajor, numMinor);
        torusVertexCount = vertices.length / 6;

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

        Matrix4f modelMatrix = new Matrix4f()
                .rotateY((float) Math.toRadians(angleY))
                .rotateX((float) Math.toRadians(angleX));

        shaderProgram.setUniform("modelMatrix", modelMatrix);

        Matrix4f viewMatrix = new Matrix4f().lookAt(
                0.0f, 0.0f, 3.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1f, 0.0f
        );
        Matrix4f projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(45.0f), 1.0f, 0.1f, 100f
        );
        shaderProgram.setUniform("viewMatrix", viewMatrix);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        glDrawArrays(GL_TRIANGLES, 0, torusVertexCount);
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

    private float[] generateTorusVertices(float majorRadius, float minorRadius, int numMajor, int numMinor) {
        float[] vertices = new float[numMajor * numMinor * 6 * 6];
        int index = 0;

        for (int i = 0; i < numMajor; i++) {
            float theta = (float) (2.0 * Math.PI * i / numMajor);
            float nextTheta = (float) (2.0 * Math.PI * (i + 1) / numMajor);

            for (int j = 0; j < numMinor; j++) {
                float phi = (float) (2.0 * Math.PI * j / numMinor);
                float nextPhi = (float) (2.0 * Math.PI * (j + 1) / numMinor);

                float[] v0 = getTorusVertex(majorRadius, minorRadius, theta, phi);
                float[] v1 = getTorusVertex(majorRadius, minorRadius, nextTheta, phi);
                float[] v2 = getTorusVertex(majorRadius, minorRadius, nextTheta, nextPhi);
                float[] v3 = getTorusVertex(majorRadius, minorRadius, theta, nextPhi);

                // Първи триъгълник
                index = addVertex(vertices, index, v0);
                index = addVertex(vertices, index, v1);
                index = addVertex(vertices, index, v2);

                // Втори триъгълник
                index = addVertex(vertices, index, v0);
                index = addVertex(vertices, index, v2);
                index = addVertex(vertices, index, v3);
            }
        }
        return vertices;
    }

    private float[] getTorusVertex(float majorRadius, float minorRadius, float theta, float phi) {
        float cosTheta = (float) Math.cos(theta);
        float sinTheta = (float) Math.sin(theta);
        float cosPhi = (float) Math.cos(phi);
        float sinPhi = (float) Math.sin(phi);

        float x = (majorRadius + minorRadius * cosPhi) * cosTheta;
        float y = (majorRadius + minorRadius * cosPhi) * sinTheta;
        float z = minorRadius * sinPhi;

        return new float[]{x, y, z};
    }

    private int addVertex(float[] vertices, int index, float[] pos) {
        vertices[index++] = pos[0];
        vertices[index++] = pos[1];
        vertices[index++] = pos[2];

        // Стойности на цветовете
        vertices[index++] = 1.0f;
        vertices[index++] = 0.0f;
        vertices[index++] = 0.5f;

        return index;
    }



    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
    }
}
