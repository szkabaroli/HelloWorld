package math;

import engine.core.ICamera;

public class Util {

    public static Matrix4f createModelMatrix(Vector3f translate, Vector3f rotate, Vector3f scale) {

        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translate, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotate.x), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotate.y), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotate.z), new Vector3f(0,0,1), matrix, matrix);
        Matrix4f.scale(scale, matrix, matrix);

        return matrix;
    }

    public static Matrix4f createProjectionMatrix(float width, float height, float FOV, float NEAR_PLANE, float FAR_PLANE) {

        float aspectRatio = width / height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;

        return projectionMatrix;
    }

    public static Matrix4f createViewMatrix(ICamera camera) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), matrix, matrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f invertedCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f.translate(invertedCameraPos, matrix, matrix);
        return matrix;
    }
    public static Matrix4f createFlippedViewMatrix(ICamera camera) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(180), new Vector3f(0,0,1), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), matrix, matrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f invertedCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f.translate(invertedCameraPos, matrix, matrix);
        return matrix;
    }
}
