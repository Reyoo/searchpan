package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface SystemTemDetailsMapper extends BaseMapper<SystemTemDetailsModel> {


     @Insert("INSERT INTO sys_temdetails (templatedetails_keyword  ,templatedetails_value  ,createtime  ,templatedetails_status) VALUES(#{keyword},#{keywordToValue},#{localDateTime},0)")
     int  addTemDetails(String keyword , String keywordToValue , LocalDateTime localDateTime);


     //sql还未修改
     @Select("SELECT td.templatedetails_id AS temdetailsId,templatedetails_keyword,templatedetails_value,td.createtime,templatedetails_status FROM sys_temdetails td LEFT JOIN " +
             "tem_template tt ON td.templatedetails_id = tt.templatedetails_id LEFT JOIN" + "sys_template t ON tt.template_id = t.template_id LEFT JOIN " +
             "user_template ut ON t.template_id = ut.template_id LEFT JOIN sys_user u ON ut.user_id = u.user_id WHERE u.user_name = 'zhangshan' ORDER BY " +
             "td.templatedetails_status DESC,td.createtime DESC")
     IPage<SystemTemDetailsModel> selectTemDetails(Page page);
}
