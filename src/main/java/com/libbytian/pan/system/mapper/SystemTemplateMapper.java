package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemTemplateMapper extends BaseMapper<SystemTemplateModel> {

    @Select("SELECT td.templatedetails_id AS temdetailsId,templatedetails_keyword AS keyword,templatedetails_value AS keywordToValue,td.createtime,templatedetails_status AS temdetailsstatus FROM sys_temdetails td LEFT JOIN tem_temdetails tt ON td.templatedetails_id = tt.templatedetails_id WHERE tt.template_id =#{templateId} ORDER BY td.templatedetails_status DESC,td.createtime DESC")
    IPage<SystemTemDetailsModel> selectTemDetails(Page page, String templateId);


    @Select("SELECT td.templatedetails_id AS temdetailsId,templatedetails_keyword AS keyword,templatedetails_value AS keywordToValue,td.createtime,templatedetails_status AS temdetailsstatus FROM sys_temdetails td LEFT JOIN tem_temdetails tt ON td.templatedetails_id = tt.templatedetails_id WHERE tt.template_id =#{templateId} ORDER BY td.templatedetails_status DESC,td.createtime DESC")
    List<SystemTemDetailsModel> selectTemDetails(String templateId);

    @Select("SELECT * FROM sys_template WHERE  template_id =#{templateId}")
    IPage<SystemTemplateModel> findTemById(Page page, int templateId);


}
