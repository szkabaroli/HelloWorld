package engine.renderer;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.core.Entity;
import engine.core.ICamera;
import engine.core.MeshRenderComponent;
import engine.meshes.CubeMap;
import engine.textures.Texture2D;
import engine.textures.TextureCube;

public class MasterRenderer {

    //Renderers
    private EntityRenderer entityRenderer;
    private SkyRenderer skyRenderer;
    private FilteredMapRenderer filterEnvRenderer;
    private IrradianceMapRenderer irradianceMapRenderer;

    private static final String[] ENVIROMENT_MAP_INSIDE = {"src/engine/lposx.png", "src/engine/lnegx.png", "src/engine/lposy.png", "src/engine/lnegy.png", "src/engine/lposz.png", "src/engine/lnegz.png"};
    private static final String[] ENVIROMENT_MAP_OUTSIDE = {"src/engine/cposx.png", "src/engine/cnegx.png", "src/engine/cposy.png", "src/engine/cnegy.png", "src/engine/cposz.png", "src/engine/cnegz.png"};
    private static final String[] ENVIROMENT_MAP_NIGTH = {"src/engine/yposx.png", "src/engine/ynegx.png", "src/engine/yposy.png", "src/engine/ynegy.png", "src/engine/yposz.png", "src/engine/ynegz.png"};

    private CubeMap enviromentMap;
    private CubeMap enviromentMap1;
    private CubeMap enviromentMap2;

    private CubeMap currentMap;

    private int currentPrefilteredMap;
    private int currentIrradianceMap;

    private int prefilteredMap1;
    private int irradianceMap1;

    private int prefilteredMap2;
    private int irradianceMap2;

    private int prefilteredMap3;
    private int irradianceMap3;


    private int brdfFLUT;

    private Map<MeshRenderComponent, List<Entity>> entities = new HashMap<>();

    MasterRenderer() {

        TextureCube texture = new TextureCube(ENVIROMENT_MAP_NIGTH);
        TextureCube texture1 = new TextureCube(ENVIROMENT_MAP_OUTSIDE);
        TextureCube texture2 = new TextureCube(ENVIROMENT_MAP_INSIDE);

        enviromentMap = new CubeMap(texture1);
        enviromentMap1 = new CubeMap(texture);
        enviromentMap2 = new CubeMap(texture2);

        currentMap = enviromentMap;

        entityRenderer = new EntityRenderer();
        skyRenderer = new SkyRenderer();
        filterEnvRenderer = new FilteredMapRenderer();
        irradianceMapRenderer = new IrradianceMapRenderer();

        prefilteredMap1 = filterEnvRenderer.render(enviromentMap);
        irradianceMap1 = irradianceMapRenderer.render(enviromentMap);

        prefilteredMap2 = filterEnvRenderer.render(enviromentMap1);
        irradianceMap2 = irradianceMapRenderer.render(enviromentMap1);

        prefilteredMap3 = filterEnvRenderer.render(enviromentMap2);
        irradianceMap3 = irradianceMapRenderer.render(enviromentMap2);

        brdfFLUT = new Texture2D("src/engine/brdf.png", false).getTextureId();
    }

    public void prepare() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(.0f, .0f, .0f, .0f);
    }

    public void renderOnce(int current) {
        switch (current) {
            case 0:
                currentMap = enviromentMap;
                currentPrefilteredMap = prefilteredMap1;
                currentIrradianceMap = prefilteredMap1;
                break;
            case 1:
                currentMap = enviromentMap1;
                currentPrefilteredMap = prefilteredMap2;
                currentIrradianceMap = prefilteredMap2;
                break;
            case 2:
                currentMap = enviromentMap2;
                currentPrefilteredMap = prefilteredMap3;
                currentIrradianceMap = prefilteredMap3;

                break;
        }
    }

    public void render(ICamera camera) {
        System.out.println(currentPrefilteredMap);
        entityRenderer.render(entities, camera, currentPrefilteredMap, currentIrradianceMap, brdfFLUT);
        skyRenderer.render(currentMap, camera, currentMap.getTexture().getTextureId());
        entities.clear();
    }

    public void addEntity(Entity entity) {
        MeshRenderComponent mesh = entity.renderer;
        List<Entity> batch = entities.get(mesh);
        if(batch!=null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(mesh, newBatch);
        }
    }

    public void clean() {
        entityRenderer.clean();
    }
}
