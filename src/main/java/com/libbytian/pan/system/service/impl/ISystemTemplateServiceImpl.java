package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemplateMapper;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ISystemTemplateServiceImpl extends ServiceImpl<SystemTemplateMapper,SystemTemplateModel> implements ISystemTemplateService {

   private final SystemTemplateMapper systemTemplateMapper;


    @Override
    public IPage<SystemTemplateModel> findTemById(Page page, int templateid) throws Exception {
       return systemTemplateMapper.selectTemById(page,templateid);
    }
}
