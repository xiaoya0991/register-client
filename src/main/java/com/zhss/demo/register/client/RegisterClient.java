package com.zhss.demo.register.client;

import java.util.*;

/**
 * 在服务上被创建和启动，负责跟register-server进行通信
 *
 * @author zhonghuashishan
 */
public class RegisterClient {

    public static final String SERVICE_NAME = "inventory-service";
    public static final String IP = "192.168.31.207";
    public static final String HOSTNAME = "inventory01";
    public static final int PORT = 9000;
    //private static final Long HEARTBEAT_INTERVAL = 30 * 1000L;

    /**
     * 服务实例id
     */
    private  String serviceInstanceId;
    /**
     * http通信组件
     */
    private HttpSender httpSender;
    /**
     * 心跳线程
     */
    private HeartbeatWorker heartbeatWorker;
    /**
     * 服务实例是否在运行
     */
    private volatile Boolean isRunning;
    /**
     * 客户端缓存的注册表
     */
    private CachedServiceRegistry registry;

    private   String host;

    private int port;

    /***
     *
     */
    private RegisterRequest registerRequest;








    /**
     *
     * @param host
     * @param port
     * @param registerRequest
     */
    public RegisterClient( String host, int port,RegisterRequest registerRequest) {
        this.serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
        this.httpSender = new HttpSender(host,port);
        this.heartbeatWorker = new HeartbeatWorker();
        this.isRunning = true;
        this.registry = new CachedServiceRegistry(this, httpSender);
        this.host = host;
        this.port = port;
        this.registerRequest = registerRequest;
    }




    /**
     * 启动ReigsterClient组件
     */
    public void start() {
        try {
            RegisterWorker registerWorker = new RegisterWorker();
            registerWorker.start();
            registerWorker.join();

            heartbeatWorker.start();

            this.registry.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止RegisterClient组件
     */
    public void shutdown() {
        this.isRunning = false;
        this.heartbeatWorker.interrupt();
        this.registry.destroy();
        this.httpSender.cancel(SERVICE_NAME, serviceInstanceId);
    }

    /**
     * 服务注册线程
     *
     * @author zhonghuashishan
     */
    private class RegisterWorker extends Thread {

        @Override
        public void run() {

            RegisterResponse registerResponse = httpSender.register(registerRequest);

            System.out.println("服务注册的结果是：" + registerResponse.getStatus() + "......");
        }

    }



    /**
     * 心跳线程
     *
     * @author zhonghuashishan
     */
    private class HeartbeatWorker extends Thread {

        @Override
        public void run() {


            HeartbeatResponse heartbeatResponse = null;
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            heartbeatRequest.setServiceName(SERVICE_NAME);
            heartbeatRequest.setServiceInstanceId("00000000000000000001");

            while (isRunning()) {

                try {

                    heartbeatResponse = httpSender.heartbeat(heartbeatRequest);

                    System.out.println("发送心跳：" + heartbeatRequest.getServiceName() + heartbeatRequest.getServiceInstanceId());


                    Thread.sleep(3000);

                }catch (Exception e){
                    e.printStackTrace();
                }

                }
            }
        }





    /**
     * 返回RegisterClient是否正在运行
     *
     * @return
     */
    public Boolean isRunning() {
        return isRunning;
    }





}
