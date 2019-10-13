package engine.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.HashMap;
import java.util.Map;

import engine.textures.ITexture;
import engine.textures.Texture2D;
import engine.textures.TextureCube;

public class Material {

    public Shader shader;
    private Map<Integer, ITexture> textures = new HashMap<>();
    private Map<Integer, String> names = new HashMap<>();

    public Material(ITexture albedo, ITexture metalic, ITexture roughness, ITexture normal) {
        addTexture(3,"albedoSample", albedo);
        addTexture(4,"metalnessSample", metalic);
        addTexture(5,"roughnessSample", roughness);
        addTexture(6,"normalSample", normal);
    }

    public void bindTextures() {

        //load uniforms for textures
        for(Integer index:names.keySet()) {
            shader.loadInt(names.get(index), index);
        }

        //load actual textures
        for(Integer index:textures.keySet()) {
            glActiveTexture(GL_TEXTURE0 + index);
            ITexture texture = textures.get(index);
            if(texture.getClass() == Texture2D.class) {
                glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
            } else if (texture.getClass() == TextureCube.class) {
                glBindTexture(GL_TEXTURE_CUBE_MAP, texture.getTextureId());
            }
        }
    }

    private void addTexture(int index, String name, ITexture texture) {
        names.put(index, name);
        textures.put(index, texture);
    }
}
