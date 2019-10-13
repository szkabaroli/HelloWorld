package engine.core;

import math.Matrix4f;
import math.Vector3f;

public interface ICamera {
    Matrix4f getProjectionViewMatrix();
    float getPitch();
    float getYaw();
    Vector3f getPosition();
}
