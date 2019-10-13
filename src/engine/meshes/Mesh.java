package engine.meshes;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

public class Mesh {

    private int vaoId;
    private List<Integer> vboIds = new ArrayList<>();
    private int vertexCount;

    public Mesh(float[] positions,int[] indices, float[] textureCoords, float[] normals) {

        vertexCount = indices.length;
        vaoId = createVao();

        storeIndicesInAttributeList(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);

        unbindVao();
    }

    public Mesh(String filePath) {

        try {
            FileInputStream f = new FileInputStream(filePath);
            Obj obj = ObjUtils.convertToRenderable(ObjReader.read(f));

            IntBuffer indices = ObjData.getFaceVertexIndices(obj);
            FloatBuffer vertices = ObjData.getVertices(obj);
            FloatBuffer uvs = ObjData.getTexCoords(obj, 2);
            FloatBuffer normals = ObjData.getNormals(obj);

            vertexCount = indices.limit();
            vaoId = createVao();

            storeIndicesInAttributeList(indices);
            storeDataInAttributeList(0, 3, vertices);
            storeDataInAttributeList(1, 2, uvs);
            storeDataInAttributeList(2, 3, normals);

            unbindVao();

        } catch (IOException e) {

        }
    }

    public Mesh(float[] positions) {
        vertexCount = positions.length/3;
        vaoId = createVao();

        storeDataInAttributeList(0, 3, positions);

        unbindVao();
    }

    private void unbindVao() {
        glBindVertexArray(0);
    }

    private int createVao() {
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        return vaoId;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void storeIndicesInAttributeList(int[] data) {
        int vboId = glGenBuffers();
        vboIds.add(vboId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer buffer = storeIntData(data);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private void storeIndicesInAttributeList(IntBuffer data) {
        int vboId = glGenBuffers();
        vboIds.add(vboId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }

    private void storeDataInAttributeList(int attributeIndex, int size, float[] data) {
        int vboId = glGenBuffers();
        vboIds.add(vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer buffer = storeFloatData(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeIndex, size, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeDataInAttributeList(int attributeIndex, int size, FloatBuffer data) {
        int vboId = glGenBuffers();
        vboIds.add(vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeIndex, size, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private FloatBuffer storeFloatData(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private IntBuffer storeIntData(int[] data) {
            IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
            buffer.put(data);
            buffer.flip();
            return buffer;
    }

    public void clean() {
        for(int vbo:vboIds) {
            glDeleteBuffers(vbo);
        }
        glDeleteVertexArrays(vaoId);
    }
}