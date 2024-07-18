package cn.dyx.test;

import cn.dyx.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dyx
 * @description 单元测试
 * @create 2024/7/18 9:53
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private RTopic dynamicThreadPoolRedisTopic;

    @Resource(name = "threadPoolExecutor01")
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void test_dynamicThreadPoolRedisTopic() throws InterruptedException {
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity("dynamic-thread-pool-test-app", "threadPoolExecutor01");
        threadPoolConfigEntity.setPoolSize(100);
        threadPoolConfigEntity.setMaximumPoolSize(100);
        dynamicThreadPoolRedisTopic.publish(threadPoolConfigEntity);

        new CountDownLatch(1).await();
    }

    @Test public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            threadPoolExecutor.execute(()->{
                try {
                    TimeUnit.SECONDS.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        new CountDownLatch(1).await();
    }


}
