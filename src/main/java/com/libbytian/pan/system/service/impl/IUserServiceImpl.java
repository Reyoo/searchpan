package com.libbytian.pan.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import com.libbytian.pan.system.mapper.UserMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.service.ISystemUserToRoleService;
import com.libbytian.pan.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IUserServiceImpl extends ServiceImpl<SystemUserMapper,SystemUserModel> implements IUserService {


    private final UserMapper userMapper;

    private final ISystemUserToRoleService userToRoleService;



    @Override
    public int deleteUserByUsername(String username) {

        return userMapper.deleteUserByUsername(username);
    }



    @Override
    public SystemUserModel updateUser(SystemUserModel user) {
        user.setLastLoginTime(LocalDateTime.now());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());
        user.setPassword(encode);
        boolean result = this.updateById(user);

        if(result){
            SystemUserToRole userToRole =  SystemUserToRole.builder().userId(user.getUserId()).roleId("ROLE_NORMAL").build();
            userToRoleService.save(userToRole);
        }
        return user;
    }

    @Override
    public IPage<SystemUserModel> findByPage(Page<SystemUserModel> page){

        return userMapper.selectPage(page,null);


    }


    @Override
    public IPage<SystemUserModel> findConditionByPage(Page<SystemUserModel> page, SystemUserModel user) {

        QueryWrapper queryWrapper = new QueryWrapper();

        if(user.getUsername() != null){
            queryWrapper.eq("user_name",user.getUsername());
        }

        if(user.getUserId() != null){
            queryWrapper.eq("user_id",user.getUserId());
        }


        return userMapper.selectPage(page,queryWrapper);

    }

}
