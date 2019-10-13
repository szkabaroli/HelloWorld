package engine.renderer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryStack;

import math.Matrix4f;
import math.Vector3f;

public class Shader {

    private int programId;
    private int vshId;
    private int fshId;

    private final Map<String, Integer> uniforms = new HashMap<>();

    public Shader(String vshPath, String fshPath) {
        vshId = loadShader(vshPath, GL_VERTEX_SHADER);
        fshId = loadShader(fshPath, GL_FRAGMENT_SHADER);

        programId = glCreateProgram();

        glAttachShader(programId, vshId);
        glAttachShader(programId, fshId);

        glLinkProgram(programId);

        if(glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            String programLog = glGetProgramInfoLog(programId);
            System.out.println(programLog);
        }
    }

    public void start() {
        glUseProgram(programId);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void clean() {
        stop();
        glDetachShader(programId, vshId);
        glDetachShader(programId, fshId);
        glDeleteShader(vshId);
        glDeleteShader(fshId);
        glDeleteProgram(programId);
    }

    public void loadInt(String uniformName, int value){
        int location = glGetUniformLocation(programId, uniformName);
        glUniform1i(location, value);
    }

    public void loadFloat(String uniformName, float value) {
        int location = glGetUniformLocation(programId, uniformName);
        glUniform1f(location, value);
    }

    public void loadVector(String uniformName, Vector3f vector) {
        int location = glGetUniformLocation(programId, uniformName);
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    public void loadMatrix(String uniformName, Matrix4f matrix) {
        int location = glGetUniformLocation(programId, uniformName);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.callocFloat(16);
            matrix.store(buffer);
            buffer.flip();
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    private String loadShaderFile(String sourcePath) {
        StringBuilder sourceString = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(sourcePath));
            String line;
            while((line = reader.readLine()) != null) {

                sourceString.append(line).append('\n');
            }
        } catch (IOException e) {
            System.err.println("Could not load shader.");
            e.printStackTrace();
            System.exit(-1);
        }
        return sourceString.toString();
    }

    public int loadShader(String sourcePath, int type) {
        int shaderId = glCreateShader(type);
        String shaderSource = loadShaderFile(sourcePath);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);


        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(shaderId, 500));
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }


        return shaderId;
    }

    public void getInfo() {
        glGetProgramInfoLog(programId, glGetProgrami(programId, GL_INFO_LOG_LENGTH));
    }
}
