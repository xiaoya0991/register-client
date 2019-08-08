package com.zhss.demo.register;

import com.zhss.demo.register.client.RegisterClient;

/**
 * @author wenliang
 */
public class Register {


    private String host;

    private int port;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }




    public Register(){

    }

    /**
     * 初始化
     */
    public void init(){
        RegisterClient registerClient = new RegisterClient(this.host,this.port);
        System.out.println("注册中心初始化开始");
        registerClient.start();
    }














}
