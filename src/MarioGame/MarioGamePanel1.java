package MarioGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;

class MarioGamePanel1 extends JPanel implements KeyListener, ActionListener {
    private Clip backgroundClip;  // 声明 backgroundClip
    private int marioX = 50; // 马里奥的X坐标
    private int marioY = 350; // 马里奥的Y坐标
    private int marioVelocityY = 0; // 垂直速度
    private boolean isJumping = false; // 是否正在跳跃
    private final int GRAVITY = 1; // 重力加速度
    private final int JUMP_STRENGTH = 15; // 跳跃初速度
    private Timer timer;
    private int lives = 3; // 马里奥的初始生命值
    private int startFallingY = -1; // 记录开始下落的Y坐标
    private final int FALL_DAMAGE_HEIGHT = 250; // 高度阈值
    private boolean isFalling = false; // 是否在摔落状态
    private boolean gameWon = false; // 游戏是否胜利
    private boolean gameOver = false; // 游戏是否失败
    private final int[][] platforms = {
            {100, 300, 100, 20}, // {x, y, width, height}
            {250, 250, 100, 20},
            {400, 200, 100, 20},
            {550, 150, 100, 20},
            {700, 150, 100, 20},
    }; // 台阶数组
    private final int levelEndX = 700; // 关卡终点
    private final int levelEndY = 150; // 关卡终点

    private final int[][] coins = {
            {130, 220}, // 每个金币的位置 {x, y}，放在台阶的上方
            {280, 170},
            {430, 120},
            {580, 70},
            {730, 350}
    };
    private boolean[] coinCollected = new boolean[coins.length]; // 记录金币是否被收集
    private JButton toggleMusicButton;
    private boolean isMusicPlaying = true;




    public MarioGamePanel1() {
        setFocusable(true);
        addKeyListener(this);
        setLayout(null); // 关闭布局管理器（允许按钮自由定位）

        // 播放背景音乐（循环播放）
        playBackgroundMusic("src/MarioGame/background_music.wav");

        // 添加音乐开关按钮
        toggleMusicButton = new JButton("BGM OFF");
        toggleMusicButton.setBounds(20, 500, 100, 30); // 设置按钮位置和大小
        toggleMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isMusicPlaying) {
                    stopBackgroundMusic();
                    toggleMusicButton.setText("BGM ON");
                } else {
                    playBackgroundMusic("src/MarioGame/background_music.wav");
                    toggleMusicButton.setText("BGM OFF");
                }
                isMusicPlaying = !isMusicPlaying;

                // 重新聚焦，防止按钮抢走键盘焦点
                requestFocusInWindow();
            }
        });

        add(toggleMusicButton); // 添加按钮到面板中

        timer = new Timer(20, this); // 每20毫秒刷新一次
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 绘制背景
        g2.setColor(Color.CYAN);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 绘制地面
        g2.setColor(Color.GREEN);
        g2.fillRect(0, 400, getWidth(), 200);

        // 绘制生命值（爱心）
        drawHearts(g2);

        // 绘制台阶
        g2.setColor(Color.ORANGE); // 设置台阶颜色
        for (int[] platform : platforms) {
            int platformX = platform[0];
            int platformY = platform[1];
            int platformWidth = platform[2];
            int platformHeight = platform[3];
            g2.fillRect(platformX, platformY, platformWidth, platformHeight);
        }

        // 绘制金币
        g2.setColor(Color.YELLOW);
        for (int i = 0; i < coins.length; i++) {
            if (!coinCollected[i]) { // 仅绘制未收集的金币
                int coinX = coins[i][0];
                int coinY = coins[i][1];
                g2.fillOval(coinX, coinY, 30, 30); // 金币为黄色小圆点
            }
        }

        // 绘制马里奥
        drawMario(g2, marioX, marioY);

        // 如果游戏胜利，显示胜利消息
        if (gameWon) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.drawString("You Win!", getWidth() / 2 - 80, getHeight() / 2);
        }

        // 如果生命值为0，显示游戏失败消息
        if (lives == 0) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.drawString("Game Over!", getWidth() / 2 - 100, getHeight() / 2);
            gameOver=true;
        }
    }

    private void drawHearts(Graphics2D g2) {
        int heartX = 20; // 爱心起始X位置
        int heartY = 20; // 爱心Y位置
        int heartSpacing = 40; // 每个爱心之间的间隔

        g2.setColor(Color.RED);

        for (int i = 0; i < lives; i++) {
            int x = heartX + i * heartSpacing;

            // 绘制爱心 (两个圆 + 一个三角形组成)
            g2.fillOval(x, heartY, 20, 20); // 左侧圆
            g2.fillOval(x + 15, heartY, 20, 20); // 右侧圆
            g2.fillPolygon(new int[]{x, x + 20, x + 35},
                    new int[]{heartY + 15, heartY + 45, heartY + 15}, 3); // 三角形
        }
    }

    private void drawMario(Graphics2D g2, int x, int y) {
        // 1. 绘制肉色脸庞
        g2.setColor(new Color(255, 220, 177)); // 肉色
        g2.fillOval(x + 10, y - 30, 30, 30); // 脸部的基础轮廓

        // 2. 绘制耳朵
        g2.setColor(new Color(255, 220, 177)); // 肉色
        g2.fillOval(x + 5, y - 25, 10, 15); // 左耳
        g2.fillOval(x + 35, y - 25, 10, 15); // 右耳

        // 3. 绘制头和帽子之间的深棕色头发
        g2.setColor(new Color(139, 69, 33)); // 深棕色
        g2.fillArc(x + 10, y - 32, 30, 20, 0, 180); // 头发在帽子下方

        // 4. 绘制浅棕色的鼻子
        g2.setColor(new Color(210, 180, 140)); // 浅棕色
        g2.fillOval(x + 20, y - 22, 10, 8); // 圆形鼻子略微突出

        // 5. 绘制深棕色的胡子
        g2.setColor(new Color(139, 69, 19)); // 深棕色
        g2.fillArc(x + 17, y - 20, 16, 10, 0, -180); // 胡子位于鼻子下方

        // 6. 绘制和善的表情
        // 绘制眼睛
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 17, y - 25, 5, 5); // 左眼
        g2.fillOval(x + 28, y - 25, 5, 5); // 右眼

        // 绘制微笑的嘴巴
        g2.setColor(Color.BLACK);
        g2.drawArc(x + 18, y - 15, 14, 8, 0, -180); // 微笑弧线

        // 7. 绘制红色内衣
        g2.setColor(Color.RED);
        g2.fillRect(x + 5, y, 40, 35); // 增宽身子

        // 8. 绘制蓝色背带裤
        g2.setColor(Color.BLUE);
        g2.fillRect(x + 5, y + 20, 40, 25); // 提高背带裤位置

        // 9. 绘制黄色纽扣
        g2.setColor(Color.YELLOW);
        g2.fillOval(x + 15, y + 25, 7, 7); // 调整纽扣位置和大小
        g2.fillOval(x + 28, y + 25, 7, 7);

        // 10. 绘制棕色鞋子
        g2.setColor(new Color(139, 69, 19)); // 棕色
        g2.fillRect(x + 10, y + 45, 12, 12); // 调整鞋子位置和宽度
        g2.fillRect(x + 28, y + 45, 12, 12);

        // 11. 绘制红色帽子（确保在最后绘制帽子）
        g2.setColor(Color.RED);
        g2.fillArc(x + 7, y - 40, 35, 25, 0, 180); // 帽子覆盖脸部上方

        // 11.1 绘制白色圆形（帽子中心标志）
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 18, y - 39, 14, 14); // 白色圆形位于帽子中间

        // 11.2 绘制红色的“M”
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 10)); // 设置字体
        g2.drawString("M", x + 22, y - 28); // 在白色圆形内绘制红色的“M”

        // 12. 绘制手（肉色）
        g2.setColor(new Color(255, 220, 177)); // 肉色
        g2.fillOval(x - 5, y + 20, 15, 15); // 左手
        g2.fillOval(x + 40, y + 20, 15, 15); // 右手

        // 13. 绘制手套（白色）
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 7, y + 23, 13, 13); // 左手套
        g2.fillOval(x + 42, y + 23, 13, 13); // 右手套
    }

    private void checkCollisions() {
        for (int[] platform : platforms) {
            int platformX = platform[0];
            int platformY = platform[1];
            int platformWidth = platform[2];
            int platformHeight = platform[3];

            // 检查马里奥是否站在台阶上
            if (marioX + 40 > platformX && marioX < platformX + platformWidth &&
                    marioY + 50 >= platformY && marioY + 50 <= platformY + platformHeight) {
                marioY = platformY - 50; // 站在台阶上
                marioVelocityY = 0;
                isJumping = false;
            }
        }
    }

    private void checkCoinCollection() {
        for (int i = 0; i < coins.length; i++) {
            if (!coinCollected[i]) { // 仅检测未收集的金币
                int coinX = coins[i][0];
                int coinY = coins[i][1];

                // 检查马里奥是否触碰到金币
                if (marioX + 20 > coinX && marioX < coinX + 15 &&
                        marioY + 50 > coinY && marioY < coinY + 15) {
                    coinCollected[i] = true; // 标记为已收集
                    playSound("coin.wav"); // 播放吃金币音效
                }
            }
        }
    }


    private void checkWinCondition() {
        boolean allCoinsCollected = true;
        for (boolean collected : coinCollected) {
            if (!collected) {
                allCoinsCollected = false;
                break;
            }
        }

        if (allCoinsCollected && marioX >= levelEndX && marioY <= levelEndY) {
            gameWon = true;
            timer.stop(); // 停止游戏循环
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 模拟重力影响
        if (marioY < 350 ) { // 如果马里奥在空中
            marioVelocityY += GRAVITY; // 重力作用
            if (marioVelocityY==0 && marioY<=200) { // 检测是否刚开始下落
                startFallingY = marioY; // 记录开始下落的位置
                isFalling = true; // 标记下落开始
                System.out.println("开始下落：startFallingY = " + startFallingY);
            }
        }

        // 更新马里奥的位置
        marioY += marioVelocityY;
        if(marioVelocityY!=0)
            System.out.println("更新马里奥速度 marioVelocityY = " + marioVelocityY);

        // 检查是否触地
        if (marioY >= 350) { // 马里奥触地时
            marioY = 350; // 重置到地面高度
            marioVelocityY = 0; // 重置垂直速度

            if (isFalling) { // 仅在下落结束时计算距离
                int fallDistance = Math.abs(startFallingY - 350); // 计算下落距离
                System.out.println("下落结束：fallDistance = " + fallDistance);

                if (fallDistance > FALL_DAMAGE_HEIGHT) { // 如果下落距离超过阈值
                    lives--; // 生命值减少
                    playSound("removeheart.wav"); // 播放跳跃音效
                    System.out.println("生命值减少：lives = " + lives);
                    if (lives <= 0) { // 检查生命值是否耗尽
                        playSound("gameover.wav"); // 播放死亡音效
                        System.out.println("生命值耗尽，游戏结束！");
                        lives = 0;
                        timer.stop(); // 停止游戏
                    }
                }
                isFalling = false; // 重置下落状态
            }
            isJumping = false; // 结束跳跃状态
        }

        repaint(); // 更新UI（包括心形图标）

        // 检查与台阶的碰撞
        checkCollisions();

        // 检查金币收集
        checkCoinCollection();

        // 检查胜利条件
        checkWinCondition();

        // 刷新界面
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // 左右移动
        if (key == KeyEvent.VK_LEFT) {
            marioX -= 10;
        }
        else if (key == KeyEvent.VK_RIGHT) {
            marioX += 10;
        }

        // 跳跃
        if (key == KeyEvent.VK_SPACE && !isJumping) {
            marioVelocityY = -JUMP_STRENGTH; // 设置向上的初速度
            isJumping = true;
            playSound("jump.wav"); // 播放跳跃音效
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void playBackgroundMusic(String musicFile) {
        try {
            File music = new File(musicFile);
            if (!music.exists()) {
                System.err.println("音频文件未找到：" + music.getAbsolutePath());
                return;
            }

            stopBackgroundMusic(); // 先停止之前的音乐

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(music);
            backgroundClip = AudioSystem.getClip(); // 使用类成员变量
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public void playSound(String soundFile) {
        try {
            File sound = new File("src/MarioGame/" + soundFile);
            AudioInputStream originalStream = AudioSystem.getAudioInputStream(sound);

            // 定义目标格式（44100 Hz, 16-bit, Stereo, Little-endian）
            AudioFormat originalFormat = originalStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100,
                    16,
                    originalFormat.getChannels(),
                    originalFormat.getChannels() * 2,
                    44100,
                    false
            );

            // 转换音频流
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);

            // 播放音频
            Clip clip = AudioSystem.getClip();
            clip.open(convertedStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

}
