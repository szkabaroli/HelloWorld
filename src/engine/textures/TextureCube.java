package engine.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;

public class TextureCube implements ITexture{

    private int textureId;

    public TextureCube(String[] imagePaths) {

        //Generate texture and bind it
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);


        for (int i = 0; i < imagePaths.length; i++) {
            //Load png image from a given paths
            BufferedImage data = TextureLoader.loadImageFile(imagePaths[i]);

            //Send textures data to OpenGL
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, TextureLoader.getByteBufferFromImage(data));
        }

        //Generate mip maps
        glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //Unbind texture
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    }

    public void clean() {
        glDeleteTextures(textureId);
    }

    public int getTextureId() {
        return textureId;
    }
}
