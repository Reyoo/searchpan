package com.libbytian.pan.system.util;

import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


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
     * @param systemUserModel
     * @return
     */
    public void isVip(SystemUserModel systemUserModel) throws Exception {

        systemUserMapper = SpringContextUtil.getBean("systemUserMapper");
        systemRoleMapper = SpringContextUtil.getBean("systemRoleMapper");


        //查询用户
        List<SystemRoleModel> roleModel = systemRoleMapper.listRolesByUser(systemUserModel);

        //查询用户权限到期时间
        LocalDateTime actTime = systemUserMapper.getUser(systemUserModel).getActtime();


        //判断是否为管理员
        if (!roleModel.stream().filter(role->String.valueOf(role.getRoleName()).equals("ROLE_ADMIN")).findAny().isPresent()){

            //判断是否为付费用户
            if (!roleModel.stream().filter(role->String.valueOf(role.getRoleName()).equals("ROLE_PAYUSER")).findAny().isPresent()){
                throw new Exception("请升级到付费用户");
            }

            //判断付费用户是否到期
            if (actTime.isBefore(LocalDateTime.now())){
                throw new Exception("你的付费时长已过期，请续费使用");
            }
        }



    }
}
