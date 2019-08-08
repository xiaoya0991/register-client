package com.zhss.demo.register.http;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author wenliang
 */
public class HttpClientUtil {



    /**
     * 发送GET请求
     * @param path
     * @param parametersBody
     * @return
     * @throws Exception
     */
    public static String getRequest(String path, List<NameValuePair> parametersBody) throws Exception{
        URIBuilder uriBuilder = new URIBuilder(path);
        uriBuilder.setParameters(parametersBody);
        HttpGet get = new HttpGet(uriBuilder.build());
        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(get);
            int code = response.getStatusLine().getStatusCode();
            if (code >= 400) {
                throw new RuntimeException((new StringBuilder()).append("Could not access protected resource. Server returned http code: ").append(code).toString());
            }
            return EntityUtils.toString(response.getEntity());
        }
        catch (ClientProtocolException e) {

        }
        catch (IOException e) {

        }
        finally {
            get.releaseConnection();
        }
        return path;
    }



    /**
     * 发送POST请求（普通表单形式）
     * @param path
     * @param parametersBody
     * @return
     */
    public static String postForm(String path, List<NameValuePair> parametersBody) {
        HttpEntity entity = new UrlEncodedFormEntity(parametersBody, Charsets.UTF_8);
        return postRequest(path, "application/x-www-form-urlencoded", entity);
    }




    /***
     * 发送POST请求（JSON形式）
     * @param path
     * @param json
     * @return
     */
    public static String postJSON(String path, String json)  {
        StringEntity entity = new StringEntity(json, Charsets.UTF_8);
        return postRequest(path, "application/json", entity);
    }



    /**
     * 发送POST请求
     * @param path
     * @param mediaType
     * @param entity
     * @return
     */
    public static String postRequest(String path, String mediaType, HttpEntity entity) {

        HttpPost post = new HttpPost(path);
        post.addHeader("Content-Type", mediaType);
        post.addHeader("Accept", "application/json");
        post.setEntity(entity);
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code >= 400) {
                return EntityUtils.toString(response.getEntity());
            }
        }
        catch (ClientProtocolException e) {

        }
        catch (IOException e) {

        }
        finally {
            post.releaseConnection();
        }

        return null;

    }
}

