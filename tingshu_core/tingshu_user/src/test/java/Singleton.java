import lombok.Data;

@Data
public class Singleton {
    private static Singleton instance = new Singleton();
    private Singleton() {
    }
    public static Singleton getInstance() {
        return instance;
    }

//    生成一个100的循环
public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                Singleton instance = Singleton.getInstance();
                System.out.println(instance);
            }).start();
        }
    }
}
