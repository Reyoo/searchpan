package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserToTemplateMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.model.SystemUserToTemplate;
import com.libbytian.pan.system.service.ISystemUserToTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserToTemplateServiceImpl extends ServiceImpl<SystemUserToTemplateMapper, SystemUserToTemplate> implements ISystemUserToTemplateService {


    private final SystemUserToTemplateMapper systemUserToTemplateMapper;



    @Override
    public int removieTemplateIdAll(String tempid) {
        return systemUserToTemplateMapper.deleteTemplateIdAll(tempid);
    }

    @Override
    public int dropUserToTemplateByUserId(SystemUserModel systemUserModel) throws Exception {
        return systemUserToTemplateMapper.deleteUserToTemplateByUserId(systemUserModel);
    }

}
