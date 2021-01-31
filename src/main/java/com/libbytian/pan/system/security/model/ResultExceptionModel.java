package com.libbytian.pan.system.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author: QiSun
 * @date: 2020-09-21
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultExceptionModel {

    String msg;
    int status;
    String data;
}
