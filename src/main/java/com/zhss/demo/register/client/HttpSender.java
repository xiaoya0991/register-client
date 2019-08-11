package com.zhss.demo.register.client;

import com.alibaba.fastjson.JSONObject;
import com.zhss.demo.register.cache.RegisterClentCache;
import com.zhss.demo.register.client.CachedServiceRegistry.RecentlyChangedServiceInstance;
import com.zhss.demo.register.http.HttpClientResult;
import com.zhss.demo.register.http.HttpClientUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 负责发送各种http请求的组件
 *
 * @author zhonghuashishan
 */
public class HttpSender {


    /***
     * 客户端缓存组件
     */
    private RegisterClentCache clentCache;

    /***
     *http请求组件
     */
    private HttpClient client = HttpClientBuilder.create().build();

    /**
     * 服务地址
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /***
     * 缓存
     */
    public HttpSender(String host,int port){
        this.clentCache = RegisterClentCache.getInstance();
        this.host = host;
        this.port = port;
    }


    /**
     * 服务进行注册
     * @param request
     * @param
     * @param
     * @return
     */
    public RegisterResponse register(RegisterRequest request) {

        Map<String,Object> map = new HashMap<>();
        map.put("hostname", request.getHostname());
        map.put("ip", request.getIp());
        map.put("port", request.getPort());
        map.put("serviceName", request.getServiceName());
        map.put("serviceInstanceId", request.getServiceInstanceId());

        RegisterResponse response = new RegisterResponse();

        try {

            HttpClientResult result = HttpClientUtils.doPost(this.getRequestUrl("register"), map);
            if (result.getCode()== HttpStatus.SC_OK){
                response.setStatus(RegisterResponse.SUCCESS);

            }

        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(RegisterResponse.FAILURE);
        }

        return response;
    }

    /**
     * 发送心跳请求
     *
     * @param request
     * @return
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest request) {
        System.out.println("服务实例【" + request + "】，发送请求进行心跳......");

        Map<String,Object> map = new HashMap<>();
        map.put("serviceName", request.getServiceName());
        map.put("serviceInstanceId", request.getServiceInstanceId());


        HeartbeatResponse response = new HeartbeatResponse();
        try {

            HttpClientResult result = HttpClientUtils.doPost(this.getRequestUrl("heartbeat"), map);
            if (result.getCode()== HttpStatus.SC_OK){
                response.setStatus(RegisterResponse.SUCCESS);
            }

        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(RegisterResponse.FAILURE);
        }

        return response;
    }

    /**
     * 全量拉取服务注册表
     *
     * @return
     */
    public Applications fetchServiceRegistry() {

        Map<String, Map<String, ServiceInstance>> registry = null;

        try {

            HttpClientResult result = HttpClientUtils.doGet(this.getRequestUrl("fetchFullRegistry"));
            if (result.getCode()== HttpStatus.SC_OK){
                JSONObject fullRegistry = JSONObject.parseObject(result.getContent());
                 registry = JSONObject.toJavaObject(fullRegistry, Map.class);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return new Applications(registry);

    }

    /**
     * 增量拉取服务注册表
     *
     * @return
     */
    public DeltaRegistry fetchDeltaRegistry() {
        LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue =
                new LinkedList<>();

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setHostname("order-service-01");
        serviceInstance.setIp("192.168.31.288");
        serviceInstance.setPort(9000);
        serviceInstance.setServiceInstanceId("ORDER-SERVICE-192.168.31.288:9000");
        serviceInstance.setServiceName("ORDER-SERVICE");

        RecentlyChangedServiceInstance recentlyChangedItem = new RecentlyChangedServiceInstance(
                serviceInstance,
                System.currentTimeMillis(),
                "register");

        recentlyChangedQueue.add(recentlyChangedItem);

        System.out.println("拉取增量注册表：" + recentlyChangedQueue);

        DeltaRegistry deltaRegistry = new DeltaRegistry(recentlyChangedQueue, 2L);

        try {
            HttpClientResult result = HttpClientUtils.doGet(this.getRequestUrl("fetchDeltaRegistry"));
            if (result.getCode()== HttpStatus.SC_OK){

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return deltaRegistry;

    }






    /***
     * 获取请求地址
     * @param requestType
     * @return
     */
    private String getRequestUrl(String requestType){

        StringBuilder stringBuilder = new StringBuilder();
        String url = stringBuilder.append(this.host).append(":").
                               append(this.port).append("/").append(requestType).toString();
        return url;

    }

    /**
     * 服务下线
     *
     * @param serviceName       服务名称
     * @param serviceInstanceId 服务实例id
     */
    public void cancel(String serviceName, String serviceInstanceId) {
        System.out.println("服务实例下线【" + serviceName + ", " + serviceInstanceId + "】");
    }

}
