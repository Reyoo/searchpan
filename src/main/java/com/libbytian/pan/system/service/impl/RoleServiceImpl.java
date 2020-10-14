package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRoleModel> implements IRoleService {

    private final SystemRoleMapper roleMapper;

    @Override
    public IPage<SystemRoleModel> findRole(Page<SystemRoleModel> page, SystemRoleModel systemRoleModel) throws Exception {

        QueryWrapper queryWrapper = new QueryWrapper();

        /**
         * 这里systemusermodel 不做空判断 。getusername 空指针  null.getUsername
         * 查询时间段，starttime，endtime为虚拟字段
         */
        if(systemRoleModel != null){
            if(systemRoleModel.getRoleName() != null){
                queryWrapper.eq("role_name",systemRoleModel.getRoleName());
            }

            if(systemRoleModel.getShowName() != null){
                queryWrapper.eq("show_name",systemRoleModel.getShowName());
            }

            if(systemRoleModel.getCreatetime() != null){
                queryWrapper.eq("createtime",systemRoleModel.getCreatetime());
            }

            if(systemRoleModel.getStarttime() != null && systemRoleModel.getEndtime() != null){
                queryWrapper.ge("createtime",systemRoleModel.getStarttime());
                queryWrapper.le("createtime",systemRoleModel.getEndtime());
            }

        }
        queryWrapper.orderByDesc("createtime");

        return roleMapper.selectPage(page,queryWrapper);

    }

    @Override
    public IPage<SystemUserModel> findUserByRole(Page<SystemRoleModel> page, String roleName)  throws Exception {

        IPage<SystemUserModel> result = roleMapper.selectUserByRole(page,roleName);

        return result;

    }

    @Override
    public IPage<SystemRoleModel> findRoleById(Page<SystemRoleModel> page, String roleId) throws Exception  {

       IPage<SystemRoleModel> result = roleMapper.selectRoleById(page,roleId);

        return result;
    }

    @Override
    public int roleNameCount(String roleName) {

        return roleMapper.roleNameCount(roleName);
    }

    @Override
    public int addRole(SystemRoleModel role) throws Exception {

        String roleId = role.getRoleId();
        String rolename =  role.getRoleName();
        LocalDateTime localDateTime = LocalDateTime.now();
        return roleMapper.insert(role);
    }

    @Override
    public int putRole(SystemRoleModel role) {

        String roleId = role.getRoleId();
        String rolename =  role.getRoleName();
        return roleMapper.update(roleId,rolename);
    }

    @Override
    public int dropRole(String roleId) throws Exception {
        return roleMapper.deleteById(roleId);
    }


    /**
     * 测试角色名邮箱验证
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
        return  flag;
    }
}
