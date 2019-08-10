package com.zhss.demo.register;

import com.zhss.demo.register.client.RegisterClient;
import com.zhss.demo.register.client.RegisterRequest;

import static com.zhss.demo.register.client.RegisterClient.*;

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

    public static void main(String[] args) {

        init();

    }

    /**
     * 初始化
     */
    public static void init(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setServiceName(SERVICE_NAME);
        registerRequest.setIp(IP);
        registerRequest.setHostname(HOSTNAME);
        registerRequest.setPort(PORT);
        registerRequest.setServiceInstanceId(registerRequest.getServiceInstanceId());
        String url = "http://localhost";
        RegisterClient registerClient = new RegisterClient(url,8888,registerRequest);
        System.out.println("注册中心初始化开始");
        registerClient.start();
    }


}
