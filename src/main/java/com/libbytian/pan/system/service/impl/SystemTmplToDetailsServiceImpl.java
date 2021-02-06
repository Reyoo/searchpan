package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.mapper.SystemTemToTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import com.libbytian.pan.system.service.ISystemTmplToTmplDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemTmplToDetailsServiceImpl extends ServiceImpl<SystemTemToTemDetailsMapper, SystemTemToTemdetail> implements ISystemTmplToTmplDetailsService {

    private  final SystemTemToTemDetailsMapper systemTemToTemDetailsMapper;
    private final SystemTemDetailsMapper systemTemDetailsMapper;


    @Override
    public int addTemplateToTemplateDetail(List<SystemTemToTemdetail> systemTemToTemdetails) {
        return systemTemToTemDetailsMapper.insertTmeplateToTemplateDetails(systemTemToTemdetails);
    }



    @Override
    public int dropTemplateAndDetails(List<String> templateDetailsList ) {
        return  systemTemToTemDetailsMapper.deleteTemplateAndDetails(templateDetailsList);

    }
}
