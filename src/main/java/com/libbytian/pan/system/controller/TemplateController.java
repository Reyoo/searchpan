package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TemplateController {

    private final ISystemTemplateService iSystemTemplateService;


    /**
     * 根据模板ID查询模板
     * @param start
     * @param limit
     * @param templateid
     * @return
     */
    @RequestMapping(value = "template/find",method = RequestMethod.GET)
    public AjaxResult findTemById(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "10") int limit , @RequestParam int templateid){

        Page<SystemTemplateModel> page = new Page<>(start,limit);

        try {
            IPage<SystemTemplateModel> result = iSystemTemplateService.findTemById(page,templateid);

            return AjaxResult.success(result);
        } catch (Exception e) {

            return AjaxResult.error(e.getMessage());
        }
    }
}
