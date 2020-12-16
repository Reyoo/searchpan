package com.libbytian.pan.system.service.impl;


import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import com.libbytian.pan.system.mapper.SystemUserToKeywordMapper;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUserModel> implements ISystemUserService {


    private final ISystemUserToRoleService userToRoleService;

    private final SystemUserMapper systemUserMapper;

    private final ISystemRoleService systemRoleService;

    private final ISystemUserToTemplateService userToTemplateService;

    private final ISystemTemplateService systemTemplateService;

    private final ISystemTemDetailsService systemTemDetailsService;

    private final IKeywordService keywordService;

    private final SystemUserToKeywordMapper systemUserToKeywordMapper;


    @Override
    public Boolean checkUserCouldDel(SystemUserModel user) throws Exception {
        SystemUserModel systemUserModel = getUser(user);
        if (systemUserModel.getAllowremove()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public SystemUserModel getUser(SystemUserModel systemUserModel) {
        return systemUserMapper.getUser(systemUserModel);
    }

    @Override
    public List<SystemUserModel> listUsers(SystemUserModel systemUserModel) {
        return systemUserMapper.listUsers(systemUserModel);
    }


    /**
     * 设置所有通过注册的用户均为普通用户，用户权限变更需要在管理端进行配置
     *
     * @param user
     * @return`
     */

    @Override
    public SystemUserModel register(SystemUserModel user) throws Exception {

//        user.setStatus(true);
        user.setCreateTime(LocalDateTime.now());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());
        user.setPassword(encode);
        LocalDateTime time = LocalDateTime.now();
        //激活到期时间 + 7天
        user.setActTime(time.plusDays(7L));
//        user.setUserBase64Name(");
        //新增用户
        boolean result = this.save(user);
        SystemRoleModel systemRoleModel = new SystemRoleModel();
        systemRoleModel.setRoleName("ROLE_NORMAL");


        SystemRoleModel roleModel = systemRoleService.getRoles(systemRoleModel);

        if (result) {
            //保存用户角色信息
            SystemUserToRole userToRole = SystemUserToRole.builder().userId(user.getUserId()).roleId(roleModel.getRoleId()).build();
            userToRoleService.save(userToRole);
            String templateId = UUID.randomUUID().toString(true);
            SystemTemplateModel template = new SystemTemplateModel();
            template.setTemplateid(templateId);
            template.setTemplatename("默认模板");
            template.setTemplatecreatetime(LocalDateTime.now());
            template.setTemplatestatus(true);
            //给用户新增模板
            systemTemplateService.save(template);
            //模板ID绑定用户ID
            //用户绑定模板表
            SystemUserToTemplate userToTemplate = SystemUserToTemplate.builder().userId(user.getUserId()).templateId(templateId).userTemplateStatus(true).build();
            userToTemplateService.save(userToTemplate);
            //注册时,在默认模板ID对应模板详情下存入默认关键词
            systemTemDetailsService.defaultSave(templateId);


            SystemKeywordModel systemKeywordModel = new SystemKeywordModel( );
            systemKeywordModel.setUserSafeKey("http://51.findfish.top/wechat/portal/" + Base64.getEncoder().encodeToString(user.getUsername().getBytes()));
            systemKeywordModel.setKeywordId(templateId);
            systemKeywordModel.setStartTime("00:00");
            systemKeywordModel.setEndTime("24:00");
            //新增用户 信息类 插入关键字表
            keywordService.addkeyword(systemKeywordModel);

            //插入关联表

            SystemUserToKeyword systemUserToKeyword = new SystemUserToKeyword(UUID.randomUUID().toString(true),user.getUserId(),templateId);

            systemUserToKeywordMapper.insertSysuserToKeyword(systemUserToKeyword);





        }
        return user;
    }


    @Override
    public void removeUserAll(SystemUserModel user) {

        systemUserMapper.removeUserAll(user);

    }


    @Override
    public SystemUserModel updateUser(SystemUserModel user) throws Exception {

        if (user.getUsername().isEmpty()) {
            throw new Exception("用户名不能为空");
        }
        if (user.getActTime() != null) {
            LocalDateTime actTime = systemUserMapper.getUser(user).getActTime();

            //如果有传入续费时长，则更新到期时间
            if (user.getActrange() != null && user.getActrange() > 0) {
                //获取当前时间
                LocalDateTime nowtime = LocalDateTime.now();

                //已过期，到期时间在当前时间之前
                if (actTime == null || actTime.isBefore(nowtime)) {
                    nowtime = nowtime.plus(user.getActrange(), ChronoUnit.MONTHS);
                } else {
                    nowtime = actTime.plus(user.getActrange(), ChronoUnit.MONTHS);
                }
                //更新到期时间
                user.setActTime(nowtime);
            }
        }


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());

        /**
         * HuangS
         * 新建个对象，拿对象的用户名去查,不带入传入的mobile(因传入的mobile数据库可能没有，会造成查询userID为null)
         */
        SystemUserModel userModel = new SystemUserModel();
        userModel.setUsername(user.getUsername());

        SystemUserModel olduser = systemUserMapper.getUser(userModel);
//        SystemUserModel olduser = systemUserMapper.getUser(user);
        user.setUserId(olduser.getUserId());
        user.setPassword(encode);
        boolean result = this.saveOrUpdate(user);
        if (result) {
            SystemUserToRole userToRole = SystemUserToRole.builder().userId(user.getUserId()).roleId("ROLE_NORMAL").build();
            userToRoleService.save(userToRole);
        }

        return user;
    }


    @Override
    public IPage<SystemUserModel> findConditionByPage(Page<SystemUserModel> page, SystemUserModel systemUserModel) throws Exception {


        QueryWrapper queryWrapper = new QueryWrapper();

        if (systemUserModel != null) {

            if (systemUserModel.getUserId() != null && !systemUserModel.getUserId().equals("")) {
                queryWrapper.eq("user_id", systemUserModel.getUserId());
            }
            if (systemUserModel.getUsername() != null && !systemUserModel.getUsername().equals("")) {
                queryWrapper.eq("user_name", systemUserModel.getUsername());
            }
            if (systemUserModel.getMobile() != null && !"".equals(systemUserModel.getMobile())) {
                queryWrapper.eq("user_mobile", systemUserModel.getMobile());
            }
            if (systemUserModel.getLastLoginTime() != null) {
                queryWrapper.eq("user_last_login_time", systemUserModel.getLastLoginTime());
            }
            if (systemUserModel.getCreateTime() != null) {
                queryWrapper.eq("createtime", systemUserModel.getCreateTime());
            }

            System.out.println(systemUserModel.getStatus());
            if (systemUserModel.getStatus() != null) {
                queryWrapper.eq("status", systemUserModel.getStatus());
            }

            if (systemUserModel.getStarttime() != null && systemUserModel.getEndtime() != null) {
                queryWrapper.ge("createtime", systemUserModel.getStarttime());
                queryWrapper.le("createtime", systemUserModel.getEndtime());
            }
        }


        return baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 校验用户状态， 启用状态，付费用户，是否存在
     *
     * @param
     * @return
     */
    @Override
    public Boolean checkUserStatus(SystemUserModel user) throws Exception {

        // 判断该用户是否存在
        // 如果存在 ，判断该用户付费剩余时长
        SystemUserModel systemUserModel = systemUserMapper.getUser(user);
        if (systemUserModel == null) {
            throw new Exception("该用户不存在");
        }

        List<SystemRoleModel> systemRoleModels = systemRoleService.getRoleInfoByUser(user);

        //如果不包含付费用户
        for (SystemRoleModel systemRoleModel : systemRoleModels) {
            // 如果是付费用户 则判断付费是否过期
            if (systemRoleModel.getRoleName().contains("ROLE_PAYUSER")) {
                LocalDateTime actTime = systemUserModel.getActTime();
                if (actTime.isBefore(LocalDateTime.now())) {
                    throw new Exception("你的付费时长已过期，请续费使用");
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public SystemUserModel getUserByUerToTemplate(String templateId) {
        return systemUserMapper.getUserByUerToTemplate(templateId);
    }

}
