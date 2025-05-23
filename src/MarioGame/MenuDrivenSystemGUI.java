package MarioGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import javax.swing.border.EmptyBorder;


public class MenuDrivenSystemGUI {

    // 用于存储用户数据的Map
    private static Map<String, String> userDatabase = new HashMap<>();
    private static boolean isLoggedIn = false; // 用于标记用户是否已经登录
    private static String currentUser = ""; // 当前登录用户

    public static void main(String[] args) {
        // 加载已有的用户数据
        loadUserDatabase();

        // 创建主窗口
        JFrame frame = new JFrame("Menu Driven System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        // 加载背景图片
        ImageIcon backgroundImage = new ImageIcon("src/MarioGame/R.jpg"); // 请将背景图放在项目根目录下
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new GridBagLayout()); // 使用 GridBagLayout，让按钮面板在中间

        // 创建菜单按钮面板（透明）
        JPanel panel = new JPanel(new GridLayout(10, 1, 5, 5)); // 增加行距
        panel.setOpaque(false); // 设置面板透明，让背景图透出来

        // 创建按钮
        JButton btnRegister = new JButton("Register");
        JButton btnLogin = new JButton("Login");
        JButton btnMarioGame1 = new JButton("Mario Game 1");
        JButton btnMarioGame2 = new JButton("Mario Game 2");
        JButton btnMarioGame3 = new JButton("Mario Game 3");
        JButton btnGuessingGame = new JButton("Guessing Game");
        JButton btnExit = new JButton("Exit");

        // 添加按钮到面板
        panel.add(btnRegister);
        panel.add(btnLogin);
        panel.add(btnMarioGame1);
        panel.add(btnMarioGame2);
        panel.add(btnMarioGame3);
        panel.add(btnGuessingGame);
        panel.add(btnExit);

        // 设置按钮样式方法
        JButton[] buttons = {btnRegister, btnLogin, btnMarioGame1, btnMarioGame2, btnMarioGame3, btnGuessingGame, btnExit};

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 18)); // 设置字体
            button.setBackground(new Color(70, 130, 180));     // 设置背景色
            button.setForeground(Color.WHITE);                 // 设置字体颜色
            button.setFocusPainted(false);                     // 去除焦点框
            button.setBorder(new EmptyBorder(10, 20, 10, 20)); // 设置内边距，扩大按钮区域
        }

        // 将面板添加到背景标签（居中）
        backgroundLabel.add(panel);

        // 设置背景标签为内容面板
        frame.setContentPane(backgroundLabel);

        // 设置窗口居中显示
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 设置按钮的功能
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        btnMarioGame1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    startMarioGame1();
            }
        });

        btnMarioGame2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMarioGame2();
            }
        });

        btnMarioGame3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMarioGame3();
            }
        });

        btnGuessingGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoggedIn) {
                    guessingGame();
                } else {
                    JOptionPane.showMessageDialog(null, "You must be logged in to play the game.");
                }
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // 用户注册
    public static void registerUser() {
        String username = JOptionPane.showInputDialog("Enter a username:");
        String password = JOptionPane.showInputDialog("Enter a password:");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username and password cannot be empty.");
            return;
        }

        if (userDatabase.containsKey(username)) {
            JOptionPane.showMessageDialog(null, "Username already exists.");
        } else {
            userDatabase.put(username, password);
            saveUserDatabase();
            JOptionPane.showMessageDialog(null, "Registration successful!");
        }
    }

    // 用户登录
    public static void loginUser() {
        String username = JOptionPane.showInputDialog("Enter your username:");
        String password = JOptionPane.showInputDialog("Enter your password:");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username and password cannot be empty.");
            return;
        }

        if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
            isLoggedIn = true;
            currentUser = username;
            JOptionPane.showMessageDialog(null, "Login successful! Welcome, " + username + ".");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password.");
        }
    }

    // 加载用户数据（从文件）
    public static void loadUserDatabase() {
        try {
            File file = new File("userDatabase.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        userDatabase.put(parts[0], parts[1]);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存用户数据（到文件）
    public static void saveUserDatabase() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("userDatabase.txt"));
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 游戏实现（与之前相同）

    public static void guessingGame() {
        int number = (int) (Math.random() * 100) + 1;
        int guess = 0;
        int attempts = 0;

        while (guess != number) {
            String input = JOptionPane.showInputDialog("Guess a number between 1 and 100:");
            guess = Integer.parseInt(input);
            attempts++;

            if (guess < number) {
                JOptionPane.showMessageDialog(null, "Too low! Try again.");
            } else if (guess > number) {
                JOptionPane.showMessageDialog(null, "Too high! Try again.");
            }
        }

        JOptionPane.showMessageDialog(null, "Correct! You guessed the number in " + attempts + " attempts.");
    }

    public static void startMarioGame1() {
        JFrame marioFrame = new JFrame("Mario Game 1");
        marioFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        marioFrame.setSize(800, 600);
        marioFrame.add(new MarioGamePanel1());
        marioFrame.setVisible(true);
    }

    public static void startMarioGame2() {
        JFrame marioFrame = new JFrame("Mario Game 2");
        marioFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        marioFrame.setSize(800, 600);
        marioFrame.add(new MarioGamePanel2());
        marioFrame.setVisible(true);
    }

    public static void startMarioGame3() {
        JFrame marioFrame = new JFrame("Mario Game 3");
        marioFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        marioFrame.setSize(800, 600);
        marioFrame.add(new MarioGamePanel3());
        marioFrame.setVisible(true);
    }
}

