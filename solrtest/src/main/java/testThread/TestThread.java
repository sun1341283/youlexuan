package testThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestThread {

    public static void main(String[] args) {
        //创建实现了Runnable的任务
        MyRunnableTask myRunnableTask1 = new MyRunnableTask();
        MyRunnableTask myRunnableTask2 = new MyRunnableTask();
        MyRunnableTask myRunnableTask3 = new MyRunnableTask();


        //线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        //提交任务
        executorService.submit(myRunnableTask1);

        executorService.submit(myRunnableTask2);
        executorService.submit(myRunnableTask3);

        System.out.println(Thread.currentThread().getName()+"运行");






    }
}
