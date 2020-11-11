package com.libbytian.pan.system.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToTemplate;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/template")
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TemplateController {

    private final ISystemTemplateService iSystemTemplateService;
    private final ISystemUserService iSystemUserService;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;
    private final ISystemUserToTemplateService iSystemUserToTemplateService;

    private final ISystemTemDetailsService iSystemTemDetailsService;




    /**
     * @param systemTemplateModel
     * @return
     * @Description: 查询所有模板、根据条件查询模板
     */

    @RequestMapping(value = "/findtemplate", method = RequestMethod.POST)
    public AjaxResult findAllTemplate(@RequestBody(required = false) SystemTemplateModel systemTemplateModel) throws Exception {

        try {
            List<SystemTemplateModel> result = iSystemTemplateService.listTemplatelObjects(systemTemplateModel);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }




    /**
     * 删除用户模板
     * hs
     * 同时删除 用户模板表、模板表、模板详情表、详情表中对应数据
     * @param tempid
     * @return
     */
    @RequestMapping(value = "/drop/{tempid}", method = RequestMethod.DELETE)
    @Transactional
    public AjaxResult removeTemplate( @PathVariable(value = "tempid") String tempid){

        try {

            iSystemUserToTemplateService.removieTemplateIdAll(tempid);

            return AjaxResult.success();
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
    @RequestMapping(value = "getuser", method = RequestMethod.GET)
    public AjaxResult getuserTemplate(HttpServletRequest httpServletRequest) {

        try {
            SystemUserModel systemUserModel = new SystemUserModel();
            systemUserModel.setUsername(httpServletRequest.getRemoteUser());
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
            if(StrUtil.isBlank(systemTemplateModel.getTemplateid())){
                return AjaxResult.error("不能为空");
            }
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
     * 新增用户模板表
     * hs
     * 新增模板设置默认关键字
     * @param
     * @return
     */
    @RequestMapping(value = "/addtouser", method = RequestMethod.POST)
    @Transactional
    public AjaxResult finduserTemplate(HttpServletRequest httpRequest, @RequestBody(required = true) SystemTemplateModel systemTemplateModel) {

        try {
            String templateId = UUID.randomUUID().toString();
            systemTemplateModel.setTemplateid(templateId);
            systemTemplateModel.setTemplatecreatetime(LocalDateTime.now());
            iSystemTemplateService.save(systemTemplateModel);

            /**
             * 插入模板 后  用户绑定 用户模板表
             */
            String username = httpRequest.getRemoteUser();
            SystemUserModel userModel = new SystemUserModel();
            userModel.setUsername(username);
            SystemUserModel systemUserModel = iSystemUserService.getUser(userModel);

            SystemUserToTemplate systemUserToTemplate = new SystemUserToTemplate();
            systemUserToTemplate.setUserId(systemUserModel.getUserId());
            systemUserToTemplate.setTemplateId(templateId);
            systemUserToTemplate.setUserTemplateStatus(true);
            iSystemUserToTemplateService.save(systemUserToTemplate);

            //新增模板设置默认关键字
            iSystemTemDetailsService.defaultSave(templateId);


            return AjaxResult.success();

        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }
}
