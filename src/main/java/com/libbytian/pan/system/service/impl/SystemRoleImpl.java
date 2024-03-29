package com.libbytian.pan.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.mapper.SystemRoleToPermissionMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemRoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SystemRoleImpl extends ServiceImpl<SystemRoleMapper, SystemRoleModel> implements ISystemRoleService {

    private final SystemRoleMapper systemRoleMapper;

    private final SystemRoleToPermissionMapper systemRoleToPermissionMapper;


    @Override
    public List<SystemRoleModel> listRolesByUser(SystemUserModel user) {
        return systemRoleMapper.listRolesByUser(user);
    }

    @Override
    public SystemRoleModel getRoles(SystemRoleModel systemRoleModel) {
        return systemRoleMapper.getRoles(systemRoleModel);
    }

    @Override
    public IPage<SystemRoleModel> getRolesPage(Page page, SystemRoleModel systemRoleModel) {
        return systemRoleMapper.getRolesPage(page, systemRoleModel);
    }


    @Override
    public IPage<SystemRoleModel> findRole(Page<SystemRoleModel> page, SystemRoleModel systemRoleModel) throws Exception {


        QueryWrapper<SystemRoleModel> queryWrapper = new QueryWrapper<>();
        /**
         * 这里systemusermodel 不做空判断 。getusername 空指针  null.getUsername
         * 查询时间段，starttime，endtime为虚拟字段
         */
        if (ObjectUtil.isNotEmpty(systemRoleModel)) {
            if (StrUtil.isNotBlank(systemRoleModel.getRoleName())) {
                queryWrapper.lambda().eq(SystemRoleModel::getRoleName, systemRoleModel.getRoleName());
            }
            if (StrUtil.isNotBlank(systemRoleModel.getShowName())) {
                queryWrapper.lambda().eq(SystemRoleModel::getShowName, systemRoleModel.getShowName());
            }
            if (systemRoleModel.getCreateTime() != null) {
                queryWrapper.lambda().eq(SystemRoleModel::getCreateTime, systemRoleModel.getCreateTime());
            }
            if (systemRoleModel.getRoleStatus() != null) {
                queryWrapper.lambda().eq(SystemRoleModel::getRoleStatus, systemRoleModel.getRoleStatus());
            }
        }
        queryWrapper.lambda().orderByDesc(SystemRoleModel::getCreateTime);

        return systemRoleMapper.selectPage(page, queryWrapper);


    }


    @Override
    public IPage<SystemRoleModel> findRoleById(Page<SystemRoleModel> page, String roleId) throws Exception {
        IPage<SystemRoleModel> result = systemRoleMapper.selectRoleById(page, roleId);
        SystemRoleModel systemRoleModel = new SystemRoleModel();
        systemRoleModel.setRoleId(roleId);
///        IPage<SystemRoleModel> result = systemRoleMapper.getRolesPage(page,systemRoleModel);
        return result;
    }


    /**
     * 删除角色同时应当判断就是是否绑定权限、如绑定权限应当删除捆绑关系表中对应关系
     *
     * @param systemRoleModel
     * @return
     * @throws Exception
     */
    @Override
    public int dropRole(SystemRoleModel systemRoleModel) {
        systemRoleToPermissionMapper.deleteRoleToPermissionByRole(systemRoleModel);
        return systemRoleMapper.deleteFindFishRole(systemRoleModel);
    }


    /**
     * 测试角色名邮箱验证
     *
     * @param roleName
     * @return
     */
    @Override
    public boolean checkEmail(String roleName) throws Exception {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(roleName);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     * 根据用户信息获取用户角色信息
     *
     * @param systemUserModel
     * @return
     */
    @Override
    public List<SystemRoleModel> getRoleInfoByUser(SystemUserModel systemUserModel) {
        return systemRoleMapper.selectUserRoleByUser(systemUserModel);
    }

    @Override
    public Boolean checkRolerCouldDel(SystemRoleModel systemRoleModel) {

        try {
            SystemRoleModel roleModel = getRoles(systemRoleModel);
            if (roleModel.getAllowremove()) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }


    @Override
    public int addFindFishRole(SystemRoleModel systemRoleModel) {
        return systemRoleMapper.insertFindFishRole(systemRoleModel);
    }
}

