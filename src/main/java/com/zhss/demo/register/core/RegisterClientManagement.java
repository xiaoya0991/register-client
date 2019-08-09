package com.zhss.demo.register.core;

import com.zhss.demo.register.client.ServiceInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册中心客户端管理组件
 * @author wenliang
 */
public class RegisterClientManagement {


    /***
     * 客户端注册表
     */
    Map<String, Map<String, ServiceInstance>> registry;

    /**
     *
     */
    private static volatile RegisterClientManagement instance = null;


    private RegisterClientManagement(){
        this.registry =  new HashMap<>();

    }


    /**
     * 获取单例
     * @return
     */
    public static RegisterClientManagement getInstance(){
        if (instance == null){
            synchronized (RegisterClientManagement.class){
                if (instance == null){
                    instance = new RegisterClientManagement();
                }
            }
        }
        return instance;
    }



}
