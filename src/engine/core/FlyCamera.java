package engine.core;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.DoubleBuffer;

import org.lwjgl.system.MemoryStack;

import math.Matrix4f;
import math.Util;
import math.Vector3f;

public class FlyCamera implements ICamera{

    private Vector3f position = new Vector3f(0.0f,0.0f,0.0f);

    private float pitch = 30.0f;
    private float yaw = 0;

    private double lastX;
    private double lastY;

    private double diffX;
    private double diffY;

    private long window;

    private static final float FOV = 80;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 200000;
    private final float SENSITIVY = 0.2f;
    private final float SPEED = 0.6f;
    //endregion

    public FlyCamera(long window) {
        this.window = window;
        float[] pos = getMousePos();
        lastX = pos[0];
        lastY = pos[1];
    }

    public void move() {
        getdiff();
        calculateRotation();
        moveCamera();
    }

    private void moveCamera() {
        float yrot = yaw / 180 * (float) Math.PI;
        float xrot = pitch / 180 * (float) Math.PI;

        if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS){
            position.x += (float) Math.sin(yrot) * SPEED;
            position.z -= (float) Math.cos(yrot) * SPEED;
            position.y -= (float) Math.sin(xrot) * SPEED;
        }

        if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS){
            position.x -= (float) Math.sin(yrot)* SPEED;
            position.z += (float) Math.cos(yrot)* SPEED;
            position.y += (float) Math.sin(xrot)* SPEED;
        }

        if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS){
            position.x += (float)Math.cos(yrot) * SPEED;
            position.z += (float)Math.sin(yrot) * SPEED;
        }

        if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS){
            position.x -= (float)Math.cos(yrot) * SPEED;
            position.z -= (float)Math.sin(yrot) * SPEED;
        }
    }

    private void calculateRotation(){
        float pitchChange = (float) diffY * SENSITIVY;
        pitch -= pitchChange;
        float yawChange = (float) diffX * SENSITIVY;
        yaw -= yawChange;

    }


    private void getdiff() {
        float[] pos = getMousePos();
        diffX = lastX - pos[0];
        diffY = lastY - pos[1];
        lastX = pos[0];
        lastY = pos[1];
    }

    private float[] getMousePos() {
        try (MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer xpos = stack.callocDouble(1);
            DoubleBuffer ypos = stack.callocDouble(1);

            glfwGetCursorPos(window, xpos, ypos);

            float x = (float) xpos.get(0);
            float y = (float) ypos.get(0);

            return new float[] {x, y};
        }
    }

    public Matrix4f getProjectionViewMatrix() {
        Matrix4f viewMatrix = Util.createViewMatrix(this);
        Matrix4f projectionMatrix = Util.createProjectionMatrix(1280, 720, FOV, NEAR_PLANE, FAR_PLANE);
        return Matrix4f.mul(projectionMatrix, viewMatrix, null);
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public Vector3f getPosition() {
        return position;
    }
}
