package main;

import javax.swing.*;
import model.TetrisModel;
import view.TetrisView;
import controller.TetrisController;

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo các thành phần cơ bản
        JFrame frame = new JFrame("Tetris Online Đối Kháng");
        TetrisModel model = new TetrisModel();
        TetrisView view = new TetrisView(model);
        TetrisController controller = new TetrisController(model, view);

        // 2. KẾT NỐI VIEW VỚI CONTROLLER
        view.setController(controller);

        // 3. Thiết lập Frame
        frame.add(view);
        frame.addKeyListener(controller);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        // 4. Lựa chọn chế độ chơi
        String[] options = {"Làm Chủ Phòng (Server)", "Vào Phòng (Client)", "Chơi Offline"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Chào mừng bạn đến với Tetris!\nChọn chế độ chơi:",
                "Kết nối Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        // 5. Xử lý logic khởi chạy
        if (choice == 0) { // Server
            System.out.println("Đang mở cổng 8888, chờ đối thủ...");
            // Khi kết nối thành công, SocketManager sẽ gọi controller.startGame(true)
            controller.getSocketManager().startServer(8888, controller);
        }
        else if (choice == 1) { // Client
            String ip = JOptionPane.showInputDialog(frame, "Nhập IP máy chủ (ví dụ: 192.168.1.5):", "localhost");
            if (ip != null && !ip.isEmpty()) {
                //Khi kết nối thành công, SocketManager sẽ gọi controller.startGame(true)
                controller.getSocketManager().connect(ip, 8888, controller);
            } else {
                System.out.println("Hủy kết nối.");
                System.exit(0   );
            }
        }
        else if (choice == 2) { // Chơi Offline
            JOptionPane.showMessageDialog(frame, "Chế độ Offline: Nhấn OK để bắt đầu tính giờ!");
            // CẬP NHẬT: Truyền 'false' vào startGame để báo hiệu chơi Offline
            controller.startGame(false);
        }
        else {
            System.exit(0);
        }
    }
}


