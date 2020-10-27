package com.libbytian.pan.system.security.provider;

import com.alibaba.fastjson.JSONObject;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.security.model.CustomRole;
import com.libbytian.pan.system.security.model.ResultExceptionModel;
import com.libbytian.pan.system.security.provider.JwtUser;
import com.libbytian.pan.system.service.ISystemRoleService;
import com.libbytian.pan.system.service.ISystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.List;

/**
 * @author niXueChao
 * @date 2019/4/8 11:26.
 */
@Slf4j
@Component
public class JwtUserDetailServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;


    @Autowired
    private ISystemUserService iSystemUserService;


    @Autowired
    private ISystemRoleService iSystemRoleService;


    @Autowired
    public JwtUserDetailServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /** 数据库查询
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<GrantedAuthority> authorities = new ArrayList<>();
        // 从数据库中取出数据信息
        if (StringUtils.isEmpty(username)){
            log.info("用户名输入不能为空");
            throw new UsernameNotFoundException(JSONObject.toJSONString(new ResultExceptionModel("用户名不能为空!!",0,"")));
        }

        SystemUserModel userModel = new SystemUserModel();
        userModel.setUsername(username);
        SystemUserModel systemUserModel = iSystemUserService.getUser(userModel);

        if (systemUserModel == null){
            throw new UsernameNotFoundException(JSONObject.toJSONString(new ResultExceptionModel("该用户名不存在!",0,"")));

        }

        List<SystemRoleModel> roles = iSystemRoleService.listRolesByUser(systemUserModel);
        log.info("用户:{}开始查询对应的角色." , username);
        for (SystemRoleModel systemRoleModel: roles ) {
            authorities.add(new CustomRole(systemRoleModel));
        }
        return new JwtUser(username,systemUserModel.getPassword(),authorities);
    }



}
