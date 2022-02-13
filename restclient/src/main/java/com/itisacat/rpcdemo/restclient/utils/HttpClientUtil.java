package com.itisacat.rpcdemo.restclient.utils;


import com.google.common.base.Stopwatch;
import com.itisacat.rpcdemo.restclient.support.dto.response.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpClientUtil {
    private static final String USER_AGENT = "user_agent";
    private static volatile CloseableHttpClient httpClient;

    private HttpClientUtil() {
    }

    private static RequestConfig requestConfig = null;

    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (CloseableHttpClient.class) {
                if (httpClient == null) {
                    ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
                    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                            .<ConnectionSocketFactory>create().register("http", plainsf)
                            .register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();
                    HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                            DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);
                    DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
                    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                            socketFactoryRegistry, connFactory, dnsResolver);

                    SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
                    connManager.setDefaultSocketConfig(defaultSocketConfig);

                    connManager.setMaxTotal(1000);
                    connManager.setDefaultMaxPerRoute(500);
                    connManager.setValidateAfterInactivity(1 * 1000);
                    requestConfig = RequestConfig.custom()
                            .setSocketTimeout(10000)
                            .setConnectTimeout(5 * 1000)
                            .setConnectionRequestTimeout(2 * 1000)
                            .build();
                    HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connManager)
                            .setConnectionManagerShared(false).evictExpiredConnections()
                            .evictIdleConnections(10, TimeUnit.SECONDS).setDefaultRequestConfig(requestConfig)
                            .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE);
                    httpClient = httpClientBuilder.build();

                    Thread closeThread = new IdleConnectionMonitorThread(connManager);
                    closeThread.setDaemon(true);
                    closeThread.start();
                }
            }
        }
        return httpClient;
    }

    public static HttpResult execute2Result(HttpRequestBase httpRequestBase, boolean isRturnHttpResponse) {
        Stopwatch begin = Stopwatch.createStarted();
        if (httpRequestBase == null) {
            throw new RuntimeException("HttpRequestBase is null!");
        }
        HttpResult result = null;
        HttpRequestWrapper httpRequest = null;
        CloseableHttpResponse response = null;
        String responseContent = null;

        try {
            // 动态设置请求超时
            setConfig(httpRequestBase, 10, TimeUnit.SECONDS);
            // 执行请求
            response = getHttpClient().execute(httpRequestBase);

            if (isRturnHttpResponse) {
                result = new HttpResult(response);
            } else {
                if (response != null) {
                    HttpEntity entity = response.getEntity();
                    responseContent = EntityUtils.toString(entity);
                    int statusCode = response.getStatusLine().getStatusCode();
                    result = new HttpResult(statusCode, responseContent);
                }
            }


            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!isRturnHttpResponse) {
                closeResources(response, httpRequestBase);
            }
        }
    }

    private static void setConfig(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit) {
        RequestConfig config = null;
        if (timeout != 10000l && timeout > 0L) {
            int timeoutInMS = Math.toIntExact(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
            config = RequestConfig.custom().setSocketTimeout(timeoutInMS).setConnectTimeout(timeoutInMS)
                    .setConnectionRequestTimeout(timeoutInMS).build();
        } else {
            config = requestConfig;
        }
        httpRequestBase.setConfig(config);
    }

    private static void closeResources(CloseableHttpResponse httpResponse, HttpRequestBase httpRequestBase) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        try {
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String sendHttpRequest(HttpRequestBase httpRequestBase) {
        HttpResult result = execute2Result(httpRequestBase, false);
        if (result != null) {
            return result.getResult();
        }
        return null;
    }

    public static String sendHttpGet(HttpGet httpGet) {
        return sendHttpRequest(httpGet);
    }

    public static String sendHttpGet(String httpUrl) {
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet);
    }

    public static String sendHttpPost(String httpUrl, String jsonStr, Header... headers) {
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            // 设置参数
            StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            httpPost.setEntity(stringEntity);
            if (headers != null) {
                httpPost.setHeaders(headers);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpResult result = execute2Result(httpPost, false);
        if (result != null) {
            return result.getResult();
        }
        return null;
    }
}
