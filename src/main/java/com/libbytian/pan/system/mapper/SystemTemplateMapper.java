package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemTemplateModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemTemplateMapper extends BaseMapper<SystemTemplateModel> {

    @Select("SELECT * FROM sys_template WHERE template_id = #{templateid}")
    IPage<SystemTemplateModel> selectTemById(Page page,int templateid);
}
