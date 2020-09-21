package com.libbytian.pan.system.aop;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestLimitModel implements Serializable {

    private static final long serialVersionUID = -3210884885630038713L;
    String ipAddress;
    int times;
}
