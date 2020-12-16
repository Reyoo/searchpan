package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemplateMapper;
import com.libbytian.pan.system.mapper.SystemUserToKeywordMapper;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserToKeyword;
import com.libbytian.pan.system.service.ISystemTemplateService;
import com.libbytian.pan.system.service.ISystemUserToKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: QiSun
 * @date: 2020-12-16
 * @Description:
 */


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class SystemUserToKeywordServiceImpl extends ServiceImpl<SystemUserToKeywordMapper, SystemUserToKeyword> implements ISystemUserToKeywordService {

    private final SystemUserToKeywordMapper systemUserToKeywordMapper;

    @Override
    public int addUserToKeyword(SystemUserToKeyword systemUserToKeyword) {
        return systemUserToKeywordMapper.insertSysuserToKeyword(systemUserToKeyword);
    }
}
