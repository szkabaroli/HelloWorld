package engine.renderer;

public interface IGameLoop {
    void onKey(int key, int action);
    void onPrep();
    void onUpdate();
    void onExit();
}
