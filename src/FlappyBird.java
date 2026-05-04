import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;

// creating a canvas
public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgroundImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image getReadyImg;
    Image gameOverImg;
    Image playButtonImg;
    Image bird1Img;
    Image bird2Img;
    Image bird3Img;

    // bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    int birdFrameIndex = 0;

    // pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;

    // game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;

    Timer gameLoopTimer;
    Timer placePipeTimer;
    Timer birdAnimationTimer;

    double score = 0;

    GameState gameState = GameState.START;

    Font gameFont;

    // play button bounds
    int btnX = boardWidth / 2 - 50;
    int btnY = boardHeight / 2;
    int btnWidth = 100;
    int btnHeight = 50;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true); // allows the panel to listen to the keyboard
        addKeyListener(this); // to react when keys are pressed

        // load images
        backgroundImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/flappybirdbg.png"))).getImage();
        bird1Img = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/bird1.png"))).getImage();
        bird2Img = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/bird2.png"))).getImage();
        bird3Img = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/bird3.png"))).getImage();
        topPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/toppipe.png"))).getImage();
        bottomPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/bottompipe.png"))).getImage();
        getReadyImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/getready.png"))).getImage();
        gameOverImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/gameover.png"))).getImage();
        playButtonImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./images/playbutton.png"))).getImage();

        // get font

        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT,
                    Objects.requireNonNull(getClass().getResourceAsStream("./fonts/bit5x3.ttf")));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // set a size you want to use
            gameFont = customFont.deriveFont(Font.PLAIN, 48f);

        } catch (Exception e) {
            e.printStackTrace();
            gameFont = new Font("Arial", Font.PLAIN, 32); // fallback
        }


        // bird
        bird = new Bird(birdX, birdY, birdWidth, birdHeight, bird1Img);
        Image[] birdFrames = {bird1Img, bird2Img, bird3Img, bird2Img};

        birdAnimationTimer = new Timer(150, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                birdFrameIndex = (birdFrameIndex + 1) % birdFrames.length;
                bird.image = birdFrames[birdFrameIndex];
            }
        });
        birdAnimationTimer.start();

        pipes = new ArrayList<>();

        // place pipes timer
        placePipeTimer = new Timer(1500, e -> placePipes());

        // game timer
        gameLoopTimer = new Timer(1000 / 60, this); // 1000/60 = 16.6; 60 frames per second

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                boolean clickedPlayButton = mouseX >= btnX && mouseX <= btnX + btnWidth &&
                        mouseY >= btnY && mouseY <= btnY + btnHeight;

                pressedAction(clickedPlayButton);
            }
        });

    }

    public void placePipes() {
        // (0-1) * pipeHeight/2 -> (0-256)
        //128
        //0 - 128 - (0-256) --> 1/4 pipeHeight -> 3/4 pipeHeight

        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(pipeX, pipeY, pipeWidth, pipeHeight, topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(pipeX, pipeY, pipeWidth, pipeHeight, bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score

        if (gameState == GameState.START) {
            drawScreen(g, getReadyImg);
        } else if (gameState == GameState.GAME_OVER) {
            drawScreen(g, gameOverImg);
        }
        drawBorderedText((Graphics2D) g, String.valueOf((int) score), 10, 35, gameFont);
    }

    public void drawScreen(Graphics g, Image gameStateImage) {
        g.drawImage(gameStateImage, boardWidth / 2 - 100, boardHeight / 4, 200, 50, null);
        g.drawImage(playButtonImg, btnX, btnY, btnWidth, btnHeight, null);

        // Instruction text under the button
        g.setFont(gameFont.deriveFont(Font.PLAIN, 18f)); // smaller size
        g.setColor(Color.BLACK);
        g.drawString("Press the button or space to start", boardWidth / 2 - 140, boardHeight / 2 + 80);
    }

    public void drawBorderedText(Graphics2D g, String text, int x, int y, Font font) {
        g.setFont(font);

        // border
        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, y);
        g.drawString(text, x, y + 2);
        g.drawString(text, x - 2, y);
        g.drawString(text, x, y - 2);

        // main text
        g.setColor(Color.WHITE); // flappy orange
        g.drawString(text, x, y);
    }


    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // 0.5 for each passed pipe, 0.5*2=1 for a set of passed pipes
            }
            if (collision(bird, pipe)) {
                gameState = GameState.GAME_OVER;
            }
        }

        if (bird.y > boardHeight) {
            gameState = GameState.GAME_OVER;
        }
    }

    public boolean collision(Bird bird, Pipe pipe) {
        return bird.x < pipe.x + pipe.width &&   // bird's top left corner doesn't reach pipe's top right corner
                bird.x + bird.width > pipe.x &&  // bird's top right corner passes pipe's top left corner
                bird.y < pipe.y + pipe.height && // bird's top left corner doesn't reach pipe's bottom left corner
                bird.y + bird.height > pipe.y;   // bird's bottom left corner passes pipe's top left corner
    }

    public void resetGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameState = GameState.PLAYING;
        gameLoopTimer.start();
        placePipeTimer.start();
        birdAnimationTimer.start();
    }

    public void pressedAction(boolean clickedPlayButton) {
        if (gameState == GameState.START) {
            if(clickedPlayButton){
                gameState = GameState.PLAYING;
                gameLoopTimer.start();
                placePipeTimer.start();
                birdAnimationTimer.start();
            }
        } else if (gameState == GameState.PLAYING) {
            velocityY = -9;
        } else if (gameState == GameState.GAME_OVER) {
            if (clickedPlayButton) {
                resetGame();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameState == GameState.GAME_OVER) {
            placePipeTimer.stop();
            gameLoopTimer.stop();
            birdAnimationTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            pressedAction(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
