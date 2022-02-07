package com.itisacat.rpc.service.register;

import com.google.common.collect.Lists;
import com.itisacat.rpc.service.anno.RpcService;
import com.itisacat.rpc.service.netty.NettyServer;
import com.itisacat.rpcdemo.serviceapi.support.common.IpHelper;
import com.itisacat.rpcdemo.serviceapi.support.dto.request.ProviderServiceRequest;
import com.itisacat.rpc.service.zk.RegisterCenter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

@Service
public class ServiceRegister implements ApplicationContextAware {
    private static Integer serverPort = 8081;
    private static Integer nettyPort = 9000;
    private static String appKey = "helloDemo";
    private List<Object> rpcServices;

    @PostConstruct
    public void init() {
        //启动Netty服务端
        NettyServer.getInstance().start(nettyPort);
        //注册到zk,元数据注册中心
        RegisterCenter.singleton().registerProvider(buildProviderServiceInfos());
    }

    private List<ProviderServiceRequest> buildProviderServiceInfos() {
        List<ProviderServiceRequest> list = Lists.newArrayList();
        rpcServices.forEach(service -> {
            ProviderServiceRequest providerService = new ProviderServiceRequest();
            providerService.setServerIp(IpHelper.localIp());
            providerService.setServerPort(serverPort);
            providerService.setAppKey(appKey);
            providerService.setServiceObject(service);
            providerService.setServiceInterface(service.getClass().getInterfaces()[0]);
            providerService.setMethodList(Lists.newArrayList());

            Method[] methods = service.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                providerService.getMethodList().add(method);
            }
            list.add(providerService);
        });
        return list;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(RpcService.class);
        rpcServices = Lists.newArrayList(map.values());
    }
}
