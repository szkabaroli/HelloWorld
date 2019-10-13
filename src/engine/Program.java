package engine;

import engine.renderer.GameLoop;
import engine.renderer.Window;

public class Program {
    public static void main(String[] args){
        GameLoop gl = new GameLoop();
        new Window(720, 1280, gl).run();
    }
}


