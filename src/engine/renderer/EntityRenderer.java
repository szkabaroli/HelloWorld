package engine.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import engine.core.Entity;
import engine.core.ICamera;
import engine.core.MeshRenderComponent;
import engine.meshes.Mesh;
import math.Matrix4f;
import math.Util;

public class EntityRenderer {

    private Shader shader;

    public EntityRenderer() {

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);

        shader = new Shader("src/engine/shader/pbr.vsh","src/engine/shader/pbr.fsh");

        shader.start();
        shader.loadInt("environmentMap", 0);
        shader.loadInt("irradianceMap", 1);
        shader.loadInt("brdfSample", 2);
        shader.stop();
    }

    public void render(Map<MeshRenderComponent, List<Entity>> entities, ICamera camera, int prefilteredMap, int irradianceMap, int brdfFLUT) {
        shader.start();
        shader.loadMatrix("projectionView", camera.getProjectionViewMatrix());

        shader.loadInt("prefilteredMap", 0);
        shader.loadInt("irradianceMap", 1);
        shader.loadInt("brdfSample", 2);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, prefilteredMap);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, irradianceMap);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, brdfFLUT);


        for(MeshRenderComponent render:entities.keySet()) {

            bindMeshVao(render.mesh);
            render.material.shader = shader;
            render.material.bindTextures();

            List<Entity> batch = entities.get(render);
            for(Entity instance:batch) {
                prepareInstance(instance, camera);
                glDrawElements(GL_TRIANGLES, render.mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            }

            unbindMeshVao();
        }

        shader.stop();
    }

    private void bindMeshVao(Mesh mesh) {
        glBindVertexArray(mesh.getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }


    private void unbindMeshVao() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity, ICamera camera) {
        Matrix4f modelMatrix = Util.createModelMatrix(entity.transform.getPosition(), entity.transform.getRotation(), entity.transform.getScale());

        shader.loadMatrix("model", modelMatrix);
        shader.loadVector("camPosition", camera.getPosition());
    }

    public void clean() {
        shader.clean();
    }
}
