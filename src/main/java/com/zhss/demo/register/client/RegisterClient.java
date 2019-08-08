package com.zhss.demo.register.client;

import java.util.UUID;

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
    private static final Long HEARTBEAT_INTERVAL = 30 * 1000L;

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


    public RegisterClient( String host, int port) {
        this.serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
        this.httpSender = new HttpSender();
        this.heartbeatWorker = new HeartbeatWorker();
        this.isRunning = true;
        this.registry = new CachedServiceRegistry(this, httpSender);
        this.host = host;
        this.port = port;
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
            // 应该是获取当前机器的信息
            // 包括当前机器的ip地址、hostname，以及你配置这个服务监听的端口号
            // 从配置文件里可以拿到
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setServiceName(SERVICE_NAME);
            registerRequest.setIp(IP);
            registerRequest.setHostname(HOSTNAME);
            registerRequest.setPort(PORT);
            registerRequest.setServiceInstanceId(serviceInstanceId);

            RegisterResponse registerResponse = httpSender.register(registerRequest,host, port);

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
            // 如果说注册成功了，就进入while true死循环
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            heartbeatRequest.setServiceName(SERVICE_NAME);
            heartbeatRequest.setServiceInstanceId(serviceInstanceId);

            HeartbeatResponse heartbeatResponse = null;

            while (isRunning()) {
                try {
                    heartbeatResponse = httpSender.heartbeat(heartbeatRequest);
                    System.out.println("心跳的结果为：" + heartbeatResponse.getStatus() + "......");
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (Exception e) {
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
