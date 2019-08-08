package com.zhss.demo.register.client;

import java.util.LinkedList;

/**
 * 增量注册表
 *
 * @author wenliang
 */
public class DeltaRegistry {


    private LinkedList<CachedServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue;
    private Long serviceInstanceTotalCount;


    public DeltaRegistry(LinkedList<CachedServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue,
                         Long serviceInstanceTotalCount) {
        this.recentlyChangedQueue = recentlyChangedQueue;
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }

    public LinkedList<CachedServiceRegistry.RecentlyChangedServiceInstance> getRecentlyChangedQueue() {
        return recentlyChangedQueue;
    }

    public void setRecentlyChangedQueue(LinkedList<CachedServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue) {
        this.recentlyChangedQueue = recentlyChangedQueue;
    }

    public Long getServiceInstanceTotalCount() {
        return serviceInstanceTotalCount;
    }

    public void setServiceInstanceTotalCount(Long serviceInstanceTotalCount) {
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }
}
