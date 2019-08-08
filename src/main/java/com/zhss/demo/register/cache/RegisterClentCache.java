package com.zhss.demo.register.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 *
 * 客户端缓存
 * @author wenliang
 */
public class RegisterClentCache {


    private static volatile RegisterClentCache instance = null;

    /**
     * 缓存
     */
    private Cache<String, Object> cache;


    /**
     *
     */
    private RegisterClentCache(){
        this.cache=this.init();
    }


    /**
     *
     * @return
     */
    public static RegisterClentCache getInstance(){
        if (instance == null){
            synchronized (RegisterClentCache.class){
                if (instance== null){
                    instance = new RegisterClentCache();
                }
            }
        }
        return instance;
    }


    /**
     * 初始化缓存
     * @return
     */
    public Cache<String, Object> init(){
        Cache<String, Object> cache = CacheBuilder.newBuilder().
                concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .initialCapacity(500)
                .maximumSize(1000)
                .build();
        return cache;
    }


    /**
     *
     * @param key
     * @param value
     */
    public void put(String key,Object value){
        this.cache.put(key, value);
    }


    /**
     *
     * @param key
     * @return
     */
    public Object get(String key){
       return   cache.getIfPresent(key);

    }


}
