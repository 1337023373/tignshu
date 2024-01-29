package com.atguigu.util;

public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void millis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
