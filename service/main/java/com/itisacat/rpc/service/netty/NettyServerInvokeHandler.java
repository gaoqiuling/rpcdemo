package com.itisacat.rpc.service.netty;

import com.alibaba.fastjson.JSONObject;
import com.itisacat.rpcdemo.serviceapi.support.dto.request.ProviderServiceRequest;
import com.itisacat.rpcdemo.serviceapi.support.dto.request.StormRequest;
import com.itisacat.rpcdemo.serviceapi.support.dto.response.StormResponse;
import com.itisacat.rpc.service.zk.IRegisterCenter4Provider;
import com.itisacat.rpc.service.zk.RegisterCenter;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 处理服务端的逻辑
 *
 * @author hsun
 */
@ChannelHandler.Sharable
public class NettyServerInvokeHandler extends SimpleChannelInboundHandler<StormRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerInvokeHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //发生异常,关闭链路
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StormRequest request) throws Exception {
        if (ctx.channel().isWritable()) {
            //从服务调用对象里获取服务提供者信息
            ProviderServiceRequest metaDataModel = request.getProviderService();
            final String methodName = request.getInvokedMethodName();
            String serviceKey = metaDataModel.getServiceInterface().getName();
            //获取注册中心服务
            IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.singleton();
            ProviderServiceRequest localProvider = registerCenter4Provider.getProviderServiceMap().get(serviceKey);
            if (localProvider == null) {
                StormResponse response = new StormResponse(request.getUniqueKey(), "no service");
                ctx.writeAndFlush(response);
                return;
            }
            Method method = localProvider.getMethodList().stream().filter(t -> t.getName().equals(methodName)).findFirst().orElse(null);
            if (method == null) {
                StormResponse response = new StormResponse(request.getUniqueKey(), "no method");
                ctx.writeAndFlush(response);
                return;
            }
            Object result = null;
            try {
                result = method.invoke(localProvider.getServiceObject(), request.getArgs());
            } catch (Exception e) {
                logger.error(JSONObject.toJSONString(localProvider) + "  " + methodName + " ", e);
                result = e;
            }
            StormResponse response = new StormResponse(request.getUniqueKey(), result);
            ctx.writeAndFlush(response);
        }
    }
}
