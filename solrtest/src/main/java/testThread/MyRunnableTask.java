package testThread;

import java.util.concurrent.locks.ReentrantLock;

public class MyRunnableTask implements Runnable {
    /*ReentrantLock lock = new ReentrantLock();*/
    public void run() {
      /* while (!Thread.currentThread().isInterrupted()){
           System.out.println(Thread.currentThread().getName()+"运行");
           System.out.println(Thread.currentThread().isInterrupted());
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           System.out.println("1s后");
           Thread.currentThread().interrupt();
           boolean interrupted = Thread.currentThread().isInterrupted();
           System.out.println(Thread.currentThread().isInterrupted());


       }*/

        Money money = Money.getMoney();


        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName()+"的钱是"+money.getMoneyc());
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        System.out.println(Thread.currentThread().getName()+"run方法结束");



    }
}
