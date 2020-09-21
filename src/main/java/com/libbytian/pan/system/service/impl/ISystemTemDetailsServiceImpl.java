package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.TemToTemDetailsModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ITemToTemDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ISystemTemDetailsServiceImpl extends ServiceImpl<SystemTemDetailsMapper, SystemTemDetailsModel> implements ISystemTemDetailsService {


    private final SystemTemDetailsMapper systemTemDetailsMapper;
    private final ITemToTemDetailsService iTemToTemDetailsService;


    /**
     *
     * @param filename
     * @param inputStream
     * @return
     */
    @Override
    public int exportExceltoDb(String filename, InputStream inputStream) {
        return 0;
    }


    /**
     *
     * @param keyword
     * @param keywordToValue
     * @return
     */
    @Override
    public int addTemDetails(String keyword, String keywordToValue) {

        SystemTemDetailsModel user = new SystemTemDetailsModel();
        LocalDateTime localDateTime = LocalDateTime.now();
        user.setKeywordToValue(keywordToValue);
        user.setKeyword(keyword);
        user.setCreatetime(localDateTime);
        int result = systemTemDetailsMapper.addTemDetails(keyword, keywordToValue, localDateTime, user);
        int id = user.getTemdetailsId();
        if (result == 1) {
            TemToTemDetailsModel temToDetails = TemToTemDetailsModel.builder().templateid(1).templatedetailsid(id).build();
            iTemToTemDetailsService.save(temToDetails);
        }
        return result;
    }


    @Override
    public IPage<SystemTemDetailsModel> findTemDetails(Page page) {

        return systemTemDetailsMapper.selectTemDetails(page);
    }
}
