package com.libbytian.pan.system.service.impl;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.*;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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
@Transactional
@Slf4j
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUserModel> implements ISystemUserService {


    private final ISystemUserToRoleService userToRoleService;

    private final SystemUserMapper systemUserMapper;
    private final SystemTemplateMapper systemTemplateMapper;
    private final SystemTemToTemDetailsMapper systemTemToTemDetailsMapper;

    private final SystemUserToTemplateMapper systemUserToTemplateMapper;

    private final SystemKeywordMapper systemKeywordMapper;


    private final ISystemRoleService systemRoleService;


    private final SystemUserToRoleMapper userToRoleMapper;


    private final ISystemUserToTemplateService userToTemplateService;

    private final ISystemTemplateService systemTemplateService;

    private final ISystemTemDetailsService systemTemDetailsService;

    private final SystemTemDetailsMapper systemTemDetailsMapper;

    private final ISystemKeywordService keywordService;

    private final SystemUserToKeywordMapper systemUserToKeywordMapper;


    @Override
    public Boolean checkUserCouldDel(SystemUserModel user) throws Exception {
        SystemUserModel systemUserModelInfo = systemUserMapper.getUserById(user);
        if (systemUserModelInfo.allowremove()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public int addSystemUser(SystemUserModel systemUserModel) throws Exception {
        return systemUserMapper.insertSystemUser(systemUserModel);
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

        String userId = UUID.fastUUID().toString();
        user.createTime(LocalDateTime.now());
        user.userId(userId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.password());
        user.password(encode);
        LocalDateTime time = LocalDateTime.now();
        //激活到期时间 + 1天
        user.actTime(time.plusDays(1L));
        //新增用户
        int insertFlag = systemUserMapper.insertSystemUser(user);
        SystemRoleModel systemRoleModel = new SystemRoleModel();
        systemRoleModel.roleName("ROLE_NORMAL");


        SystemRoleModel roleModel = systemRoleService.getRoles(systemRoleModel);

        if (insertFlag == 1) {
            String templateId = UUID.randomUUID().toString(true);
            //保存用户角色信息
            SystemUserToRole userToRole = new SystemUserToRole(templateId, userId, roleModel.roleId(), Boolean.TRUE, Boolean.FALSE);
            userToRoleService.addUserToRoleModel(userToRole);
            SystemTemplateModel template = new SystemTemplateModel();
            template.setTemplateid(templateId);
            template.setTemplatename("默认模板");
            template.setTemplatecreatetime(LocalDateTime.now());
            template.setTemplatestatus(true);
            //给用户新增模板
            systemTemplateService.save(template);
            //模板ID绑定用户ID
            //用户绑定模板表
            SystemUserToTemplate userToTemplate = SystemUserToTemplate.builder().userId(userId).templateId(templateId).userTemplateStatus(true).build();
            userToTemplateService.save(userToTemplate);
            //注册时,在默认模板ID对应模板详情下存入默认关键词
            systemTemDetailsService.defaultSave(templateId);

            String appId = user.appId();
            if (StrUtil.isEmpty(appId)) {
                appId = "请填写appID";
            }

            SystemKeywordModel systemKeywordModel = new SystemKeywordModel();
            systemKeywordModel.userSafeKey("http://51.findfish.top/wechat/portal/" + Base64.getEncoder().encodeToString(user.username().getBytes()) + "/" + appId);
            systemKeywordModel.keywordId(templateId);
            //00:00-00：00 默认全天开
            systemKeywordModel.startTime("00:00");
            systemKeywordModel.endTime("00:00");
            systemKeywordModel.appId(appId);
            //新增用户 信息类 插入关键字表
            keywordService.addkeyword(systemKeywordModel);
            //插入关联表
            SystemUserToKeyword systemUserToKeyword = new SystemUserToKeyword(UUID.randomUUID().toString(true), user.userId(), templateId);
            systemUserToKeywordMapper.insertSysuserToKeyword(systemUserToKeyword);


        }
        return user;
    }


    /**
     * 删除要一个一个删除  先去删除关联表  最后删除用户表   ！！！！！！！！！！！！！！！！！！！！！！！2020年12月20日21:50:50 待改 功能不可用
     *
     * @param user
     */
    @Override
    public void removeUserAll(SystemUserModel user) throws Exception {


        //删除模板详细
        log.info("删除模板详细");
        systemTemDetailsMapper.deleteTemplateDetailsByUser(user);
        //删除模板表
        log.info("刪除模板表");
        systemTemplateMapper.deleteTemplateByUser(user);
        //删除模板与模板详细关联表
        systemTemToTemDetailsMapper.deleteFindFishTempToTempDetailsByUser(user);
        //删除用户模板关联表
        log.info("刪除用户模板关联表");
        systemUserToTemplateMapper.deleteUserToTemplateByUserId(user);

        //删除用户角色关联表中数据
        log.info("删除角色关联表");
        userToRoleMapper.deleteUserRoleByUserModel(user);
        //删除用户关键词
        log.info("刪除用户关键词");
        systemKeywordMapper.deleteKeywordByUser(user);
//            删除用户关键词关联表
        log.info("删除用户关键词关联表");
        systemUserToKeywordMapper.deleteUserToKeywordByUser(user);
//            删除用户表

        log.info("删除用户表");
        systemUserMapper.deleteSysUserByUser(user);


//        2、删除模板
//        3、删除用户与模板关联表
//        4、删除角色用户关联表
//        5、删除用户表


    }


    @Override
    public int updateUser(SystemUserModel user) throws Exception {

        if (user.username().isEmpty()) {
            throw new Exception("用户名不能为空");
        }

        if(user.password()!=null || StrUtil.isNotBlank(user.password())){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encode = encoder.encode(user.password());
            user.password(encode);
        }

        return systemUserMapper.updateUserById(user);


    }


    @Override
    public IPage<SystemUserModel> findConditionByPage(Page<SystemUserModel> page, SystemUserModel systemUserModel) throws Exception {
        QueryWrapper<SystemUserModel> queryWrapper = new QueryWrapper();
        if (systemUserModel != null) {
            if (systemUserModel.userId() != null && !systemUserModel.userId().equals("")) {
                queryWrapper.lambda().eq(SystemUserModel::userId, systemUserModel.userId());
            }
            if (systemUserModel.username() != null && !systemUserModel.username().equals("")) {
                queryWrapper.lambda().eq(SystemUserModel::username, systemUserModel.username());
            }
            if (systemUserModel.mobile() != null && !"".equals(systemUserModel.mobile())) {
                queryWrapper.lambda().eq(SystemUserModel::mobile, systemUserModel.mobile());
            }
            if (systemUserModel.lastLoginTime() != null) {
                queryWrapper.lambda().eq(SystemUserModel::lastLoginTime, systemUserModel.lastLoginTime());
            }
            if (systemUserModel.createTime() != null) {
                queryWrapper.lambda().eq(SystemUserModel::createTime, systemUserModel.createTime());
            }
            if (systemUserModel.status() != null) {
                queryWrapper.lambda().eq(SystemUserModel::status, systemUserModel.status());
            }
            if (systemUserModel.starttime() != null && systemUserModel.endtime() != null) {
                queryWrapper.lambda().ge(SystemUserModel::createTime, systemUserModel.starttime());
                queryWrapper.lambda().le(SystemUserModel::createTime, systemUserModel.endtime());
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
        if (ObjectUtil.isEmpty(systemUserModel)) {
            throw new Exception("该用户不存在");
        }
        List<SystemRoleModel> systemRoleModels = systemRoleService.getRoleInfoByUser(user);
        //如果不包含付费用户
        for (SystemRoleModel systemRoleModel : systemRoleModels) {
            // 如果是付费用户 则判断付费是否过期
            if (systemRoleModel.roleName().contains("ROLE_PAYUSER")) {
                LocalDateTime actTime = systemUserModel.actTime();
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
