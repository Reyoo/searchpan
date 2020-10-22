package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Mapper
@Repository
public interface SystemUserMapper extends BaseMapper<SystemUserModel> {


    SystemUserModel getUser(SystemUserModel systemUserModel);

    List<SystemUserModel> listUsers(SystemUserModel systemUserModel);


    void removeUserAll(SystemUserModel user);

    SystemUserModel getUserByUerToTemplate(String templateId);


}
