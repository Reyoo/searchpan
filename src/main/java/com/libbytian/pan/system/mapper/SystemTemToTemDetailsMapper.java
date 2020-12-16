package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemTemToTemDetailsMapper extends BaseMapper<SystemTemToTemdetail> {

    int insertTmeplateToTemplateDetails(List<SystemTemToTemdetail> systemTemToTemdetails);

}
