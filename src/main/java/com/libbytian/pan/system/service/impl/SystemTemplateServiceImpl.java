package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemplateMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemTemplateServiceImpl extends ServiceImpl<SystemTemplateMapper,SystemTemplateModel> implements ISystemTemplateService {

   private final SystemTemplateMapper systemTemplateMapper;


    @Override
    public IPage<SystemTemDetailsModel> findTemDetailsPage(Page page, String templateId) throws Exception {
        return systemTemplateMapper.selectTemDetailsPage(page,templateId);
    }

    /**
     * 不分页查询
     * @param templateId
     * @return
     * @throws Exception
     */
    @Override
    public List<SystemTemDetailsModel> findTemDetails(String templateId) throws Exception {
        return systemTemplateMapper.selectTemDetails(templateId);
    }

    @Override
    public List<SystemTemplateModel> getTemplateModelByUser(SystemUserModel systemUserModel) throws Exception {
        return systemTemplateMapper.findTemplateModelByUser(systemUserModel);
    }


}
