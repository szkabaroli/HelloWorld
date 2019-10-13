package engine.core;

import math.Vector3f;

public class TransformComponent implements IComponent {

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    TransformComponent(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
}
