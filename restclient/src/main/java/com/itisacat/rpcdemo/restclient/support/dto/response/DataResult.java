package com.itisacat.rpcdemo.restclient.support.dto.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class DataResult<T> {
    private int status;
    private String message;
    private T data;
    private Date time;

    public DataResult() {
    }

    public DataResult(int status, String message, T data) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.setTime(new Date());
    }

    public DataResult(T data) {
        this.data = data;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isOk() {
        return this.status == 0;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isFail() {
        return this.status != 0;
    }

    public T getData() {
        return (T) data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static <V> DataResult<V> fail(int status, String message, V result) {
        return new DataResult<>(status, message, result);
    }

    public static <V> DataResult<V> fail(String message) {
        DataResult<V> result = new DataResult<>();
        result.setMessage(message);
        result.setStatus(-1);
        return result;
    }

    public static <V> DataResult<V> fail(int status, String message) {
        DataResult<V> result = new DataResult<>();
        result.setStatus(status);
        result.setMessage(message);
        return result;
    }

    public static <V> DataResult<V> ok() {
        DataResult<V> result = new DataResult<>();
        result.setMessage("success");
        return result;
    }


    public static <V> DataResult<V> ok(V data) {
        DataResult<V> result = new DataResult<>();
        result.setStatus(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <V> DataResult<V> ok(String message, V data) {
        DataResult<V> result = new DataResult<>();
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <V> DataResult<V> ok(Integer status, String message, V data) {
        DataResult<V> result = new DataResult<>();
        result.setStatus(status);
        result.setMessage(message);
        result.setData(data);
        result.setTime(new Date());
        return result;
    }

    @Override
    public String toString() {
        return "DataResult{" + "status =" + status + ", message =" + message + ", data =" + JSON.toJSONString(data) + ", time = " + time
                + "}";
    }
}
