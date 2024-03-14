package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BalloonShootingGame extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;
    private final int WIDTH = 600;
    private final int HEIGHT = 400;
    private final int TRIANGLE_WIDTH = 50;
    private final int TRIANGLE_HEIGHT = 20;
    private final int BALLOON_WIDTH = 30;
    private final int BALLOON_HEIGHT = 50;
    private final int BULLET_SIZE = 5;
    private final int BALLOON_SPEED = 3;
    private final int GAME_DURATION = 120; // 2 minutes in seconds
    private Timer gameTimer;
    private Timer balloonTimer;
    private ArrayList<Point> balloons;
    private Point triangle;
    private ArrayList<Point> bullets;
    private int score;
    private long startTime;

    public BalloonShootingGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        startGame();
    }

    public void startGame() {
        balloons = new ArrayList<>();
        triangle = new Point(WIDTH / 2 - TRIANGLE_WIDTH / 2, HEIGHT - TRIANGLE_HEIGHT - 10);
        bullets = new ArrayList<>();
        score = 0;
        startTime = System.currentTimeMillis() / 1000;

        gameTimer = new Timer(GAME_DURATION * 1000, this);
        gameTimer.setRepeats(false);
        gameTimer.start();

        balloonTimer = new Timer(1000 / 30, this);
        balloonTimer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (Point bullet : bullets) {
            g.setColor(Color.WHITE);
            g.fillRect(bullet.x, bullet.y, BULLET_SIZE, BULLET_SIZE);
        }

        g.setColor(Color.BLACK);
        int[] xPoints = {triangle.x, triangle.x + TRIANGLE_WIDTH / 2, triangle.x + TRIANGLE_WIDTH};
        int[] yPoints = {triangle.y + TRIANGLE_HEIGHT, triangle.y, triangle.y + TRIANGLE_HEIGHT};
        g.fillPolygon(xPoints, yPoints, 3);

        if (balloons != null) {
            g.setColor(Color.RED);
            for (Point balloon : balloons) {
                g.fillOval(balloon.x, balloon.y, BALLOON_WIDTH, BALLOON_HEIGHT);
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, WIDTH - 100, 30);

        long elapsedTime = System.currentTimeMillis() / 1000 - startTime;
        int remainingTime = GAME_DURATION - (int) elapsedTime;
        g.drawString("Time: " + remainingTime + "s", 20, 30);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Timer) {
            if (e.getSource() == balloonTimer) {
                moveBalloons();
                checkCollisions();
            } else if (e.getSource() == gameTimer) {
                endGame();
            }
            repaint();
        } else if (e.getActionCommand().equals("Play")) {
            startGame();
            repaint();
        }
    }

    public void moveBalloons() {
        for (int i = 0; i < balloons.size(); i++) {
            Point balloon = balloons.get(i);
            balloon.y -= BALLOON_SPEED;
            if (balloon.y + BALLOON_HEIGHT < 0) {
                balloons.remove(i);
                i--;
            }
        }
        if (Math.random() < 0.03) {
            int x = (int) (Math.random() * (WIDTH - BALLOON_WIDTH));
            balloons.add(new Point(x, HEIGHT));
        }

        for (int i = 0; i < bullets.size(); i++) {
            Point bullet = bullets.get(i);
            bullet.y -= 10; // Adjust bullet speed
            if (bullet.y < 0) {
                bullets.remove(i);
                i--;
            }
        }
    }

    public void checkCollisions() {
        for (int i = 0; i < bullets.size(); i++) {
            Point bullet = bullets.get(i);
            for (int j = 0; j < balloons.size(); j++) {
                Point balloon = balloons.get(j);
                if (bullet.x >= balloon.x && bullet.x <= balloon.x + BALLOON_WIDTH &&
                    bullet.y >= balloon.y && bullet.y <= balloon.y + BALLOON_HEIGHT) {
                    bullets.remove(i);
                    i--;
                    balloons.remove(j);
                    j--;
                    score += 2;
                    break;
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            if (triangle.x > 0) {
                triangle.x -= 10;
                repaint();
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            if (triangle.x + TRIANGLE_WIDTH < WIDTH) {
                triangle.x += 10;
                repaint();
            }
        } else if (key == KeyEvent.VK_A) {
            bullets.add(new Point(triangle.x + TRIANGLE_WIDTH / 2, HEIGHT - TRIANGLE_HEIGHT));
            repaint();
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    public void endGame() {
        balloonTimer.stop();
        JOptionPane.showMessageDialog(this, "Game Over!\nYour final score: " + score, "Game Over", JOptionPane.PLAIN_MESSAGE);
        int choice = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            startGame();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Balloon Shooting Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.getContentPane().add(new BalloonShootingGame());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}














