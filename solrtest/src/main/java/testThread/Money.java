package testThread;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Money {
    //private volatile AtomicInteger money=new AtomicInteger(100);

    private volatile int money;

    private static Money Money;

    private final ReentrantLock lock=new ReentrantLock();

    private final ThreadLocal threadLocal = new ThreadLocal();

    private Money() {
    }

    private Money(int money) {
        this.money = money;
    }

    public static Money getMoney(){
        synchronized ("1"){
            if (Money==null){
                Money = new Money();
            }
        }
        return Money;
    }

    public   double getMoneyc() {
        /*try{
            lock.lock();
            return money--;
        }finally {
            lock.unlock();
        }*/

        return Math.random();
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
