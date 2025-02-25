package cn.dyx.middleware.dynamic.thread.pool.sdk.trigger.listener;

import cn.dyx.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import cn.dyx.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.dyx.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import com.alibaba.fastjson.JSON;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author dyx
 * @description 动态线程池变更监听
 * @create 2024/7/18 16:46
 */
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    private Logger logger = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolConfigAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("动态线程池，调整线程池配置。线程池名称:{} 核心线程数:{} 最大线程数:{} 最大队列数{}", threadPoolConfigEntity.getThreadPoolName(),
                threadPoolConfigEntity.getCorePoolSize(), threadPoolConfigEntity.getMaximumPoolSize(),
                threadPoolConfigEntity.getRemainingCapacity());

        try {
            dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);
        }catch (ClassCastException e){
            logger.error("使用队列无法扩容 {}",e.getMessage());
            return;
        }catch (IllegalArgumentException e){
            logger.error("队列容量不可少于当前队列中任务数量{}",e.getMessage());
            return;
        }


        // 更新后上报最新数据
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);

        ThreadPoolConfigEntity threadPoolConfigEntityCurrent = dynamicThreadPoolService.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntityCurrent);
        logger.info("动态线程池，上报线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
    }

}
