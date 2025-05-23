package MarioGame;

import java.awt.*;

public class Player {
    private int x, y;
    private int velocityY;
    private boolean isJumping;
    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = 15;
    private static final int WIDTH = 40, HEIGHT = 50;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.velocityY = 0;
        this.isJumping = false;
    }

    // 获取马里奥的位置
    public int getX() { return x; }
    public int getY() { return y; }

    // 设置马里奥的位置
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 更新马里奥的垂直位置
    public void update() {
        if (isJumping) {
            velocityY += GRAVITY;
            y += velocityY;
        }
    }

    // 跳跃方法
    public void jump() {
        if (!isJumping) {
            velocityY = -JUMP_STRENGTH;
            isJumping = true;
        }
    }

    // 停止跳跃
    public void stopJumping() {
        isJumping = false;
    }

    // 公开的绘制马里奥方法
    public void drawMario(Graphics2D g2, int x, int y) {
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

    // 检查与平台的碰撞
    public void checkCollisions(int[][] platforms) {
        for (int[] platform : platforms) {
            int platformX = platform[0];
            int platformY = platform[1];
            int platformWidth = platform[2];
            int platformHeight = platform[3];

            // 检查马里奥是否站在台阶上
            if (x + WIDTH > platformX && x < platformX + platformWidth &&
                    y + HEIGHT >= platformY && y + HEIGHT <= platformY + platformHeight) {
                y = platformY - HEIGHT; // 站在台阶上
                velocityY = 0;
                isJumping = false;
            }
        }
    }
}
