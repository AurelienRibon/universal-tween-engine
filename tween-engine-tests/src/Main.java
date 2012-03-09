
import aurelienribon.gdxtests.App;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class Main {
    public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.resizable = false;
		cfg.vSyncEnabled = true;
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		cfg.title = "Tween-Engine tests";
		new LwjglApplication(new App(), cfg);
	}
}