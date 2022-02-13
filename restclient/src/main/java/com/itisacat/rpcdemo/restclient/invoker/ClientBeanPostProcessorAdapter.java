package com.itisacat.rpcdemo.restclient.invoker;

import com.google.common.collect.Maps;
import com.itisacat.rpcdemo.restclient.anno.RpcClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class ClientBeanPostProcessorAdapter implements InstantiationAwareBeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private static final Map<Type, Object> beanDefinitionMap = Maps.newConcurrentMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //注入属性
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            RpcClient annotation = field.getAnnotation(RpcClient.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                try {
                    field.set(bean, createProxy(field.getType()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return pvs;
    }

    public Object createProxy(Class<?> clazz) throws Exception {
        Object o = beanDefinitionMap.get(clazz);
        if (o != null) {
            return o;
        }
        String[] beanNamesForType = applicationContext.getBeanNamesForType(clazz);
        if (beanNamesForType != null && beanNamesForType.length > 0) {
            return beanNamesForType[0];
        }
        InvokeFactoryBean cpf = new InvokeFactoryBean(clazz);
        Object resultObject = cpf.getObject();
        beanDefinitionMap.put(clazz, resultObject);
        return resultObject;
    }
}
