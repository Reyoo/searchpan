package com.libbytian.pan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemKeywordMapper;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目名: pan
 * 文件名: KeywordServiceImpl
 * 创建者: HS
 * 创建时间:2020/12/14 16:04
 * 描述: TODO
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemKeywordServiceImpl extends ServiceImpl<SystemKeywordMapper, SystemKeywordModel> implements ISystemKeywordService {

    private final SystemKeywordMapper systemKeywordMapper ;


    @Override
    public SystemKeywordModel getKeywordByUser(String username ) {
        return systemKeywordMapper.getKeywordByUser(username);
    }

    @Override
    public void updateKeyword(SystemKeywordModel systemKeywordModel) {
        systemKeywordMapper.updateKeyword(systemKeywordModel);
    }

    @Override
    public int addkeyword(SystemKeywordModel systemKeywordModel) {
        return systemKeywordMapper.insertKeyword(systemKeywordModel);
    }
    @Override
    public int dropKeywordByUser(SystemUserModel systemUserModel) {
        return systemKeywordMapper.deleteKeywordByUser(systemUserModel);
    }


}
