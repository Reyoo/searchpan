package com.libbytian.pan.system.service.impl;


import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUserModel> implements ISystemUserService {


    private final ISystemUserToRoleService userToRoleService;

    private final SystemUserMapper systemUserMapper ;

    private final ISystemRoleService systemRoleService;

    private final ISystemUserToTemplateService userToTemplateService;

    private final ISystemTemplateService systemTemplateService;



    @Override
    public SystemUserModel getUserByUserName(String username)   {
        return systemUserMapper.selectUserByUsername(username);
    }

    /**
     * 设置所有通过注册的用户均为普通用户，用户权限变更需要在管理端进行配置
     * @param user
     * @return`
     */

    @Override
    public SystemUserModel register(SystemUserModel user)  throws Exception{
        user.setStatus(true);
        user.setCreateTime(LocalDateTime.now());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());
        user.setPassword(encode);
        boolean result = this.save(user);
        /**
         * 新用户赋权限为 ROLE_NORMAL
         */

        /**
         * 1. 先去查询角色表中ROLE_NORMAL 对应的id
         * 2. 拿着 角色id 绑定用户id 插入到库中
         * 3. 拿着 默认模板id=1 绑定用户id
         */

        SystemRoleModel systemRoleModel = systemRoleService.getRoleByRolename("ROLE_NORMAL");

        if (result) {
            SystemUserToRole userToRole  = SystemUserToRole.builder().userId(user.getUserId()).roleId(systemRoleModel.getRoleId()).build();
            userToRoleService.save(userToRole);
          //新增模板,存入模板表 sys_template
            String uuid = UUID.randomUUID().toString();
            SystemTemplateModel template =  new SystemTemplateModel();
            template.setTemplateid(uuid);
            template.setTemplatename("模板1");
            template.setTemplatecreatetime(LocalDateTime.now());
            template.setTemplatestatus(true);
            systemTemplateService.save(template);

            //模板ID绑定用户ID
            SystemUserToTemplate userToTemplate  = SystemUserToTemplate.builder().userId(user.getUserId()).templateId(uuid).userTemplateStatus(true).build();
            userToTemplateService.save(userToTemplate);
        }
        return user;
    }

    /**
     * 查询用户是否存在
     * @param username
     * @return
     */
    @Override
    public int selectByName(String username) throws Exception {

        int count = systemUserMapper.selectByName(username);

        return count;

    }


    @Override
    public SystemUserModel updateUser(SystemUserModel user) throws Exception {

        if(user.getUsername().isEmpty()){
            throw new Exception("用户名不能为空");
        }

        LocalDateTime actTime = systemUserMapper.findActTime(user.getUsername());

    //如果有传入续费时长，则更新到期时间
        if(user.getActrange() != null && user.getActrange() > 0) {
            //获取当前时间
            LocalDateTime nowtime = LocalDateTime.now();

            //已过期，到期时间在当前时间之前
            if(actTime == null || actTime.isBefore(nowtime)) {
                nowtime = nowtime.plus(user.getActrange(), ChronoUnit.MONTHS);
            } else {
                nowtime = actTime.plus(user.getActrange(), ChronoUnit.MONTHS);
            }
            //更新到期时间
            user.setActtime(nowtime);
        }


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());
        SystemUserModel olduser = systemUserMapper.selectUserByUsername(user.getUsername());
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
                queryWrapper.eq("user_mobile",systemUserModel.getMobile());
            }
            if(systemUserModel.getLastLoginTime() != null){
                queryWrapper.eq("user_last_login_time",systemUserModel.getLastLoginTime());
            }
            if(systemUserModel.getCreateTime() != null){
                queryWrapper.eq("createtime",systemUserModel.getCreateTime());
            }
            if(systemUserModel.isStatus()){
                queryWrapper.eq("status",systemUserModel.isStatus());
            }
            if(systemUserModel.getStarttime() != null && systemUserModel.getEndtime() != null){
                queryWrapper.ge("createtime",systemUserModel.getStarttime());
                queryWrapper.le("createtime",systemUserModel.getEndtime());
            }

        }
        queryWrapper.orderByDesc("createtime");

        return systemUserMapper.selectPage(page,queryWrapper);

    }

    @Override
    public SystemUserModel findByUsername(String username) throws Exception {
        return systemUserMapper.selectUserByUsername(username);
    }


    @Override
    public List<SystemTemplateModel> findTemplateById(String username) {

        return systemUserMapper.findTemplateById(username);
    }



}
