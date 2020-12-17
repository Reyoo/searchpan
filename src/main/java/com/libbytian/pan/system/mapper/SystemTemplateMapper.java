package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface SystemTemplateMapper extends BaseMapper<SystemTemplateModel> {


    List<SystemTemplateModel> listTemplatelByUser(SystemUserModel systemUserModel);

    /**
     * 查询用户在使用模板
     * @param systemUserModel
     * @return
     */
    List<SystemTemplateModel> listTemplatelByUserOnUse(SystemUserModel systemUserModel);


    List<SystemTemplateModel> listTemplatelObjects(SystemTemplateModel systemTemplateModel);


}
