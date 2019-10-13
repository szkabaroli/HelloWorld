package engine.core;

import math.Matrix4f;
import math.Util;
import math.Vector3f;

public class CubeCamera implements ICamera{

    private Vector3f position = new Vector3f(0.0f,10.0f,10.0f);

    private float pitch = 30.0f;
    private float yaw = 0;

    private static final float FOV = 90;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 200000;

    private int SIZE;


    public CubeCamera(int size) {
        this.SIZE = size;
    }

    public void switchToFace(int faceIndex) {
        switch (faceIndex) {
            case 0:
                pitch = 0;
                yaw = 90;
                break;
            case 1:
                pitch = 0;
                yaw = -90;
                break;
            case 2:
                pitch = -90;
                yaw = 180;
                break;
            case 3:
                pitch = 90;
                yaw = 180;
                break;
            case 4:
                pitch = 0;
                yaw = 180;
                break;
            case 5:
                pitch = 0;
                yaw = 0;
                break;
        }
    }

    public Matrix4f getProjectionViewMatrix() {
        Matrix4f viewMatrix = Util.createFlippedViewMatrix(this);
        Matrix4f projectionMatrix = Util.createProjectionMatrix(SIZE, SIZE, FOV, NEAR_PLANE, FAR_PLANE);
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
