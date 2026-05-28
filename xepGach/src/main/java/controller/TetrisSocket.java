package controller;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class TetrisSocket implements Runnable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private TetrisController controller;
    private volatile boolean isRunning = false;

    public void startServer(int port, TetrisController controller) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("Đang đợi đối thủ kết nối ở cổng " + port + "...");
                this.socket = server.accept();
                init(controller);
                SwingUtilities.invokeLater(() -> controller.startGame(true, controller.isServerSide()));
            } catch (Exception e) {
                showError("Lỗi Server: " + e.getMessage());
            }
        }).start();
    }

    public void connect(String ip, int port, TetrisController controller) {
        new Thread(() -> {
            try {
                this.socket = new Socket();
                this.socket.connect(new InetSocketAddress(ip, port), 5000);
                init(controller);
                SwingUtilities.invokeLater(() -> controller.startGame(true, controller.isServerSide()));
            } catch (Exception e) {
                showError("Không thể kết nối đến đối thủ: " + e.getMessage());
            }
        }).start();
    }

    private void init(TetrisController controller) throws IOException {
        this.controller = controller;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.isRunning = true;

        Thread listenThread = new Thread(this);
        listenThread.setDaemon(true);
        listenThread.start();
        System.out.println("Kết nối thành công!");
    }

    public synchronized void sendData(String data) {
        try {
            if (out != null && isRunning) {
                out.writeUTF(data);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Mất kết nối khi gửi dữ liệu.");
            isRunning = false;
        }
    }

    @Override
    public void run() {
        try {
            while (isRunning && socket != null && !socket.isClosed()) {
                String msg = in.readUTF();
                processMessage(msg);
            }
        } catch (Exception e) {
            System.out.println("Đối thủ đã rời trận.");
        } finally {
            closeConnection();
        }
    }

    private void processMessage(String msg) {
        if ("ATTACK".equals(msg)) {
            controller.receiveAttack();
        } else if (msg.startsWith("SCORE:")) {
            try {
                int opponentScore = Integer.parseInt(msg.substring(6));
                controller.handleResult(opponentScore);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi đọc điểm!");
            }
        }
        // KHI NHẬN ĐƯỢC ĐIỂM HẾT GIỜ: Cho phép cập nhật đè lên bảng đang hiển thị
        else if (msg.startsWith("SCORE_END:")) {
            try {
                int opponentScore = Integer.parseInt(msg.substring(10));
                // Ép luồng đồ họa cập nhật lại chữ và điểm đối thủ thực tế
                SwingUtilities.invokeLater(() -> {
                    controller.handleResult(opponentScore);
                });
            } catch (NumberFormatException e) {
                System.err.println("Lỗi đọc điểm hết giờ!");
            }
        }
        else if ("CMD_REMATCH_REQUEST".equals(msg)) {
            new Thread(() -> {
                int choice = JOptionPane.showConfirmDialog(null,
                        "Đối thủ muốn thách đấu lại, bạn có đồng ý không?",
                        "Lời mời Thách Đấu",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    sendData("CMD_REMATCH_ACCEPT");
                    executeNewMatch();
                } else {
                    sendData("CMD_REMATCH_DENIED");
                }
            }).start();
        }
        else if ("CMD_REMATCH_ACCEPT".equals(msg)) {
            executeNewMatch();
        }
        else if ("CMD_REMATCH_DENIED".equals(msg)) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Đối thủ đã từ chối lời mời đấu lại!");
            });
        }
    }

    private void executeNewMatch() {
        SwingUtilities.invokeLater(() -> {
            if (controller != null) {
                try {
                    controller.resetMatch();
                    controller.startGame(true, controller.isServerSide());
                    System.out.println("Bắt đầu trận đấu mới đồng bộ thành công!");
                } catch (Exception e) {
                    System.err.println("Lỗi đồng bộ reset trận đấu mới: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message);
        });
    }

    public void closeConnection() {
        isRunning = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}