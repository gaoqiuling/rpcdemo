package com.itisacat.rpcdemo.restclient.support.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class BaseRequest implements java.io.Serializable {

    /**
     * 需要请求的目标Api地址
     */
    @JSONField(serialize = false)
    private String _url;

    /**
     * Api版本号
     */
    @JSONField(serialize = false)
    private Integer _version;

    /**
     * 请求源（Web、Android、Ios...）
     */
    private String _source;

    public void setUrl(String url) {
        this._url = url;
    }
    public String getUrl() {
        return this._url;
    }
    /**
     * APi请求头
     */
    @JSONField(serialize = false)
    private Map<String, String> _headers = new HashMap<>();

    public Map<String, String> getHeaders() {
        return _headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this._headers = headers;
    }

    public BaseRequest() {
        this._headers = new HashMap<>();
    }

    @JSONField(serialize = false)
    public Map<String, Object> getParamData() {
        Map<String, Object> mp = new HashMap<>();

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                mp.put(field.getName(), field.get(this));
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        mp.remove("serialVersionUID");
        return mp;
    }
}
