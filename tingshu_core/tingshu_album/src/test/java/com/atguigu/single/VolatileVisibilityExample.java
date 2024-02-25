package com.atguigu.single;

public class VolatileVisibilityExample {
    private volatile boolean flag = false;

    public void toggleFlag() {
        flag = !flag;
    }

    public boolean getFlag() {
        return flag;
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileVisibilityExample example = new VolatileVisibilityExample();

        // 线程1：修改共享变量的值
        Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(1000); // 等待一段时间
                example.toggleFlag(); // 切换标志位
                System.out.println("Flag value changed by thread1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 线程2：读取共享变量的值
        Thread thread2 = new Thread(() -> {
            while (!example.getFlag()) {
                // 等待标志位变为 true
            }
            System.out.println("Flag value read by thread2: " + example.getFlag());
        });

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待线程执行完毕
        thread1.join();
        thread2.join();
    }
}
