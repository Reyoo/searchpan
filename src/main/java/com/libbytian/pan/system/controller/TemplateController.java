package com.libbytian.pan.system.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import com.libbytian.pan.system.service.ISystemTmplToTmplDetailsService;
import com.libbytian.pan.system.service.ISystemUserToTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/template")
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TemplateController {

    private final ISystemTemplateService iSystemTemplateService;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;
    private final ISystemUserToTemplateService iSystemUserToTemplateService;




    /**
     * @param systemTemplateModel
     * @return
     * @Description: 查询所有模板、根据条件查询模板
     */

    @RequestMapping(value = "/findtemplate", method = RequestMethod.POST)
    public AjaxResult findAllTemplate(@RequestBody(required = false) SystemTemplateModel systemTemplateModel) {

        try {
            QueryWrapper<SystemTemplateModel> wrapper = new QueryWrapper();
            List<SystemTemplateModel> systemTemplateModels = null;
            if (systemTemplateModel == null) {
                systemTemplateModels = iSystemTemplateService.list();
            } else {

                if (StrUtil.isNotBlank(systemTemplateModel.getTemplateid())) {
                    wrapper.eq("template_id", systemTemplateModel.getTemplateid());
                }
                if (StrUtil.isNotBlank(systemTemplateModel.getTemplatename())) {
                    wrapper.eq("template_name", systemTemplateModel.getTemplatename());
                }

                systemTemplateModels = iSystemTemplateService.list(wrapper);
            }

            return AjaxResult.success(systemTemplateModels);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }




    /**
     * 删除模板
     * @param systemTemplateModel
     * @return
     */
    @RequestMapping(value = "/drop", method = RequestMethod.DELETE)
    @Transactional
    public AjaxResult removeTemplate( @RequestBody(required = true) SystemTemplateModel systemTemplateModel) {

        try {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("template_id", systemTemplateModel.getTemplateid());
//            1. 删除用户模板关联表中用
            iSystemUserToTemplateService.remove(queryWrapper);
            boolean isRemove = iSystemTemplateService.removeById(systemTemplateModel.getTemplateid());
            // 2. 删除模板
            // 3. 删除模板详细
            if(isRemove){
                iSystemTmplToTmplDetailsService.remove(queryWrapper);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 更新模板模板
     * @param systemTemplateModel
     * @return
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.PATCH)
    @Transactional
    public AjaxResult updateTemplate(@RequestBody(required = true) SystemTemplateModel systemTemplateModel) {

        try {
            boolean isupdate = iSystemTemplateService.updateById(systemTemplateModel);
            if(isupdate){
                return AjaxResult.success();
            }
           return AjaxResult.error();
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }



    /**
     * 获取用户所有的模板
     *
     * @return
     */
    @RequestMapping(value = "getusertemplate", method = RequestMethod.POST)
    public AjaxResult getuserTemplate(@RequestBody SystemUserModel systemUserModel) {

        try {
            List<SystemTemplateModel> result = iSystemTemplateService.listTemplatelByUser(systemUserModel);
            List<SystemTemplateModel> oldResult = new ArrayList<>();
            for (SystemTemplateModel systemTemplateModel : result) {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("template_id", systemTemplateModel.getTemplateid());
                systemTemplateModel.setDetialsize(iSystemTmplToTmplDetailsService.count(queryWrapper));
                oldResult.add(systemTemplateModel);
            }
            return AjaxResult.success(oldResult);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

}
