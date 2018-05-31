package com.github.microkibaco.taxi;

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestOkHttp3 {

    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    @Test
    public void testGet() {
        // 创建 OkHttpClient 对象
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();

        //  OkHttpClient 执行 Request
        try {
            final Response response = client.newCall(request).execute();
            System.out.println("response: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testPost() {
        // 创建 OkHttpClient 对象
        final OkHttpClient client = new OkHttpClient();

        final RequestBody body = RequestBody.create(JSON, "{\"name\":\"MicroKibaco\"}");

        final Request request = new Request.Builder()
                .url("http://httpbin.org/post")  // 请求行
                // .header() // 请求头
                .post(body) // 请求体
                .build();

        try {
            final Response response = client.newCall(request).execute();
            System.out.println("response: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 测试缓存
     */
    @Test
    public void testCache() {

        // 创建缓存对象
        final Cache cache = new Cache(new File("cache.cache"), 1024 * 1024);

        // 创建 OkHttpClient 对象
        final OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        final Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();

        //  OkHttpClient 执行 Request
        try {
            final Response response = client.newCall(request).execute();
            final Response cacheResponse = response.cacheResponse();
            final Response netResponse = response.networkResponse();
            if (cacheResponse != null) {
                // 从缓存响应
                System.out.println("cacheResponse: " + cacheResponse.body().string());
            }
            if (netResponse != null) {
                // 从网络响应
                System.out.println("response from net");
            }
            System.out.println("response: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 测试拦截器:
     * 拦截器就是所有的请求和所有的响应,它都能截获到,
     * 统计请求花费的时间和日志,拦截器是一种面向切面 IOP的思想
     */
    @Test
    public void testInterceptor() {
        // 定义拦截器
        final Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                long start = System.currentTimeMillis();
                final Request request = chain.request();
                final Response response = chain.proceed(request);
                long end = System.currentTimeMillis();
                System.out.println("interceptor: cost time = " + (end - start));
                return response;
            }
        };

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // 创建 OkHttpClient 对象
        final Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        //  OkHttpClient 执行 Request
        try {
            final Response response = client.newCall(request).execute();
            System.out.println("response: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
