import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // background image is 360x640px

        int boardWidth = 360;
        int boardHeight = 640;

        // create window
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); // sets the window in the center
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // adding canvas(JPanel) to the frame
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack(); // resizes the window by the components preferred size so everything fits properly
        flappyBird.requestFocus();
        frame.setVisible(true);

    }
}