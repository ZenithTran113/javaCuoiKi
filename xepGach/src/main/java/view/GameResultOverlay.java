package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import model.TetrisModel;

public class GameResultOverlay extends JPanel {
    private JButton btnRematch;
    private JButton btnMainMenu;

    private boolean isMultiplayer;

    public GameResultOverlay(boolean isMultiplayer, String resultText, int myScore, int oppScore, int timePlayed,
                             ActionListener rematchAction, ActionListener menuAction) {

        this.isMultiplayer = isMultiplayer; 

        this.setBounds(0, 0, TetrisModel.COLS * TetrisView.SIZE, TetrisModel.ROWS * TetrisView.SIZE);
        this.setBackground(new Color(15, 15, 20, 230));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(18, 20, 18, 20);

        JLabel lblTitle = new JLabel("GAME OVER", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        this.add(lblTitle, gbc);

        if (this.isMultiplayer) {
            JLabel lblResult = new JLabel(resultText, SwingConstants.CENTER);

            lblResult.setFont(new Font("Arial", Font.BOLD, 16));

            if (resultText.contains("CHIẾN THẮNG")) {
                lblResult.setForeground(new Color(50, 255, 50)); 
            } else if (resultText.contains("HÒA")) {
                lblResult.setForeground(Color.YELLOW);
            } else {
                lblResult.setForeground(Color.LIGHT_GRAY); 
            }
            gbc.gridy = 1;
            this.add(lblResult, gbc);
        }

        gbc.gridy = 2;

        if (this.isMultiplayer) {
            gbc.gridwidth = 1;

            JLabel lblMyScore = new JLabel("Điểm của bạn: " + myScore, SwingConstants.LEFT);
            lblMyScore.setFont(new Font("Arial", Font.PLAIN, 15));
            lblMyScore.setForeground(Color.WHITE);
            gbc.gridx = 0;
            this.add(lblMyScore, gbc);

            JLabel lblOppScore = new JLabel("Đối thủ: " + (oppScore < 0 ? 0 : oppScore), SwingConstants.RIGHT);
            lblOppScore.setFont(new Font("Arial", Font.PLAIN, 15));
            lblOppScore.setForeground(Color.LIGHT_GRAY);
            gbc.gridx = 1;
            this.add(lblOppScore, gbc);

        } else {
            gbc.gridwidth = 2;
            gbc.gridx = 0;

            JLabel lblMyScore = new JLabel("Điểm của bạn: " + myScore, SwingConstants.CENTER);
            lblMyScore.setFont(new Font("Arial", Font.BOLD, 18));
            lblMyScore.setForeground(Color.WHITE);
            this.add(lblMyScore, gbc);
        }

        int minutes = timePlayed / 60;
        int seconds = timePlayed % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        JLabel lblTime = new JLabel("Thời gian: " + timeStr, SwingConstants.CENTER);
        lblTime.setFont(new Font("Arial", Font.ITALIC, 14));
        lblTime.setForeground(Color.CYAN);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        this.add(lblTime, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        btnRematch = new JButton("CHƠI LẠI (REMATCH)");
        btnRematch.setFont(new Font("Arial", Font.BOLD, 18));
        btnRematch.setBackground(new Color(40, 167, 69));
        btnRematch.setForeground(Color.WHITE);
        btnRematch.setFocusable(false);
        btnRematch.addActionListener(rematchAction);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 20, 5, 20); 
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
