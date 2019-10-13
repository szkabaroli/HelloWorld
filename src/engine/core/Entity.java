package engine.core;

import java.util.ArrayList;
import java.util.List;

import engine.meshes.Mesh;
import engine.renderer.Material;
import math.Vector3f;

public class Entity {

    private List<IComponent> components = new ArrayList<>();
    public MeshRenderComponent renderer;
    public TransformComponent transform;

    public Entity(Mesh mesh, Material material, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.transform = new TransformComponent(position, rotation, scale);
        this.renderer = new MeshRenderComponent(mesh, material);
    }

    public void addComponent(IComponent component) {
        components.add(component);
    }

    public Object getComponent(IComponent type) {
        for(IComponent component:components) {
            if(IComponent.class == component.getClass()) {
                return component;
            }
        }
        return null;
    }
}
