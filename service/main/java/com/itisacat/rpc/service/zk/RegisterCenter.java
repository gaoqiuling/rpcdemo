package com.itisacat.rpc.service.zk;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itisacat.rpcdemo.serviceapi.support.common.IpHelper;
import com.itisacat.rpcdemo.serviceapi.support.dto.request.ProviderServiceRequest;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author hsun
 * @Descrption 注册中心实现
 * @DATE 19-11-24 下午11:34
 ***/
public class RegisterCenter implements IRegisterCenter4Provider {

    private static RegisterCenter registerCenter = new RegisterCenter();

    //服务提供者列表,Key:服务提供者接口  value:服务提供者服务方法列表
    private static final Map<String, ProviderServiceRequest> providerServiceMap = Maps.newConcurrentMap();
    //服务端ZK服务元信息,选择服务(第一次直接从ZK拉取,后续由ZK的监听机制主动更新)
    private static final Map<String, List<ProviderServiceRequest>> serviceMetaDataMap4Consume = Maps.newConcurrentMap();

    private static String ZK_SERVICE = "127.0.0.1:2181";
    private static int ZK_SESSION_TIME_OUT = 6000;
    private static int ZK_CONNECTION_TIME_OUT = 6000;
    private static String ROOT_PATH = "/storm";
    private static String PROVIDER_TYPE = "provider";
    private static volatile ZkClient zkClient = null;

    private RegisterCenter() {
    }

    public static RegisterCenter singleton() {
        return registerCenter;
    }

    @Override
    public void registerProvider(final List<ProviderServiceRequest> serviceMetaData) {
        if (CollectionUtils.isEmpty(serviceMetaData)) {
            return;
        }
        //连接zk,注册服务
        synchronized (RegisterCenter.class) {
            for (ProviderServiceRequest provider : serviceMetaData) {
                String serviceItfKey = provider.getServiceInterface().getName();
                providerServiceMap.putIfAbsent(serviceItfKey, provider);
            }

            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }

            //创建 ZK命名空间/当前部署应用APP命名空间/
            String APP_KEY = serviceMetaData.get(0).getAppKey();
            String ZK_PATH = ROOT_PATH + "/" + APP_KEY;
            boolean exist = zkClient.exists(ZK_PATH);
            if (!exist) {
                zkClient.createPersistent(ZK_PATH, true);
            }

            for (Map.Entry<String, ProviderServiceRequest> entry : providerServiceMap.entrySet()) {
                //创建服务提供者
                String serviceNode = entry.getKey();
                String servicePath = ZK_PATH + "/" + serviceNode + "/" + PROVIDER_TYPE;
                exist = zkClient.exists(servicePath);
                if (!exist) {
                    zkClient.createPersistent(servicePath, true);
                }
                //创建当前服务器节点
                int serverPort = entry.getValue().getServerPort();//服务端口
                String localIp = IpHelper.localIp();
                String currentServiceIpNode = servicePath + "/" + localIp + "|" + serverPort;
                exist = zkClient.exists(currentServiceIpNode);
                if (!exist) {
                    //注意,这里创建的是临时节点
                    zkClient.createEphemeral(currentServiceIpNode);
                }

                //监听注册服务的变化,同时更新数据到本地缓存
                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                        if (currentChilds == null) {
                            currentChilds = Lists.newArrayList();
                        }

                        //存活的服务IP列表
                        List<String> activityServiceIpList = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
                            @Override
                            public String apply(String input) {
                                return StringUtils.split(input, "|")[0];
                            }
                        }));
                        refreshActivityService(activityServiceIpList);
                    }
                });

            }
        }
    }

    @Override
    public Map<String, ProviderServiceRequest> getProviderServiceMap() {
        return providerServiceMap;
    }

    //利用ZK自动刷新当前存活的服务提供者列表数据
    private void refreshActivityService(List<String> serviceIpList) {
        if (serviceIpList == null) {
            serviceIpList = Lists.newArrayList();
        }
        Map<String, ProviderServiceRequest> currentMap = Maps.newHashMap();
        for (Map.Entry<String, ProviderServiceRequest> entry : providerServiceMap.entrySet()) {
            if (serviceIpList.contains(entry.getValue().getServerIp())) {
                currentMap.put(entry.getKey(), entry.getValue());
            }
        }
        providerServiceMap.clear();
        System.out.println("currentServiceMetaDataMap," + JSON.toJSONString(currentMap));
        providerServiceMap.putAll(currentMap);
    }
}
