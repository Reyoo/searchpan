package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemKeywordModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目名: pan
 * 文件名: SystemKeywordMapper
 * 创建者: HS
 * 创建时间:2020/12/14 16:07
 * 描述: TODO
 */
@Mapper
public interface SystemKeywordMapper extends BaseMapper<SystemKeywordModel> {


    SystemKeywordModel getKeywordByUser(String userId);

    void updateKeyword(SystemKeywordModel systemKeywordModel);
}
