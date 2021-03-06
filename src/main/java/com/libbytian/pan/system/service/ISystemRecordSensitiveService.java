package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemRecordSensitiveModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 项目名: pan
 * 文件名: ISystemRecordSensitiveService
 * 创建者: HuangS
 * 创建时间:2020/10/21 15:42
 * 描述: TODO
 */

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemRecordSensitiveService extends IService<SystemRecordSensitiveModel> {

}
