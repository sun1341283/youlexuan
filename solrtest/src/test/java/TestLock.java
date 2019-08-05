import org.junit.Test;

public class TestLock {

    @Test
    public void ThreadTest(){
        MyThread myThread1 = new MyThread();
        myThread1.run();
    }
}
