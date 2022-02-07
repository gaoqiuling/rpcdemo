package com.itisacat.rpc.service.serialization;

import lombok.Data;

import java.io.*;
import java.nio.charset.Charset;

@Data
public class JdkSerializer implements ISerializer {
    private Charset charset;

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Jdk序列化异常", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> targetClass) {
        if (content == null) {
            return null;
        }
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(content);
            ObjectInput oin = new ObjectInputStream(bin);
            Object o = oin.readObject();
            return (T) o;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Jdk反序列化异常", e);
        }
    }
}
