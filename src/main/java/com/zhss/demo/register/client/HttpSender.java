package com.zhss.demo.register.client;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhss.demo.register.cache.RegisterClentCache;
import com.zhss.demo.register.client.CachedServiceRegistry.RecentlyChangedServiceInstance;
import com.zhss.demo.register.core.RegisterClientManagement;
import com.zhss.demo.register.http.HttpClientResult;
import com.zhss.demo.register.http.HttpClientUtils;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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



    /***
     * 注册中心客户端管理组件
     */
    private RegisterClientManagement register = RegisterClientManagement.getInstance();


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

        try {

            HttpClientResult result = HttpClientUtils.doPost(this.getRequestUrl("register"), map);
            if (result.getCode()== HttpStatus.SC_OK){
                System.out.println("ddd");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        // 收到register-server响应之后，封装一个Response对象
        RegisterResponse response = new RegisterResponse();
        response.setStatus(RegisterResponse.SUCCESS);

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
        try {

            HttpClientResult result = HttpClientUtils.doPost(this.getRequestUrl("heartbeat"), map);
            if (result.getCode()== HttpStatus.SC_OK){

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        HeartbeatResponse response = new HeartbeatResponse();
        response.setStatus(RegisterResponse.SUCCESS);

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
                new LinkedList<RecentlyChangedServiceInstance>();

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
            HttpResponse response = client.execute(this.getRequest(this.getRequestUrl("fetchDeltaRegistry")));
            
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
