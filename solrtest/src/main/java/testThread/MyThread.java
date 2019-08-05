package testThread;

public class MyThread implements Runnable {

    public void run() {
        System.out.println(Thread.currentThread().getName()+"正在运行");
    }
}
