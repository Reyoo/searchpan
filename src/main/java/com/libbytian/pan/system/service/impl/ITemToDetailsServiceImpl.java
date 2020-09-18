package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.TemToTemDetailsMapper;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.model.TemToTemDetailsModel;
import com.libbytian.pan.system.service.ITemToTemDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ITemToDetailsServiceImpl extends ServiceImpl<TemToTemDetailsMapper, TemToTemDetailsModel> implements ITemToTemDetailsService {

}
