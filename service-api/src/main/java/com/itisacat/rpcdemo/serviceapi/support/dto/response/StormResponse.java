package com.itisacat.rpcdemo.serviceapi.support.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StormResponse implements Serializable {
    //UUID,唯一标识一次返回值
    private String uniqueKey;
    //接口调用返回的结果对象
    private Object result;
}
