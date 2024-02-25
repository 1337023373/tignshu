package com.atguigu.single;

public class Singleton {
//    创建无参构造
    public Singleton() {}

    //    创建私有属性
    private static volatile Singleton instance;

    public  Singleton getInstance() {
//        首先判断instance是否被创建,创建就直接返回实例,没有再创建
        if (instance == null) {
//            确保一次只有一个线程能进入创建
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;

    }
}
