package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import model.TetrisModel;
import controller.TetrisController;

public class TetrisView extends JPanel {
    private TetrisModel model;
    private TetrisController controller;
    public static final int SIZE = 30;

    private final Color[] colors = {
            Color.CYAN, Color.MAGENTA, Color.RED, Color.GREEN,
            Color.YELLOW, Color.ORANGE, Color.BLUE, Color.GRAY
    };

    private JLayeredPane layeredPane;
    private JPanel gameCanvas;
    private GameResultOverlay overlayPanel; 

    public TetrisView(TetrisModel model) {
        this.model = model;
        int width = TetrisModel.COLS * SIZE;
        int height = TetrisModel.ROWS * SIZE;

        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(width, height));

        gameCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderGameGraphics(g); 
            }
        };
        gameCanvas.setBounds(0, 0, width, height);
        gameCanvas.setOpaque(false); 

        layeredPane.add(gameCanvas, JLayeredPane.DEFAULT_LAYER);
        this.add(layeredPane, BorderLayout.CENTER);
    }

    public void setController(TetrisController controller) {
        this.controller = controller;
    }

    public void showResultOverlay(boolean isMultiplayer, String resultText, int myScore, int oppScore, int timePlayed,
                                  ActionListener rematchAction, ActionListener menuAction) {
        removeResultOverlay();

        overlayPanel = new GameResultOverlay(isMultiplayer, resultText, myScore, oppScore, timePlayed, rematchAction, menuAction);

        layeredPane.add(overlayPanel, JLayeredPane.POPUP_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    public void removeResultOverlay() {
        if (overlayPanel != null) {
            layeredPane.remove(overlayPanel);
            overlayPanel = null;
            layeredPane.revalidate();
            layeredPane.repaint();
        }
    }

    @Override
    public void repaint() {
        super.repaint();
        if (gameCanvas != null) {
            gameCanvas.repaint();
        }
    }

    private void renderGameGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(20, 20, 25));
        g2.fillRect(0, 0, TetrisModel.COLS * SIZE, TetrisModel.ROWS * SIZE);

        g2.setColor(new Color(40, 40, 50));
        for (int i = 0; i <= TetrisModel.COLS; i++) {
            g2.drawLine(i * SIZE, 0, i * SIZE, TetrisModel.ROWS * SIZE);
        }
        for (int i = 0; i <= TetrisModel.ROWS; i++) {
            g2.drawLine(0, i * SIZE, TetrisModel.COLS * SIZE, i * SIZE);
        }

        int[][] board = model.getBoard();
        for (int r = 0; r < TetrisModel.ROWS; r++) {
            for (int c = 0; c < TetrisModel.COLS; c++) {
                if (board[r][c] != 0) {
                    drawBlock(g2, c * SIZE, r * SIZE, colors[board[r][c] - 1]);
                }
            }
        }

        int[][] shape = model.getCurrentShape();
        if (shape != null) {
            Color curColor = colors[model.getCurrentType()];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] != 0) {
                        int drawX = (model.getCurX() + j) * SIZE;
                        int drawY = (model.getCurY() + i) * SIZE;
                        drawBlock(g2, drawX, drawY, curColor);
                    }
                }
            }
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));

        g2.drawString("Score: " + model.getScore(), 10, 25);

        g2.setColor(new Color(255, 200, 0));
        g2.drawString("Best: " + model.getHighScore(), 10, 45);

        if (controller != null) {
            int time = controller.getTimeLeft();
            if (time <= 10) g2.setColor(Color.RED);
            else g2.setColor(Color.GREEN);

            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Time: " + time + "s", 10, 70);
        }
    }

    private void drawBlock(Graphics2D g2, int x, int y, Color color) {
        g2.setColor(color);
        g2.fillRect(x, y, SIZE, SIZE);
        g2.setColor(color.brighter());
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x + 2, y + 2, SIZE - 4, SIZE - 4);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(x, y, SIZE, SIZE);
    }
}
