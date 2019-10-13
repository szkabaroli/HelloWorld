package engine.renderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;

import org.lwjgl.opengl.GLDebugMessageCallback;

import engine.core.Entity;
import engine.core.FlyCamera;
import engine.loaders.OBJLoader;
import engine.meshes.Mesh;
import engine.textures.Texture2D;
import math.Vector3f;

public class GameLoop implements IGameLoop {


    private Material mMaterial;
    private Mesh mMesh;
    private Entity e;

    public long window;
    private FlyCamera mFlyCamera;
    private int currentEnvironment = 0;

    private MasterRenderer mMasterRenderer;

    @Override
    public void onPrep() {
        glEnable(GL_DEBUG_OUTPUT);
        glDisable(GL_FRAMEBUFFER_SRGB);
        glDebugMessageCallback(new GLDebugMessageCallback() {
            @Override
            public void invoke(int i, int i1, int i2, int i3, int i4, long l, long l1) {
                System.out.println(i);
            }
        },0);



        mMesh = new Mesh("src/engine/dragon.obj");
        mMaterial = new Material(new Texture2D("src/engine/gold-scuffed_basecolor-boosted.png", false),
                                 new Texture2D("src/engine/gold-scuffed_metallic.png", false),
                                 new Texture2D("src/engine/gold-scuffed_roughness.png", false),
                                 new Texture2D("src/engine/gold-scuffed_normal.png", false));

        e = new Entity(mMesh, mMaterial, new Vector3f(0, -30, -30), new Vector3f(0, 0, 0), new Vector3f(100f, 100f, 100f));

        mFlyCamera = new FlyCamera(window);

        mMasterRenderer = new MasterRenderer();
        mMasterRenderer.renderOnce(0);
    }

    @Override
    public void onKey(int key, int action) {
        if(key == GLFW_KEY_C && action == GLFW_PRESS) {

            if(currentEnvironment < 2) {
                currentEnvironment++;
            }
            else {
                currentEnvironment = 0;
            }
            mMasterRenderer.renderOnce(currentEnvironment);
        }

    }

    @Override
    public void onUpdate() {
        mMasterRenderer.addEntity(e);

        mMasterRenderer.prepare();
        mMasterRenderer.render(mFlyCamera);
        mFlyCamera.move();
    }

    @Override
    public void onExit() {
        mMasterRenderer.clean();
        mMesh.clean();
    }
}
