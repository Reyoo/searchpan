package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/template")
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TemplateController {

    private final ISystemTemplateService iSystemTemplateService;


    /**
     * 根据模板ID查询模板详情
     *
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/findTemDetails", method = RequestMethod.GET)
    public AjaxResult findTemDetails(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit, @RequestParam String templateId) {

        Page<SystemTemDetailsModel> findpage = new Page<>(page, limit);
        try {
            IPage<SystemTemDetailsModel> result = iSystemTemplateService.findTemDetailsPage(findpage,templateId);
            return AjaxResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 查询所有模板
     * @param page
     * @param limit
     * @return
     */

    @RequestMapping(value = "/findtemplate",method = RequestMethod.POST)
    public AjaxResult findTemById(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit , @RequestBody SystemTemplateModel systemTemplateModel){

        Page<SystemTemplateModel> findpage = new Page<>(page,limit);

        try {
            IPage<SystemTemplateModel> result = iSystemTemplateService.findTemById(findpage,systemTemplateModel);

            return AjaxResult.success(result);
        } catch (Exception e) {

            return AjaxResult.error(e.getMessage());
        }
    }





}
