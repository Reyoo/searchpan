package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author: QiSun
 * @date: 2020-12-16
 * @Description:
 */


@Mapper
public interface SystemUserToKeywordMapper extends BaseMapper<SystemUserToKeyword> {

    int insertSysuserToKeyword(SystemUserToKeyword systemUserToKeyword);

    int deleteUserToKeywordByUser(SystemUserModel systemUserModel);

}
