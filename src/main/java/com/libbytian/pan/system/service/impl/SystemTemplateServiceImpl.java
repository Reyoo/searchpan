package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemplateMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public IPage<SystemTemplateModel> findTemById(Page page, SystemTemplateModel systemTemplateModel) throws Exception {

        QueryWrapper queryWrapper = new QueryWrapper<>();
        if (systemTemplateModel != null){
            if (systemTemplateModel.getTemplateid()!=null){
                queryWrapper.eq("template_id",systemTemplateModel.getTemplateid());
            }
            if (systemTemplateModel.getTemplatename()!=null){
                queryWrapper.eq("template_name",systemTemplateModel.getTemplatename());
            }
            if (systemTemplateModel.getTemplatecreatetime()!=null){
                queryWrapper.eq("template_createtime",systemTemplateModel.getTemplatecreatetime());
            }
            if (systemTemplateModel.getTemplatelastupdate()!=null){
                queryWrapper.eq("template_lastupdate",systemTemplateModel.getTemplatelastupdate());
            }
            if (systemTemplateModel.getTemplatestatus()!=null){
                queryWrapper.eq("template_status",systemTemplateModel.getTemplatestatus());
            }

        }

            return  systemTemplateMapper.selectPage(page,queryWrapper);

    }

    @Override
    public Map findTemNameAndSize() {
        return null;
    }
}
