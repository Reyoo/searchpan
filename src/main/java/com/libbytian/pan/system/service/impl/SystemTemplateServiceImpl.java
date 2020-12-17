package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemTemplateServiceImpl extends ServiceImpl<SystemTemplateMapper,SystemTemplateModel> implements ISystemTemplateService {

   private final SystemTemplateMapper systemTemplateMapper;


    @Override
    public List<SystemTemplateModel> listTemplatelByUser(SystemUserModel systemUserModel) throws Exception {
        return systemTemplateMapper.listTemplatelByUser(systemUserModel);
    }

//    /**
//     * 引入缓存机制 ，该接口待测试
//     * 这个地方应该加一个业务代码 即 当更新用户模板信息启用状态时，清除 userTemplate 缓存
//     * 否则会有bug 产生
//     * @param systemUserModel
//     * @param inUsed
//     * @return
//     * @throws Exception
//     */
//    @Override
//    @Cacheable(value = "userTemplate",key = "#systemUserModel.username" ,condition = "#inUsed")
//    public List<SystemTemplateModel> getTemplateModelByUser(SystemUserModel systemUserModel,Boolean inUsed) throws Exception {
//        List<SystemTemplateModel> systemTemplateModels =  systemTemplateMapper.listTemplatelByUser(systemUserModel);
//        if(inUsed){
//            List<SystemTemplateModel> systemTemplateModelListstatusOn = systemTemplateModels.stream().filter(systemTemplateModel -> systemTemplateModel.getTemplatestatus().equals(Boolean.TRUE)).collect(Collectors.toList());
//            return systemTemplateModelListstatusOn;
//        }else{
//            return systemTemplateModels;
//        }
//    }

    @Override
    public List<SystemTemplateModel> listTemplatelObjects(SystemTemplateModel systemTemplateModel) throws Exception {
        return systemTemplateMapper.listTemplatelObjects(systemTemplateModel);
    }

    /**
     * 校验用户是否已经有启用状态的模板 true  有,false 没有
     * @param systemUserModel
     * @return
     */
    @Override
    public boolean checkTemplateIsBinded(SystemUserModel systemUserModel) {
        List<SystemTemplateModel> systemTemplateModels =  systemTemplateMapper.listTemplatelByUserOnUse(systemUserModel);
        if(systemTemplateModels!=null&&systemTemplateModels.size()>=1){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
