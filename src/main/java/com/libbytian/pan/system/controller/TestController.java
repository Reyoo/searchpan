package com.libbytian.pan.system.controller;


import cn.hutool.core.lang.UUID;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import com.libbytian.pan.system.service.ISystemTmplToTmplDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试类接口
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/test")
public class TestController {


    private final RedisTemplate redisTemplate;
    private final ISystemTemplateService iSystemTemplateService;
    private final SystemTemDetailsMapper systemTemDetailsMapper;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;

    @RequestMapping(value = "/getredis", method = RequestMethod.GET)
    public AjaxResult getRedisExprie() {

        try {
            Long expire = redisTemplate.boundHashOps("aidianying").getExpire();
            System.out.println("redis有效时间：" + expire + "S");
            return AjaxResult.success(expire);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }


//    @RequestMapping(value = "/addKeyword", method = RequestMethod.POST)
////    public AjaxResult addKeyword(@RequestBody String keyword , @RequestBody String keywordToValue){
//    public AjaxResult addKeyword(@RequestBody SystemTemDetailsModel details){
//
////        SystemTemDetailsModel details = new SystemTemDetailsModel();
////        details.setKeyword(keyword);
////        details.setKeywordToValue(keywordToValue);
//
//        //获取所有模板
//        List<SystemTemplateModel> allTemplate = iSystemTemplateService.getAllTemplate();
//        //遍历所有模板，将新增关键词存入 详情表，绑定中间表。
//        for (SystemTemplateModel templateModel : allTemplate) {
//            details.setTemdetailsId(UUID.randomUUID().toString());
//            details.setCreatetime(LocalDateTime.now());
//
//            //插入模板详情表
//            int result = systemTemDetailsMapper.insert(details);
//            if (result == 1) {
//                //插入模板_模板详情表
//                SystemTemToTemdetail temToDetails = SystemTemToTemdetail.builder().templateid(templateModel.getTemplateid()).templatedetailsid(details.getTemdetailsId()).build();
//                iSystemTmplToTmplDetailsService.save(temToDetails);
//            }
//        }
//        return AjaxResult.success("新增成功");
//
//    }
    }
