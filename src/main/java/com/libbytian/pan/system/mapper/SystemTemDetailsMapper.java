package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemTemDetailsMapper extends BaseMapper<SystemTemDetailsModel> {

    @Select("SELECT td.templatedetails_id AS temdetailsId,templatedetails_keyword AS keyword,templatedetails_value AS keywordToValue,td.createtime,templatedetails_status AS temdetailsstatus " +
            "FROM sys_temdetails td " +
            "LEFT JOIN tem_temdetails tt " +
            "ON td.templatedetails_id = tt.templatedetails_id " +
            "WHERE tt.template_id =#{templateId} " +
            "ORDER BY " +
            "td.templatedetails_status DESC,td.createtime DESC , td.show_order")
    IPage<SystemTemDetailsModel> selectTemDetailsPage(Page page, String templateId);


    List<SystemTemDetailsModel> getTemDetails(SystemTemplateModel systemTemplateModel);

    List<SystemTemDetailsModel> findTemDetailsByUser(SystemUserModel systemUserModel);

    List<SystemTemDetailsModel> listTemDetailsObjectsByWord(SystemTemDetailsModel systemTemDetailsModel);




    SystemTemDetailsModel selectUserKeywordDetail(String username,String keyword);



}
