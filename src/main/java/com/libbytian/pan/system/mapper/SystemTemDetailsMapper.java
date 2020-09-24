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


//     @Insert("INSERT INTO sys_temdetails (templatedetails_keyword  ,templatedetails_value  ,createtime  ,templatedetails_status) VALUES(#{keyword},#{keywordToValue},#{localDateTime},0)")
//     @Options(useGeneratedKeys = true, keyProperty = "user.temdetailsId")
//     Integer  addTemDetails(String keyword , String keywordToValue , LocalDateTime localDateTime,SystemTemDetailsModel user);

     @Insert("INSERT INTO sys_temdetails (templatedetails_id,templatedetails_keyword  ,templatedetails_value  ,createtime  ,templatedetails_status) VALUES(#{uuid},#{keyword},#{keywordToValue},#{localDateTime},0)")
     @Options(useGeneratedKeys = true, keyProperty = "systemTemDetailsModel.temdetailsId")
     Integer  addTemDetails(String uuid , String keyword , String keywordToValue , LocalDateTime localDateTime,SystemTemDetailsModel systemTemDetailsModel);


}
