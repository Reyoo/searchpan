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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Cacheable(value = "userTemplateDetail" ,key = "#templateId")
    public List<SystemTemDetailsModel> findTemDetails(String templateId) throws Exception {
        return systemTemplateMapper.selectTemDetails(templateId);
    }

    /**
     * 引入缓存机制 ，该接口待测试
     * 这个地方应该加一个业务代码 即 当更新用户模板信息启用状态时，清除 userTemplate 缓存
     * 否则会有bug 产生
     * @param systemUserModel
     * @param inUsed
     * @return
     * @throws Exception
     */
    @Override
    @Cacheable(value = "userTemplate",key = "#systemUserModel.username" ,condition = "#inUsed")
    public List<SystemTemplateModel> getTemplateModelByUser(SystemUserModel systemUserModel,Boolean inUsed) throws Exception {
        List<SystemTemplateModel> systemTemplateModels =  systemTemplateMapper.findTemplateModelByUser(systemUserModel);
        if(inUsed){
            List<SystemTemplateModel> systemTemplateModelListstatusOn = systemTemplateModels.stream().filter(systemTemplateModel -> systemTemplateModel.getTemplatestatus().equals(Boolean.TRUE)).collect(Collectors.toList());
            return systemTemplateModelListstatusOn;
        }else{
            return systemTemplateModels;
        }
    }

//    @Override
//    public IPage<SystemTemplateModel> findTemById(Page page, SystemTemplateModel systemTemplateModel) throws Exception {
//
//        QueryWrapper queryWrapper = new QueryWrapper<>();
//        if (systemTemplateModel != null){
//            if (systemTemplateModel.getTemplateid()!=null){
//                queryWrapper.eq("template_id",systemTemplateModel.getTemplateid());
//            }
//            if (systemTemplateModel.getTemplatename()!=null){
//                queryWrapper.eq("template_name",systemTemplateModel.getTemplatename());
//            }
//            if (systemTemplateModel.getTemplatecreatetime()!=null){
//                queryWrapper.eq("template_createtime",systemTemplateModel.getTemplatecreatetime());
//            }
//            if (systemTemplateModel.getTemplatelastupdate()!=null){
//                queryWrapper.eq("template_lastupdate",systemTemplateModel.getTemplatelastupdate());
//            }
//            if (systemTemplateModel.getTemplatestatus()!=null){
//                queryWrapper.eq("template_status",systemTemplateModel.getTemplatestatus());
//            }
//
//        }
//
//            return  systemTemplateMapper.selectPage(page,queryWrapper);
//
//    }


}
