package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.service.ISystemUserToRoleService;
import com.libbytian.pan.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl extends ServiceImpl<SystemUserMapper,SystemUserModel> implements IUserService {


    private final SystemUserMapper userMapper;

    private final ISystemUserToRoleService userToRoleService;



    @Override
    public SystemUserModel updateUser(SystemUserModel user) throws Exception {

        if(user.getUsername().isEmpty()){
            throw new Exception("用户名不能为空");
        }
        user.setLastLoginTime(LocalDateTime.now());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());
        SystemUserModel olduser = userMapper.selectUserByUsername(user.getUsername());
        user.setUserId( olduser.getUserId());
        user.setPassword(encode);
        boolean result = this.saveOrUpdate(user);
        if(result){
            SystemUserToRole userToRole =  SystemUserToRole.builder().userId(user.getUserId()).roleId("ROLE_NORMAL").build();
            userToRoleService.save(userToRole);
        }

        return user;
    }


    @Override
    public IPage<SystemUserModel> findConditionByPage(Page<SystemUserModel> page, SystemUserModel systemUserModel) throws Exception {

        QueryWrapper queryWrapper = new QueryWrapper();

        /**
         * 这里systemusermodel 不做空判断 。getusername 空指针  null.getUsername
         */
        if(systemUserModel != null){

            if(systemUserModel.getUserId() != null){
                queryWrapper.eq("user_id",systemUserModel.getUserId());
            }
            if(systemUserModel.getUsername() != null){
                queryWrapper.eq("user_name",systemUserModel.getUsername());
            }
            if(systemUserModel.getMobile() != null){
                queryWrapper.eq("user_mobile",systemUserModel.getUserId());
            }
            if(systemUserModel.getLastLoginTime() != null){
                queryWrapper.eq("user_last_login_time",systemUserModel.getUserId());
            }
            if(systemUserModel.getCreateTime() != null){
                queryWrapper.eq("createtime",systemUserModel.getUserId());
            }
            if(systemUserModel.isStatus()){
                queryWrapper.eq("status",systemUserModel.isStatus());
            }
        }
        queryWrapper.orderByDesc("createtime");

        return userMapper.selectPage(page,queryWrapper);

    }

    @Override
    public SystemUserModel findByUsername(String username) throws Exception {
        return userMapper.selectUserByUsername(username);
    }


    @Override
    public SystemTemplateModel findTemplateById(String username) {

        return userMapper.findTemplateById(username);
    }


}
