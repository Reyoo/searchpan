package com.libbytian.pan.proxy.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: QiSun
 * @date: 2021-01-13
 * @Description:
 */
@Data
public class ProxyIpAndPortModel implements Serializable {

    String proxy;
    int fail_count;
    String region;
    String type;
    String source;
    int check_count;
    int last_status;
    String last_time;

}
