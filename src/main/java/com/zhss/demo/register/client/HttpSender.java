package com.zhss.demo.register.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhss.demo.register.cache.RegisterClentCache;
import com.zhss.demo.register.client.CachedServiceRegistry.RecentlyChangedServiceInstance;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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


    private HttpPost httpPost;


    /***
     * 缓存
     */
    public HttpSender(){
        this.clentCache = RegisterClentCache.getInstance();
        this.httpPost = new HttpPost();
    }


    /**
     * 服务进行注册
     * @param request
     * @param
     * @param
     * @return
     */
    public RegisterResponse register(RegisterRequest request) {


        StringBuilder stringBuilder = new StringBuilder();
        String url = stringBuilder.append(request.getIp()).append(":").append(request.getPort()).append("/").append("register").toString();

        HttpPost post = new HttpPost(url);

        Map<String,Object> map = new HashMap<>();
        map.put("hostname", request.getHostname());
        map.put("ip", request.getIp());
        map.put("port", request.getPort());
        map.put("serviceName", request.getServiceName());
        map.put("serviceInstanceId", request.getServiceInstanceId());
        Gson gson = new Gson();
        String json = gson.toJson(map, new TypeToken<Map<String, String>>() {}.getType());
        post.setEntity(new StringEntity(json, Charsets.UTF_8));
        post.addHeader("Content-Type", "application/json");
        try {
            HttpResponse response = client.execute(post);

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



        String url = "http://localhost:8888/heartbeat";
        HttpPost post = new HttpPost(url);

        Map<String,String> map = new HashMap<String,String>();
        map.put("hostname", "admin");
        map.put("ip", "123456");
        map.put("port", "123456");
        map.put("type", "123456");
        map.put("serviceName", "123456");
        map.put("serviceInstanceId", "123456");
        Gson gson = new Gson();
        String json = gson.toJson(map, new TypeToken<Map<String, String>>() {}.getType());
        post.setEntity(new StringEntity(json, Charsets.UTF_8));
        post.addHeader("Content-Type", "application/json");

        try {
            HttpResponse response = client.execute(post);
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


        Map<String, Map<String, ServiceInstance>> registry =
                new HashMap<String, Map<String, ServiceInstance>>();

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setHostname("finance-service-01");
        serviceInstance.setIp("192.168.31.1207");
        serviceInstance.setPort(9000);
        serviceInstance.setServiceInstanceId("FINANCE-SERVICE-192.168.31.207:9000");
        serviceInstance.setServiceName("FINANCE-SERVICE");

        Map<String, ServiceInstance> serviceInstances = new HashMap<String, ServiceInstance>();
        serviceInstances.put("FINANCE-SERVICE-192.168.31.207:9000", serviceInstance);

        registry.put("FINANCE-SERVICE", serviceInstances);

        System.out.println("拉取注册表：" + registry);

        String url = "http://localhost:8888/fetchFullRegistry";
        HttpGet get = new HttpGet(url);

        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());

        } catch (IOException e) {
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



        String url = "http://localhost:8888/fetchDeltaRegistry";
        HttpGet get = new HttpGet(url);

        try {
            HttpResponse response = client.execute(get);
            
        } catch (IOException e) {
            e.printStackTrace();
        }


        return deltaRegistry;


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
