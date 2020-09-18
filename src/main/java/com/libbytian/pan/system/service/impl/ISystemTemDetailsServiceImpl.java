package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ISystemTemDetailsServiceImpl extends ServiceImpl<SystemTemDetailsMapper, SystemTemDetailsModel> implements ISystemTemDetailsService {


    private final SystemTemDetailsMapper systemTemDetailsMapper;

    @Override
    public int addTemDetails(String keyword, String keywordToValue) {

        LocalDateTime localDateTime = LocalDateTime.now();

       int result = systemTemDetailsMapper.addTemDetails(keyword,keywordToValue,localDateTime);

        return result;
    }

    @Override
    public IPage<SystemTemDetailsModel> findTemDetails(Page page) {

    }
}
