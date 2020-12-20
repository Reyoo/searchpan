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


    /**
     * 删除用户下的模板
     * 同时删除 用户模板表、模板表、模板详情表、详情表中对应数据
     * @param temped
     * @return
     */
    int deleteTemplateIdAll(String temped);

}
