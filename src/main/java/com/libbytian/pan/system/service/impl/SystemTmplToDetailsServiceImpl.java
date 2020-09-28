package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.TemToTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemToTemdetails;
import com.libbytian.pan.system.service.ISystemTmplToTmplDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemTmplToDetailsServiceImpl extends ServiceImpl<TemToTemDetailsMapper, SystemTemToTemdetails> implements ISystemTmplToTmplDetailsService {

}
