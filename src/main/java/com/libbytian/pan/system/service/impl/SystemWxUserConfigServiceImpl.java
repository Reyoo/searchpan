package com.libbytian.pan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemWxUserConfigServiceMapper;
import com.libbytian.pan.system.model.SystemWxUserConfigModel;
import com.libbytian.pan.system.service.ISystemWxUserConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * *************************************************************************
 * <p/>
 *
 * @author SunQi
 * @文件名称: SystemWxUserConfigServiceImpl.java
 * @包 路 径： com.libbytian.pan.system.service.impl
 * @版权所有：北京数字认证股份有限公司 (C) 2018
 * @类描述: 身份证号校验
 * @版本: V1.0 @创建人：SunQi
 * @创建时间：2022/1/25 17:45
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemWxUserConfigServiceImpl extends ServiceImpl<SystemWxUserConfigServiceMapper, SystemWxUserConfigModel>  implements ISystemWxUserConfigService {

    private final SystemWxUserConfigServiceMapper systemWxUserConfigServiceMapper;


}
