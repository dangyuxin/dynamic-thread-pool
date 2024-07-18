package cn.dyx.middleware.dynamic.thread.pool.sdk.registry;

import cn.dyx.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author dyx
 * @description 注册中心接口
 * @create 2024/7/18 11:48
 */
public interface IRegistry {

    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolEntities);

    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);

}

