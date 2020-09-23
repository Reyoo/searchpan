package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface SystemTemDetailsMapper extends BaseMapper<SystemTemDetailsModel> {


//     @Insert("INSERT INTO sys_temdetails (templatedetails_keyword  ,templatedetails_value  ,createtime  ,templatedetails_status) VALUES(#{keyword},#{keywordToValue},#{localDateTime},0)")
//     int  addTemDetails(String keyword , String keywordToValue , LocalDateTime localDateTime);


     @Insert("INSERT INTO sys_temdetails (templatedetails_keyword  ,templatedetails_value  ,createtime  ,templatedetails_status) VALUES(#{keyword},#{keywordToValue},#{localDateTime},0)")
     @Options(useGeneratedKeys = true, keyProperty = "user.temdetailsId")
     Integer  addTemDetails(String keyword , String keywordToValue , LocalDateTime localDateTime,SystemTemDetailsModel user);

     @Select("SELECT td.templatedetails_id AS temdetailsId,templatedetails_keyword AS keyword,templatedetails_value AS keywordToValue,td.createtime,templatedetails_status AS temdetailsstatus FROM sys_temdetails td LEFT JOIN tem_temdetails tt ON td.templatedetails_id = tt.templatedetails_id WHERE tt.template_id =1 ORDER BY td.templatedetails_status DESC,td.createtime DESC")
     IPage<SystemTemDetailsModel> selectTemDetails(Page page);


     @Insert("INSERT INTO sys_temdetails (templatedetails_keyword  ,templatedetails_value  ,createtime  ,templatedetails_status) VALUES(#{keyword},#{keywordToValue},#{localDateTime},#{temdetailsstatus})")
     Integer insertTemDetails(String keyword , String keywordToValue , LocalDateTime localDateTime,int temdetailsstatus);
}
