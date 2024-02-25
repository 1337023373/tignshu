package com.atguigu.single;

public class VolatileDemo {
    static  Integer  flag = 1;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag == 1) {

                }
                System.out.println("子线程结束" + flag);
            }
        },"A").start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        flag = 2;
        System.out.println("主线程" + flag);
    }

}
