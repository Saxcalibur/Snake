import com.sun.jdi.IntegerValue;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 900;
    static final int SCREEN_HEIGHT = 900;
    static final int UNIT_SIZE = 15;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    char speed = '2';
    boolean running = false;
    Timer timer;
    Random random;
    File pop = new File("Data/pop.wav");
    File gameOverSound = new File("Data/gameOver.wav");
    File ugh = new File("Data/ugh.wav");

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();

    }

    public void startGame() {
        spawnApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void restart() {
        x[0] = 0;
        y[0] = 0;
        bodyParts = 6;
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[0];
            y[i] = y[0];
        }
        applesEaten = 0;
        direction = 'R';
        spawnApple();
        running = true;
        timer.start();
    }

    public void speedChange(char speed) {
        switch (speed) {
            case '1':
                timer.stop();
                DELAY = 100;
                timer = new Timer(DELAY, this);
                timer.start();
                break;
            case '2':
                timer.stop();
                DELAY = 75;
                timer = new Timer(DELAY, this);
                timer.start();
                break;
            case '3':
                timer.stop();
                DELAY = 50;
                timer = new Timer(DELAY, this);
                timer.start();
                break;
            case '4':
                timer.stop();
                DELAY = 25;
                timer = new Timer(DELAY, this);
                timer.start();
                break;
            case '5':
                timer.stop();
                DELAY = 15;
                timer = new Timer(DELAY, this);
                timer.start();
                break;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
//            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
//                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
//                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
//            }
            g.setColor(new Color(0x720808));
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(0xEFEFEF));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

                } else {
                    g.setColor(new Color(0xC0C0C0));
//                    g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(new Color(0x720808));
            g.setFont(new Font ("Times New Roman",Font.PLAIN,30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten,(SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2,g.getFont().getSize());

        }
        else {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(ugh);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
            gameOver(g);
        }
    }

    public void spawnApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[i] == appleX) && (y[i] == appleY)) {
                bodyParts++;
                applesEaten+= Character.getNumericValue(speed);
                spawnApple();
                try {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(pop);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void checkCollision() {
        //snake collision
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //border collision
        if (x[0] < 0) {
            running = false;
        }
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        if (y[0] < 0) {
            running = false;
        }
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }

    }

    public void gameOver(Graphics g) {
        //Score txt
        g.setColor(new Color(0x720808));
        g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        //Game Over txt
        g.setColor(new Color(0x720808));
        g.setFont(new Font("Times New Roman", Font.PLAIN, 69));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("YOU DIED", (SCREEN_WIDTH - metrics2.stringWidth("YOU DIED")) / 2, (SCREEN_HEIGHT / 2));
        //Game Over Sound
            try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(gameOverSound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R' && x[1] != (x[0]-UNIT_SIZE)) {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L' && x[1] != (x[0]+UNIT_SIZE)) {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D' && y[1] != (y[0]-UNIT_SIZE)) {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U' && y[1] != (y[0]+UNIT_SIZE)) {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_R:
                    restart();
                    break;
                case KeyEvent.VK_1:
                    speed = '1';
                    speedChange(speed);
                    break;
                case KeyEvent.VK_2:
                    speed = '2';
                    speedChange(speed);
                    break;
                case KeyEvent.VK_3:
                    speed = '3';
                    speedChange(speed);
                    break;
                case KeyEvent.VK_4:
                    speed = '4';
                    speedChange(speed);
                    break;
                case KeyEvent.VK_5:
                    speed = '5';
                    speedChange(speed);
                    break;
            }

        }
    }
}
