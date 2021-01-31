package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemNotifyModel;

import java.util.List;

/**
 * 系统通知业务类
 */
public interface ISystemNotifyService extends IService<SystemNotifyModel> {




    /**
     * 查询 返回多个通知信息
     * @param systemUserModel
     * @return
     */
    List<SystemNotifyModel> listSystemNotify(SystemNotifyModel systemUserModel) throws Exception;



    IPage<SystemNotifyModel> findConditionByPage(Page<SystemNotifyModel> page, SystemNotifyModel systemUserModel) throws Exception;



    int addSystemNotify(SystemNotifyModel systemNotifyModel) throws Exception;



    int removeSystemNoitfy(SystemNotifyModel systemNotifyModel) throws Exception;


    int updateSystemNoitfy(SystemNotifyModel systemNotifyModel) throws Exception;



}
