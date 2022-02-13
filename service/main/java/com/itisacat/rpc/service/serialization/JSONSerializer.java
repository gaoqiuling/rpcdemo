package com.itisacat.rpc.service.serialization;


import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Data
public class JSONSerializer implements ISerializer {

    private Charset charset = StandardCharsets.UTF_8;

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        try {
            String json = JSON.toJSONString(object);
            return json.getBytes(charset);
        } catch (Exception e) {
            throw new RuntimeException("序列化到JSON发生异常", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> targetClass) {
        if (content == null) {
            return null;
        }
        try {
            String object = new String(content, charset);
            return JSON.parseObject(object, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("反序列化JSON异常", e);
        }
    }
}
