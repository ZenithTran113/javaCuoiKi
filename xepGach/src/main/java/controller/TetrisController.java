package controller;

import model.TetrisModel;
import view.TetrisView;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TetrisController extends KeyAdapter implements Runnable {
    private TetrisModel model;
    private TetrisView view;
    private TetrisSocket socketManager;
    private boolean isRunning = false;
    private int timeLeft = 60; // 60 giây đếm ngược
    private boolean isGameOverProcessed = false; // Chống hiện thông báo nhiều lần
    private boolean isMultiplayer = false;
    private boolean isServer = false; // true = Player 1, false = Player 2

    public TetrisController(TetrisModel model, TetrisView view) {
        this.model = model;
        this.view = view;
        this.socketManager = new TetrisSocket();
    }

    public boolean isServerSide() {
        return this.isServer;
    }

    public void startGame(boolean multiplayer) {
        startGame(multiplayer, false);
    }

    public void startGame(boolean multiplayer, boolean isServerSide) {
        if (!isRunning) {
            this.isMultiplayer = multiplayer;
            this.isServer = isServerSide;
            this.timeLeft = 60;
            this.isGameOverProcessed = false;

            view.removeResultOverlay();

            model.spawnPiece();
            isRunning = true;
            new Thread(this).start();
            startCountdown();
        }
    }

    // --- FIX DỨT ĐIỂM: CHỦ ĐỘNG BẬT OVERLAY KHI HẾT GIỜ, CHỐNG ĐƠ ---
    private void startCountdown() {
        new Thread(() -> {
            while (timeLeft > 0 && isRunning) {
                try { Thread.sleep(1000); } catch (Exception e) {}

                if (!isRunning) return;
                timeLeft--;
                view.repaint();
            }

            // Khi hết giờ hoàn toàn
            if (timeLeft <= 0 && isRunning) {
                isRunning = false; // Dừng rơi gạch

                if (isMultiplayer) {
                    // 1. Báo điểm của mình cho đối thủ biết
                    socketManager.sendData("SCORE_END:" + model.getScore());

                    // 2. ÉP máy mình phải tự hiện bảng kết quả đen lên ngay lập tức, tạm thời coi như đối thủ 0 điểm
                    SwingUtilities.invokeLater(() -> {
                        this.isGameOverProcessed = false; // Cho phép hàm chạy để hiện bảng
                        handleResult(-3);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> handleResult(-3));
                }
            }
        }).start();
    }

    public void receiveAttack() {
        if (isRunning) {
            model.addGarbageLine();
            view.repaint();
        }
    }

    // --- HÀM XỬ LÝ KẾT QUẢ ĐÃ ĐƯỢC CẬP NHẬT LOGIC ĐẬP TAN DEADLOCK ---
    public void handleResult(int opponentScore) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> handleResult(opponentScore));
            return;
        }

        // Nếu bảng đã hiện rồi và nhận được điểm thực tế từ mạng (-3 là mã giữ chỗ khi vừa hết giờ)
        if (isGameOverProcessed && opponentScore == -3) return;

        isGameOverProcessed = true;
        isRunning = false;

        final int myScore = model.getScore();
        final int timePlayed = 60 - timeLeft;

        // Xử lý điểm đối thủ nếu chưa nhận được gói tin mạng kịp lúc thì tạm để là 0
        int displayOpponentScore = (opponentScore >= 0) ? opponentScore : 0;

        String resultText;
        if (!isMultiplayer) {
            if (opponentScore == -3) resultText = "HẾT GIỜ TRẬN ĐẤU!";
            else resultText = "";
        } else {
            if (opponentScore == -1) {
                resultText = "BẠN ĐÃ CHIẾN THẮNG!";
            } else if (opponentScore == -2) {
                resultText = "BẠN ĐÃ THẤT BẠI!";
            } else if (opponentScore == -3) {
                // Trạng thái vừa hết giờ, đang đợi mạng cập nhật điểm đối thủ
                resultText = "ĐANG TÍNH ĐIỂM SỐ...";
            } else {
                // Đã có điểm thực của đối thủ từ gói tin SCORE_END
                if (myScore > opponentScore) {
                    resultText = "BẠN ĐÃ CHIẾN THẮNG!";
                } else if (myScore < opponentScore) {
                    resultText = "BẠN ĐÃ THẤT BẠI!";
                } else {
                    resultText = "TRẬN ĐẤU HÒA!";
                }
            }
        }

        ActionListener rematchAction = e -> {
            if (isMultiplayer) {
                socketManager.sendData("CMD_REMATCH_REQUEST");
                new Thread(() -> {
                    JOptionPane.showMessageDialog(view, "Đã gửi lời mời đấu lại! Đang đợi đối thủ phản hồi...");
                }).start();
            } else {
                resetMatch();
                startGame(false);
            }
        };

        ActionListener menuAction = e -> {
            if (isMultiplayer) {
                socketManager.closeConnection();
            }
            System.exit(0);
        };

        // Mỗi lần gọi lại (kể cả khi cập nhật điểm thực tế), ta xóa overlay cũ đi vẽ lại overlay mới
        view.removeResultOverlay();
        view.repaint();
        view.showResultOverlay(isMultiplayer, resultText, myScore, displayOpponentScore, timePlayed, rematchAction, menuAction);
    }

    public void resetMatch() {
        isRunning = false;
        isGameOverProcessed = false;
        this.timeLeft = 60;

        if (model != null) {
            model.resetGame();
        }
        if (view != null) {
            view.removeResultOverlay();
            view.repaint();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            boolean moved = model.move(0, 1);

            if (!moved) {
                model.freeze();
                int lines = model.clearLines();

                if (lines > 0 && isMultiplayer) {
                    socketManager.sendData("ATTACK");
                }
                model.spawnPiece();
            }
            view.repaint();

            if (model.isGameOver()) {
                isRunning = false;
                if (isMultiplayer) {
                    socketManager.sendData("SCORE:-1");
                }
                SwingUtilities.invokeLater(() -> handleResult(-2));
                break;
            }
            try { Thread.sleep(600); } catch (Exception e) {}
        }
    }

    public int getTimeLeft() { return timeLeft; }
    public TetrisSocket getSocketManager() { return socketManager; }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isRunning || model.isGameOver() || isGameOverProcessed) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> model.move(-1, 0);
            case KeyEvent.VK_RIGHT -> model.move(1, 0);
            case KeyEvent.VK_DOWN -> model.move(0, 1);
            case KeyEvent.VK_UP -> model.rotate();
        }
        view.repaint();
    }
}