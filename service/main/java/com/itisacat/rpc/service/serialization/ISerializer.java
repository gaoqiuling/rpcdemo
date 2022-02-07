package com.itisacat.rpc.service.serialization;

import java.nio.charset.Charset;

public interface ISerializer {
    void setCharset(Charset charset);

    Charset getCharset();

    byte[] serialize(Object object);

    <T> T deserialize(byte[] content, Class<T> targetClass);
}
