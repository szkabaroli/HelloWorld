package engine.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL21.GL_SRGB;

import java.awt.image.BufferedImage;

public class Texture2D implements ITexture {

    private int textureId;

    public Texture2D(String imagePath, boolean srgb) {

        //Generate texture and bind it
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        //Load png image from a given path
        BufferedImage image = TextureLoader.loadImageFile(imagePath);

        //Send texture data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, srgb ? GL_SRGB : GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, TextureLoader.getByteBufferFromImage(image));

        //Generate mip map-s
        //glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //Unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void clean() {
        glDeleteTextures(textureId);
    }

    public int getTextureId() {
        return textureId;
    }


}
