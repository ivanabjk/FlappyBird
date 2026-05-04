import java.awt.*;

public class Pipe {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image image;
    public boolean passed;
    public Pipe(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        passed = false;
    }

}
