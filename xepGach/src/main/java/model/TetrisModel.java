package model;

import javax.swing.JOptionPane;
import java.io.*;

public class TetrisModel {
    public static final int ROWS = 20;
    public static final int COLS = 10;
    private int[][] board = new int[ROWS][COLS];

    private final int[][] shapeI = {{1, 1, 1, 1}};
    private final int[][] shapeT = {{1, 1, 1}, {0, 1, 0}};
    private final int[][] shapeZ = {{1, 1, 0}, {0, 1, 1}};
    private final int[][] shapeS = {{0, 1, 1}, {1, 1, 0}};
    private final int[][] shapeO = {{1, 1}, {1, 1}};
    private final int[][] shapeL = {{1, 1, 1}, {1, 0, 0}};
    private final int[][] shapeJ = {{1, 1, 1}, {0, 0, 1}};
    public final int[][][] ALL_SHAPES = {shapeI, shapeT, shapeZ, shapeS, shapeO, shapeL, shapeJ};

    private int[][] currentShape;
    private int curX, curY, currentType;
    private int score = 0;
    private int highScore = 0;
    private boolean isGameOver = false;

    public TetrisModel() {
        this.highScore = loadHighScore();
    }

    private void saveHighScore() {
        if (score > highScore) {
            highScore = score;
            try (PrintWriter out = new PrintWriter(new FileWriter("highscore.txt"))) {
                out.println(highScore);
            } catch (IOException e) {
                System.err.println("Không thể lưu kỷ lục: " + e.getMessage());
            }
        }
    }

    private int loadHighScore() {
        File file = new File("highscore.txt");
        if (!file.exists()) return 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            return (line != null) ? Integer.parseInt(line.trim()) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public void addGarbageLine() {
        for (int i = 0; i < ROWS - 1; i++) {
            board[i] = board[i + 1].clone();
        }
        int[] garbageLine = new int[COLS];
        int hole = (int) (Math.random() * COLS);
        for (int j = 0; j < COLS; j++) {
            garbageLine[j] = (j == hole) ? 0 : 8;
        }
        board[ROWS - 1] = garbageLine;
    }

    public void spawnPiece() {
        currentType = (int) (Math.random() * ALL_SHAPES.length);
        currentShape = ALL_SHAPES[currentType];
        curX = 4; curY = 0;

        if (!canMove(currentShape, curX, curY)) {
            isGameOver = true;
            saveHighScore();
        }
    }

    public void resetGame() {
        board = new int[ROWS][COLS];
        score = 0;
        isGameOver = false;
        spawnPiece();
    }

    public boolean move(int dx, int dy) {
        if (isGameOver) return false;
        if (canMove(currentShape, curX + dx, curY + dy)) {
            curX += dx;
            curY += dy;
            return true;
        }
        return false;
    }

    public void rotate() {
        if (isGameOver) return;
        int[][] rotated = new int[currentShape[0].length][currentShape.length];
        for (int i = 0; i < currentShape.length; i++)
            for (int j = 0; j < currentShape[0].length; j++)
                rotated[j][currentShape.length - 1 - i] = currentShape[i][j];
        if (canMove(rotated, curX, curY)) currentShape = rotated;
    }
    private boolean canMove(int[][] shape, int nextX, int nextY) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int tx = nextX + j;
                    int ty = nextY + i;
                    if (tx < 0 || tx >= COLS || ty >= ROWS) return false;
                    if (ty >= 0 && board[ty][tx] != 0) return false;
                }
            }
        }
        return true;
    }

    public void freeze() {
        if (currentShape == null) return;
        for (int i = 0; i < currentShape.length; i++)
            for (int j = 0; j < currentShape[i].length; j++)
                if (currentShape[i][j] != 0) {
                    int  boardY= curY + i;
                    int boardX = curX + j;
                    if (boardY >= 0 && boardY < ROWS && boardX >= 0 && boardX < COLS) {
                        board[boardY][boardX] = currentType + 1;
                    }
                }
    }

    public int clearLines() {
        int lines = 0;
        for (int i = ROWS - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < COLS; j++) if (board[i][j] == 0) full = false;
            if (full) {
                for (int k = i; k > 0; k--) board[k] = board[k - 1].clone();//hàng đủ
                board[0] = new int[COLS];
                lines++;
                i++; 
            }
        }
        score += lines * 100;
        saveHighScore(); 
        return lines;
    }

    public boolean isGameOver() { return isGameOver; }
    public int[][] getBoard() { return board; }
    public int[][] getCurrentShape() { return currentShape; }
    public int getCurX() { return curX; }
    public int getCurY() { return curY; }
    public int getCurrentType() { return currentType; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
}
