package engine.meshes;

import engine.renderer.Material;

public class TexturedMesh {
    private Mesh mesh;
    private Material material;

    public TexturedMesh(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Material getMaterial() {
        return material;
    }
}
