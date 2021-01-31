package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemNotifyModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemNotifyServiceMapper extends BaseMapper<SystemNotifyModel> {


    /**
     * 查询 返回多个通知信息
     * @param systemUserModel
     * @return
     */
    List<SystemNotifyModel> listSystemNotify(SystemNotifyModel systemUserModel)  ;

    int deleteSystemNoitfy(SystemNotifyModel systemNotifyModel)  ;

    int updateSystemNoitfy(SystemNotifyModel systemNotifyModel)  ;

    int insertSystemNotify(SystemNotifyModel systemNotifyModel);


}
