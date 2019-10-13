package engine.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import engine.core.ICamera;
import engine.meshes.CubeMap;
import engine.meshes.Mesh;

public class SkyRenderer {

    private Shader shader;

    public SkyRenderer() {

        shader = new Shader("src/engine/shader/sky.vsh","src/engine/shader/sky.fsh");

    }

    public void render(CubeMap cubeMap, ICamera camera, int texture) {
        shader.start();
        shader.loadMatrix("projectionView", camera.getProjectionViewMatrix());
        bindCubeVao(cubeMap.getCube());
        bindTexture(texture);
        glDrawArrays(GL_TRIANGLES, 0, cubeMap.getCube().getVertexCount());
        unbindCubeVao();
        shader.stop();
    }

    private void bindCubeVao(Mesh cube){
        glBindVertexArray(cube.getVaoId());
        glEnableVertexAttribArray(0);
    }

    private void unbindCubeVao(){
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    private void bindTexture(int texture){
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
    }
}
