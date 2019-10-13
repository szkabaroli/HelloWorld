package engine.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.glTexStorage2D;

import org.lwjgl.opengl.GL30;

import engine.core.CubeCamera;
import engine.meshes.CubeMap;
import engine.meshes.Mesh;
import engine.textures.TextureCube;

public class FilteredMapRenderer {

    private Shader shader;
    private CubeCamera captureCamera;

    private int numMips = 7;


    FilteredMapRenderer() {
        captureCamera = new CubeCamera(256);

        shader = new Shader("src/engine/shader/prefilter.vsh","src/engine/shader/prefilter.fsh");
    }

    public int render(CubeMap envMap) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(.0f, .0f, .0f, .0f);

        int size = 256;

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
        glTexStorage2D(GL_TEXTURE_CUBE_MAP, numMips, GL_RGBA8, size, size);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, numMips);
        glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

        int fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        bindCubeVao(envMap.getCube());
        bindTexture(envMap.getTexture());

        shader.start();
        for(int mip = 0; mip < numMips; mip++) {

            glViewport(0, 0, size, size);
            size = size/2;

            float roughness = (float)mip / (float)(numMips - 1);


            for (int face = 0; face < 6; face++) {
                captureCamera.switchToFace(face);
                shader.loadFloat("roughness", roughness);
                shader.loadMatrix("projectionView", captureCamera.getProjectionViewMatrix());
                glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, textureId, mip);

                glDrawArrays(GL_TRIANGLES, 0, envMap.getCube().getVertexCount());
            }
        }
        shader.stop();

        unbindCubeVao();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteFramebuffers(fbo);

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        glViewport(0,0,1280,720);

        return textureId;
    }

    private void bindCubeVao(Mesh cube){
        glBindVertexArray(cube.getVaoId());
        glEnableVertexAttribArray(0);
    }

    private void unbindCubeVao(){
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    private void bindTexture(TextureCube texture){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture.getTextureId());
    }

}

