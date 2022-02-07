package com.itisacat.rpc.service.support.enums;

import com.itisacat.rpc.service.serialization.ISerializer;
import com.itisacat.rpc.service.serialization.JSONSerializer;
import com.itisacat.rpc.service.serialization.JdkSerializer;
import com.itisacat.rpc.service.serialization.PlainTextSerializer;

public enum MessageType {

    /**
     * 发送和接受java类型的消息。使用的默认序列化器为<br>
     * 序列化后的消息体中，会包含java的类信息，即使发送的是string或者jsonobject, 被序列化器转换后，也仍然会带上类信息。<br>
     * 所以不支持异构，要求发送方和接收方的实现都是java，且实现了序列化接口 {@link java.io.Serializable}
     */
    JAVA_TYPE("application/x-java-serialized-object", new JdkSerializer()),

    /**
     * 发送和接收json类型的消息，使用的序列化
     * 支持异构。
     */
    JSON("application/json", new JSONSerializer()),

    /**
     * 发送和接受string类型的消息
     */
    PLAIN("text/plain", new PlainTextSerializer());

    protected ISerializer serializer;

    protected String contentType;

    MessageType(String contentType, ISerializer converter) {
        this.contentType = contentType;
        this.serializer = converter;
    }

    public static MessageType fromContentType(String contentType){
        for (MessageType type: MessageType.values()){
            if (type.getContentType().equals(contentType)){
                return type;
            }
        }
        return JSON;
    }

    public ISerializer getSerializer() {
        return serializer;
    }

    public String getContentType() {
        return contentType;
    }
}
