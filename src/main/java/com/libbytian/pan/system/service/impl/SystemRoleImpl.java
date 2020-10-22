package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemRoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemRoleImpl extends ServiceImpl<SystemRoleMapper, SystemRoleModel> implements ISystemRoleService {

    private final SystemRoleMapper systemRoleMapper ;


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
        return systemRoleMapper.getRolesPage(page,systemRoleModel);
    }


    @Override
    public IPage<SystemRoleModel> findRole(Page<SystemRoleModel> page, SystemRoleModel systemRoleModel) throws Exception {

//        QueryWrapper queryWrapper = new QueryWrapper();
//
//        /**
//         * 这里systemusermodel 不做空判断 。getusername 空指针  null.getUsername
//         * 查询时间段，starttime，endtime为虚拟字段
//         */
//        if(systemRoleModel != null){
//            if(systemRoleModel.getRoleName() != null){
//                queryWrapper.eq("role_name",systemRoleModel.getRoleName());
//            }
//
//            if(systemRoleModel.getShowName() != null){
//                queryWrapper.eq("show_name",systemRoleModel.getShowName());
//            }
//
//            if(systemRoleModel.getCreateTime() != null){
//                queryWrapper.eq("createtime",systemRoleModel.getCreateTime());
//            }
//
//            if(systemRoleModel.getStarttime() != null && systemRoleModel.getEndtime() != null){
//                queryWrapper.ge("createtime",systemRoleModel.getStarttime());
//                queryWrapper.le("createtime",systemRoleModel.getEndtime());
//            }
//
//        }
//        queryWrapper.orderByDesc("createtime");
//
//        return systemRoleMapper.selectPage(page,queryWrapper);

        return systemRoleMapper.getRolesPage(page,systemRoleModel);

    }


    @Override
    public IPage<SystemRoleModel> findRoleById(Page<SystemRoleModel> page, String roleId) throws Exception  {

///        IPage<SystemRoleModel> result = systemRoleMapper.selectRoleById(page,roleId);
//        IPage<SystemRoleModel> result = systemRoleMapper.selectRoleById(page,roleId);
        SystemRoleModel systemRoleModel = new SystemRoleModel();
        systemRoleModel.setRoleId(roleId);

        IPage<SystemRoleModel> result = systemRoleMapper.getRolesPage(page,systemRoleModel);

        return result;
    }



    @Override
    public int dropRole(String roleId) throws Exception {
        return systemRoleMapper.deleteById(roleId);
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


    /**
     * 根据用户信息获取用户角色信息
     * @param systemUserModel
     * @return
     */
    @Override
    public List<SystemRoleModel> getRoleInfoByUser(SystemUserModel systemUserModel) {
        return systemRoleMapper.selectUserRoleByUser(systemUserModel);
    }
}

