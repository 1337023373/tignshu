package com.atguigu.single;

import java.util.concurrent.TimeUnit;

public class PersonTest {
    public static void main(String[] args) {
//        调用类
        Person person = new Person();
//        创建线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                设置name
                person.setName("hengheng");
//               睡眠
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("线程1 == " + person.getName());
            }
        });
//        启动线程
        thread.start();

        //        创建线程
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
//                设置name
                person.setName("sama");
//               睡眠
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("线程2 == " + person.getName());
            }
        });
//        启动线程
        thread2.start();
    }
}
