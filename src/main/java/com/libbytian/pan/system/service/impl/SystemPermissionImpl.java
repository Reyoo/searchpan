package com.libbytian.pan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemPermissionMapper;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.service.ISystemPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：  权限业务实现类
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemPermissionImpl extends ServiceImpl<SystemPermissionMapper, SystemPermissionModel> implements ISystemPermissionService {

    private final SystemPermissionMapper systemPermissionMapper ;

    @Override
    public List<SystemPermissionModel> getPermissionByUsername(String username) throws Exception {
        return systemPermissionMapper.selectUserByUsername(username);
    }
}

