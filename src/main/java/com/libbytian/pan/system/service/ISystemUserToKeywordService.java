package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToKeyword;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: QiSun
 * @date: 2020-12-16
 * @Description:
 */


@Transactional(propagation = Propagation.REQUIRES_NEW)


public interface ISystemUserToKeywordService extends IService<SystemUserToKeyword> {

    /**
     * 新增用户关键字关联表
     * @param systemUserToKeyword
     * @return
     */
    int addUserToKeyword(SystemUserToKeyword systemUserToKeyword);

}
