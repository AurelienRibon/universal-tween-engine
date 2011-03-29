import com.badlogic.gdx.backends.jogl.JoglApplication;

public class Main {
    public static void main(String[] args) {
        new JoglApplication(new App(), "", 500, 500, false);
    }
}
