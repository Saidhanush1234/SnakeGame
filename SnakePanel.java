import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakePanel extends JPanel implements ActionListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int ALL_TILES = (WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE);
    private final int RAND_POS = 29;
    private final int DELAY = 100;

    private final ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private int direction;
    private boolean running;
    private boolean grow;
    private Timer timer;

    public SnakePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) {
                    direction = KeyEvent.VK_LEFT;
                } else if (key == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) {
                    direction = KeyEvent.VK_RIGHT;
                } else if (key == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) {
                    direction = KeyEvent.VK_UP;
                } else if (key == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) {
                    direction = KeyEvent.VK_DOWN;
                }
            }
        });
    }

    public void startGame() {
        running = true;
        direction = KeyEvent.VK_RIGHT;
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        spawnFood();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void spawnFood() {
        Random rand = new Random();
        int x = rand.nextInt(RAND_POS) * TILE_SIZE;
        int y = rand.nextInt(RAND_POS) * TILE_SIZE;
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            g.setColor(Color.RED);
            g.fillRect(food.x, food.y, TILE_SIZE, TILE_SIZE);

            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x, p.y, TILE_SIZE, TILE_SIZE);
            }
        } else {
            showGameOver(g);
        }
    }

    private void showGameOver(Graphics g) {
        String message = "Game Over";
        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(message, (WIDTH - metrics.stringWidth(message)) / 2, HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            moveSnake();
            checkCollision();
            checkFood();
            repaint();
        }
    }

    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = (Point) head.clone();

        switch (direction) {
            case KeyEvent.VK_LEFT:
                newHead.translate(-TILE_SIZE, 0);
                break;
            case KeyEvent.VK_RIGHT:
                newHead.translate(TILE_SIZE, 0);
                break;
            case KeyEvent.VK_UP:
                newHead.translate(0, -TILE_SIZE);
                break;
            case KeyEvent.VK_DOWN:
                newHead.translate(0, TILE_SIZE);
                break;
        }

        snake.add(0, newHead);
        if (!grow) {
            snake.remove(snake.size() - 1);
        } else {
            grow = false;
        }
    }

    private void checkCollision() {
        Point head = snake.get(0);

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
            }
        }

        if (!running) {
            timer.stop();
        }
    }

    private void checkFood() {
        Point head = snake.get(0);
        if (head.equals(food)) {
            grow = true;
            spawnFood();
        }
    }
}
