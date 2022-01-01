package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemNotifyServiceMapper;
import com.libbytian.pan.system.model.SystemNotifyModel;
import com.libbytian.pan.system.service.ISystemNotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author SunQi
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemNotifyServiceImpl extends ServiceImpl<SystemNotifyServiceMapper, SystemNotifyModel> implements ISystemNotifyService {

    private final SystemNotifyServiceMapper systemNotifyServiceMapper;


    @Override
    public List<SystemNotifyModel> listSystemNotify(SystemNotifyModel systemUserModel) throws Exception {
        return systemNotifyServiceMapper.listSystemNotify(systemUserModel);
    }

    @Override
    public IPage<SystemNotifyModel> findConditionByPage(Page<SystemNotifyModel> page, SystemNotifyModel systemNotifyModel) throws Exception {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (systemNotifyModel != null) {
            if (systemNotifyModel.notifyText() != null && !systemNotifyModel.notifyText().equals("")) {
                queryWrapper.eq("notify_text", systemNotifyModel.notifyText());
            }
        }
        return baseMapper.selectPage(page, queryWrapper).addOrder(OrderItem.desc("modify_date"));
    }

    /**
     * 新增通知
     * @param systemNotifyModel
     * @return
     * @throws Exception
     */
    @Override
    public int addSystemNotify(SystemNotifyModel systemNotifyModel) throws Exception {
        return systemNotifyServiceMapper.insertSystemNotify(systemNotifyModel);
    }

    /**
     * 删除通知
     * @param systemNotifyModel
     * @return
     * @throws Exception
     */
    @Override
    public int removeSystemNoitfy(SystemNotifyModel systemNotifyModel) throws Exception {
        return systemNotifyServiceMapper.deleteSystemNoitfy(systemNotifyModel);
    }

    /**
     * 更新通知
     * @param systemNotifyModel
     * @return
     * @throws Exception
     */
    @Override
    public int updateSystemNoitfy(SystemNotifyModel systemNotifyModel) throws Exception {
        return systemNotifyServiceMapper.updateSystemNoitfy(systemNotifyModel);
    }


}
