package com.itisacat.rpc.service.serialization;

import lombok.Data;

import java.nio.charset.Charset;

@Data
public class PlainTextSerializer implements ISerializer {
    private Charset charset;

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString().getBytes(charset);
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> targetClass) {
        if (targetClass != String.class) {
            throw new RuntimeException("消息类型不是String，不能使用PlainTextSerializer");
        }
        return (T) new String(content, charset);
    }
}
