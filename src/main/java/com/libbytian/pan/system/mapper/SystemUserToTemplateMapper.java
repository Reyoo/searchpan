package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.model.SystemUserToTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemUserToTemplateMapper extends BaseMapper<SystemUserToTemplate> {


    /**
     * 通过用户ID查询绑定模板信息，看起来没啥用，感觉可删除
     * @param userId
     * @return
     */
    List<SystemUserToTemplate> listUserByUserid(String userId);
}
