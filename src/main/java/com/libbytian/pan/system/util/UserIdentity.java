package com.libbytian.pan.system.util;

import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;


/**
 * @author: HS
 * @date: 2020-10-15
 * @Description:
 */

public class UserIdentity {


    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private SystemRoleMapper systemRoleMapper;

    /**
     * 判断是否为付费用户并且仍在付费使用期
     * @param username
     * @return
     */
    public void isVip(String username) throws Exception {

        systemUserMapper = SpringContextUtil.getBean("systemUserMapper");
        systemRoleMapper = SpringContextUtil.getBean("systemRoleMapper");


        //查询用户是否为付费用户
        String[] userToRoleId = systemRoleMapper.getRoleIdByUsername(username);

        //查询用户权限到期时间
        LocalDateTime actTime = systemUserMapper.findActTime(username);

        //判断用户是否为付费用户
        if( !Arrays.asList(userToRoleId).contains("3")){
            throw new Exception("请升级到付费用户");
        }

        //判断付费用户是否到期
        if (actTime.isBefore(LocalDateTime.now())){
            throw new Exception("你的付费时长已过期，请续费使用");
        }

    }
}
