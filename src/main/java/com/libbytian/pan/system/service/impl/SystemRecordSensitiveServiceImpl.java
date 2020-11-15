package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRecordSensitiveMapper;
import com.libbytian.pan.system.model.SystemRecordSensitiveModel;
import com.libbytian.pan.system.service.ISystemRecordSensitiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目名: pan
 * 文件名: SystemRecordSensitiveServiceImpl
 * 创建者: HuangS
 * 创建时间:2020/10/21 16:34
 * 描述: TODO
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemRecordSensitiveServiceImpl extends ServiceImpl<SystemRecordSensitiveMapper, SystemRecordSensitiveModel> implements ISystemRecordSensitiveService {
}
