package engine.core;

import math.Matrix4f;
import math.Util;
import math.Vector3f;

public class FboCamera implements ICamera{

    private Vector3f position = new Vector3f(0.0f,10.0f,10.0f);

    private float pitch = 30.0f;
    private float yaw = 0;

    private static final float FOV = 80;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 20000;

    public FboCamera() {

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
