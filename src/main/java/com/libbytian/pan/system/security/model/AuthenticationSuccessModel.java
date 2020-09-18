package com.libbytian.pan.system.security.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: QiSun
 * @date: 2020-09-17
 * @Description:
 */

@Data
public class AuthenticationSuccessModel implements Serializable {

   String username;
   String route;
   String token;
   int status;
}
