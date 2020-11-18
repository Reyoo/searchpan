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

    private final ISystemTemDetailsService iSystemTemDetailsService;


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
        if(user.isStatus())
        user.setStatus(true);
        user.setCreateTime(LocalDateTime.now());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());
        user.setPassword(encode);
        user.setActTime(LocalDateTime.now());
        boolean result = this.save(user);
        /**
         * 新用户赋权限为 ROLE_NORMAL
         */

        /**
         * 1. 先去查询角色表中ROLE_NORMAL 对应的id
         * 2. 拿着 角色id 绑定用户id 插入到库中
         * 3. 拿着 默认模板id=1 绑定用户id
         */


        SystemRoleModel systemRoleModel = new SystemRoleModel();
        systemRoleModel.setRoleName("ROLE_NORMAL");


        SystemRoleModel roleModel = systemRoleService.getRoles(systemRoleModel);

        if (result) {
            SystemUserToRole userToRole  = SystemUserToRole.builder().userId(user.getUserId()).roleId(roleModel.getRoleId()).build();
            userToRoleService.save(userToRole);


            /**
             * HuangS
             * 11.12-优化
             * 注册时不为用户新建模板，直接绑定默认展示模板"1"
             * 付费后删除绑定模板"1"，并允许用户新建模板
             */
//            //模板ID绑定用户ID
//            SystemUserToTemplate userToTemplate = SystemUserToTemplate.builder().userId(user.getUserId()).templateId("1").userTemplateStatus(true).build();
//            userToTemplateService.save(userToTemplate);


            //          //新增模板,存入模板表 sys_template
            String templateId = UUID.randomUUID().toString();
            SystemTemplateModel template =  new SystemTemplateModel();
            template.setTemplateid(templateId);
            template.setTemplatename("默认模板");
            template.setTemplatecreatetime(LocalDateTime.now());
            template.setTemplatestatus(true);
            systemTemplateService.save(template);

            //模板ID绑定用户ID
            SystemUserToTemplate userToTemplate = SystemUserToTemplate.builder().userId(user.getUserId()).templateId(templateId).userTemplateStatus(true).build();
            userToTemplateService.save(userToTemplate);

            //注册时,在默认模板ID对应模板详情下存入默认关键词
            iSystemTemDetailsService.defaultSave(templateId);
        }
        return user;
    }



    @Override
    public void removeUserAll(SystemUserModel user) {

        systemUserMapper.removeUserAll(user);

    }


    @Override
    public SystemUserModel updateUser(SystemUserModel user) throws Exception {

        if(user.getUsername().isEmpty()){
            throw new Exception("用户名不能为空");
        }
        if(user.getActTime() != null){
            LocalDateTime actTime = systemUserMapper.getUser(user).getActTime();

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
            if(systemUserModel.getUsername() != null&& !systemUserModel.getUsername().equals("")){
                queryWrapper.eq("user_name",systemUserModel.getUsername());
            }
            if(systemUserModel.getMobile() != null && !"".equals(systemUserModel.getMobile())){
                queryWrapper.eq("user_mobile",systemUserModel.getMobile());
            }
            if(systemUserModel.getLastLoginTime() != null){
                queryWrapper.eq("user_last_login_time",systemUserModel.getLastLoginTime());
            }
            if(systemUserModel.getCreateTime() != null){
                queryWrapper.eq("createtime",systemUserModel.getCreateTime());
            }
            System.out.println(String.valueOf(systemUserModel.isStatus()));

            queryWrapper.eq("status",systemUserModel.isStatus());

            if(systemUserModel.getStarttime() != null && systemUserModel.getEndtime() != null){
                queryWrapper.ge("createtime",systemUserModel.getStarttime());
                queryWrapper.le("createtime",systemUserModel.getEndtime());
            }

        }
        queryWrapper.orderByDesc("createtime");

        return systemUserMapper.selectPage(page,queryWrapper);

    }

    /**
     * 校验用户状态， 启用状态，付费用户，是否存在
     *
     * @param
     * @return
     */
    @Override
    public boolean checkUserStatus(SystemUserModel user) throws Exception {

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
                }else{
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
