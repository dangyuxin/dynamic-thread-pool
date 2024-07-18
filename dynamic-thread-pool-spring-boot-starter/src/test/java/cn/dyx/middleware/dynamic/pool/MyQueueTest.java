package cn.dyx.middleware.dynamic.pool;

import cn.dyx.middleware.dynamic.thread.pool.sdk.common.queue.MyDynamicLinkedBlockingQueue;
import io.micrometer.core.instrument.util.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * @author dyx
 * @description 自己队列测试
 * @create 2024/7/18 15:55
 */

public class MyQueueTest {
    private static ThreadPoolExecutor buildThreadPool() {
        return new ThreadPoolExecutor(
                2,
                5,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                new NamedThreadFactory("dyx")
        );
    }

    public static  void printThreadPoolProperties(ThreadPoolExecutor executor) {
        System.out.println(Thread.currentThread().getName());
        System.out.println("核心线程数: " + executor.getCorePoolSize());
        System.out.println("最大线程数: " + executor.getMaximumPoolSize());
        System.out.println("当前活动线程数: " + executor.getActiveCount());
        System.out.println("任务完成数(completedTaskCount): " + executor.getCompletedTaskCount());
        System.out.println("队列大小(queueSize): " + executor.getQueue().size());
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();

        ThreadPoolExecutor threadPoolExecutor = buildThreadPool();
        try {
            MyDynamicLinkedBlockingQueue queue = (MyDynamicLinkedBlockingQueue)threadPoolExecutor.getQueue();
            queue.setCapacity(8);
        }catch (ClassCastException e){
            System.out.println("使用队列无法扩容 "+e.getMessage());
        }
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(()->{
                printThreadPoolProperties(threadPoolExecutor);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }


        threadPoolExecutor.shutdown();
        try {
            // 等待所有任务执行完毕，或者超时退出
            threadPoolExecutor.awaitTermination(100,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("主线程被中断");
            Thread.currentThread().interrupt();
        }

        System.out.println("子线程执行完毕，主线程继续执行");
        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        double durationInSeconds = (double) durationInNano / 1_000_000_000.0;

        System.out.println("执行时间 (纳秒): " + durationInNano);
        System.out.println("执行时间 (秒): " + durationInSeconds);
    }
}
