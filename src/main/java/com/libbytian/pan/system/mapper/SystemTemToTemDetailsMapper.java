package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemTemToTemDetailsMapper extends BaseMapper<SystemTemToTemdetail> {


    /**
     * 批量插入模板与模板绑定
     * @param systemTemToTemdetails
     * @return
     */
    int insertTmeplateToTemplateDetails(List<SystemTemToTemdetail> systemTemToTemdetails);



    /**
     * 删除模板与模板详细绑定关联表 绑定关系
     * @param templateDetailsList
     * @return
     */
    int deleteTemplateAndDetails(List<String> templateDetailsList);

}
