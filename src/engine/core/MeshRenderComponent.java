package engine.core;

import engine.meshes.Mesh;
import engine.renderer.Material;
import engine.renderer.Shader;

public class MeshRenderComponent implements IComponent {

    private Shader shader;
    public Mesh mesh;
    public Material material;

    public MeshRenderComponent(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }
}
