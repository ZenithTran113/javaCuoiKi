package main;

import javax.swing.*;
import model.TetrisModel;
import view.TetrisView;
import controller.TetrisController;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Online Đối Kháng");
        TetrisModel model = new TetrisModel();
        TetrisView view = new TetrisView(model);
        TetrisController controller = new TetrisController(model, view);

        view.setController(controller);

        frame.add(view);
        frame.addKeyListener(controller);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        String[] options = {"Làm Chủ Phòng (Server)", "Vào Phòng (Client)", "Chơi Offline"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Chào mừng bạn đến với Tetris!\nChọn chế độ chơi:",
                "Kết nối Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) { 
            System.out.println("Đang mở cổng 8888, chờ đối thủ...");
            controller.getSocketManager().startServer(8888, controller);
        }
        else if (choice == 1) { 
            String ip = JOptionPane.showInputDialog(frame, "Nhập IP máy chủ (ví dụ: 192.168.1.5):", "localhost");
            if (ip != null && !ip.isEmpty()) {
                controller.getSocketManager().connect(ip, 8888, controller);
            } else {
                System.out.println("Hủy kết nối.");
                System.exit(0   );
            }
        }
        else if (choice == 2) {
            JOptionPane.showMessageDialog(frame, "Chế độ Offline: Nhấn OK để bắt đầu tính giờ!");
            controller.startGame(false);
        }
        else {
            System.exit(0);
        }
    }
}


