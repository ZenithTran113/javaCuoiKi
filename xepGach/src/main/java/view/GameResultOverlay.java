package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import model.TetrisModel;

public class GameResultOverlay extends JPanel {
    private JButton btnRematch;
    private JButton btnMainMenu;

    // Biến cờ lưu trạng thái chế độ chơi
    private boolean isMultiplayer;

    // Hàm khởi tạo (Constructor) nhận thêm tham số boolean isMultiplayer vào vị trí đầu tiên
    public GameResultOverlay(boolean isMultiplayer, String resultText, int myScore, int oppScore, int timePlayed,
                             ActionListener rematchAction, ActionListener menuAction) {

        this.isMultiplayer = isMultiplayer; // Lưu cấu hình chế độ chơi

        // Cấu hình panel nền đen mờ bao phủ toàn bộ bàn cờ game
        this.setBounds(0, 0, TetrisModel.COLS * TetrisView.SIZE, TetrisModel.ROWS * TetrisView.SIZE);
        this.setBackground(new Color(15, 15, 20, 230));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // SỬA LỖI 1: Đổi sang NONE để chữ giữ nguyên kích thước chuẩn, không phình ngang gây tràn viền
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        // SỬA LỖI 1: Tăng khoảng cách đệm trục dọc (Top/Bottom) lên 18 để các dòng chữ không bị dính chặt vào nhau
        gbc.insets = new Insets(18, 20, 18, 20);

        // 1. TIÊU ĐỀ LỚN: GAME OVER
        JLabel lblTitle = new JLabel("GAME OVER", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        this.add(lblTitle, gbc);

        // 2. DÒNG THÔNG BÁO PHÂN ĐỊNH THẮNG THUA (CHỈ HIỆN KHI CHƠI CHẾ ĐỘ 2 NGƯỜI)
        if (this.isMultiplayer) {
            // SỬA LỖI 2: Chuỗi 'resultText' truyền xuống từ Controller lúc này đã được rút gọn
            // thành câu ngắn gọn "BẠN ĐÃ CHIẾN THẮNG!" hoặc "BẠN ĐÃ THẤT BẠI!" tùy theo danh tính máy.
            JLabel lblResult = new JLabel(resultText, SwingConstants.CENTER);

            // SỬA LỖI 1: Hạ Font size xuống 16 giúp câu chữ nằm gọn gàng, thanh thoát ở chính giữa màn hình
            lblResult.setFont(new Font("Arial", Font.BOLD, 16));

            if (resultText.contains("CHIẾN THẮNG")) {
                lblResult.setForeground(new Color(50, 255, 50)); // Màu xanh lá cho người thắng
            } else if (resultText.contains("HÒA")) {
                lblResult.setForeground(Color.YELLOW);
            } else {
                lblResult.setForeground(Color.LIGHT_GRAY); // Màu xám cho người thua
            }
            gbc.gridy = 1;
            this.add(lblResult, gbc);
        }

        // 3. PHẦN ĐIỂM SỐ: TÍNH TOÁN BỐ CỤC THEO CHẾ ĐỘ CHƠI
        gbc.gridy = 2;

        if (this.isMultiplayer) {
            // CHẾ ĐỘ MULTIPLAYER: Chia làm 2 cột song song
            gbc.gridwidth = 1;

            // Cột bên trái (0): Điểm của bạn căn biên trái
            JLabel lblMyScore = new JLabel("Điểm của bạn: " + myScore, SwingConstants.LEFT);
            lblMyScore.setFont(new Font("Arial", Font.PLAIN, 15));
            lblMyScore.setForeground(Color.WHITE);
            gbc.gridx = 0;
            this.add(lblMyScore, gbc);

            // Cột bên phải (1): Điểm đối thủ căn biên phải
            JLabel lblOppScore = new JLabel("Đối thủ: " + (oppScore < 0 ? 0 : oppScore), SwingConstants.RIGHT);
            lblOppScore.setFont(new Font("Arial", Font.PLAIN, 15));
            lblOppScore.setForeground(Color.LIGHT_GRAY);
            gbc.gridx = 1;
            this.add(lblOppScore, gbc);

        } else {
            // CHẾ ĐỘ OFFLINE 1 NGƯỜI:
            gbc.gridwidth = 2;
            gbc.gridx = 0;

            JLabel lblMyScore = new JLabel("Điểm của bạn: " + myScore, SwingConstants.CENTER);
            lblMyScore.setFont(new Font("Arial", Font.BOLD, 18));
            lblMyScore.setForeground(Color.WHITE);
            this.add(lblMyScore, gbc);
        }

        // 4. THỜI GIAN TRẬN ĐẤU (Luôn ở chính giữa)
        int minutes = timePlayed / 60;
        int seconds = timePlayed % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        JLabel lblTime = new JLabel("Thời gian: " + timeStr, SwingConstants.CENTER);
        lblTime.setFont(new Font("Arial", Font.ITALIC, 14));
        lblTime.setForeground(Color.CYAN);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        this.add(lblTime, gbc);

        // 5. PHẦN BUTTONS (NÚT CHỨC NĂNG)
        // Kích hoạt lại fill HORIZONTAL riêng cho các nút bấm để nút trải rộng vừa vặn biên ngang bàn cờ
        gbc.fill = GridBagConstraints.HORIZONTAL;

        btnRematch = new JButton("CHƠI LẠI (REMATCH)");
        btnRematch.setFont(new Font("Arial", Font.BOLD, 18));
        btnRematch.setBackground(new Color(40, 167, 69));
        btnRematch.setForeground(Color.WHITE);
        btnRematch.setFocusable(false);
        btnRematch.addActionListener(rematchAction);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 20, 5, 20); // Tạo khoảng cách đệm phía trên nút rộng hơn một chút
        this.add(btnRematch, gbc);

        btnMainMenu = new JButton("MENU CHÍNH");
        btnMainMenu.setFont(new Font("Arial", Font.PLAIN, 14));
        btnMainMenu.setBackground(new Color(108, 117, 125));
        btnMainMenu.setForeground(Color.WHITE);
        btnMainMenu.setFocusable(false);
        btnMainMenu.addActionListener(menuAction);
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 20, 10, 20);
        this.add(btnMainMenu, gbc);
    }
}