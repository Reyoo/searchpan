package com.libbytian.pan.system.security.simple;

import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.security.model.CustomRole;
import com.libbytian.pan.system.service.ISystemRoleService;
import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.system.service.ISystemUserToRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
    private ISystemUserToRoleService iUserToRoleService;

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

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 从数据库中取出数据信息
        if (StringUtils.isEmpty(username)){
            log.info("用户名输入不能为空");
            throw new UsernameNotFoundException("用户名不能为空");
        }
        SystemUserModel systemUserModel = iSystemUserService.getUserByUserName(username);

        if (systemUserModel == null){
            throw new UsernameNotFoundException("该用户名不存在!");
        }
        System.out.println(systemUserModel.getPassword());
        List<SystemRoleModel> roles = iSystemRoleService.getRolenameByUserId(systemUserModel.getUserId());
        log.info("用户:{}开始查询对应的角色." , username);
        for (SystemRoleModel systemRoleModel: roles ) {
            authorities.add(new CustomRole(systemRoleModel));
        }
        return new JwtUser(systemUserModel,new HashSet(authorities));
    }


//        UserDetails userDetails = null;
//        if ("admin".equals(username)) {
//            return new JwtUser("admin", passwordEncoder.encode("123456"));
//        }
//        if ("user".equals(username)) {
//            return new JwtUser("user", passwordEncoder.encode("123456"));
//        }


}
