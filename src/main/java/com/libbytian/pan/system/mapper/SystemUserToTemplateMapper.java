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
     * 不知道有啥用，先写着看，随时可删除
     * @param userId
     * @return
     */
    @Select("select user_template_id AS userTotemplateId ,user_id,role_id, user_template_status from user_template where user_id = #{0}")
    List<SystemUserToRole> selectUserByUserid(String userId);
}
