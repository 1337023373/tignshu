package com.atguigu.single;

public class Person {
//   创建当前线程
   ThreadLocal<String> name =  new ThreadLocal<>();

    public String getName() {
        return this.name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
    public void remove() {
        this.name.remove();
    }
}
