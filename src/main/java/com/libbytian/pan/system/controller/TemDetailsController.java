package com.libbytian.pan.system.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.service.ISystemSensitiveWordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemTemplateService;
import com.libbytian.pan.system.service.ISystemTmplToTmplDetailsService;
import com.libbytian.pan.system.util.CheckStrContainUrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@RequestMapping("/details")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
/**
 * 模板详细接口
 */
public class TemDetailsController {

    private final ISystemTemDetailsService iSystemTemDetailsService;

    private final ISystemSensitiveWordService iSystemSensitiveWordService;

    private final ISystemTmplToTmplDetailsService systemTmplToTmplDetailsService;



    /**
     * 根据模板ID查询模板详情
     *
     * @param page
     * @param limit
     * @return
     */


    @RequestMapping(value = "/getTempWithId", method = RequestMethod.GET)
    public AjaxResult findTemDetails(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit, @RequestParam String templateId) {
        LocalDateTime begin = LocalDateTime.now();

        Page<SystemTemDetailsModel> findpage = new Page<>(page, limit);
        try {
            IPage<SystemTemDetailsModel> result = iSystemTemDetailsService.findTemDetailsPage(findpage, templateId);
            LocalDateTime end = LocalDateTime.now();
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * HuangS
     * 输入key 或者 value 查询模板ID下模板详情
     * 必传字段 templateid
     * 未分页
     * 是否会有分页bug ?  Qi
     * @param systemTemDetailsModel
     * @return
     */
    @RequestMapping(value = "listTemDetailsObjectsByWord", method = RequestMethod.POST)
    public AjaxResult listTemDetailsObjectsByWord(@RequestBody SystemTemDetailsModel systemTemDetailsModel) {

        try {
            List<SystemTemDetailsModel> result = iSystemTemDetailsService.listTemDetailsObjectsByWord(systemTemDetailsModel);

            if (result.size() > 0) {
                return AjaxResult.success(result);
            } else {
                return AjaxResult.error("未搜索到该内容");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return AjaxResult.error("查询错误");
        }
    }


    /**
     * 添加关键字及对应回复
     *
     * @param systemTemDetailsModel 传入 keyword，keywordToValue,templateId(存入的模板)
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestBody SystemTemDetailsModel systemTemDetailsModel,HttpServletRequest httpRequest) {

        try {

            if (iSystemSensitiveWordService.isContaintSensitiveWord(systemTemDetailsModel)) {

                return AjaxResult.error("包含敏感词请重新填写");
            }

            //对前端标签进行转义处理
            systemTemDetailsModel.setKeywordToValue(HtmlUtils.htmlUnescape(systemTemDetailsModel.getKeywordToValue()));

            iSystemTemDetailsService.addTemDetails(systemTemDetailsModel, systemTemDetailsModel.getTemplateId(),httpRequest.getRemoteUser());
            return AjaxResult.success("添加成功");

        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 更新关键字及对应回复
     *
     * @param systemTemDetailsModel
     * @return
     */
    @RequestMapping(value = "/updateTemDetails", method = RequestMethod.PATCH)
    public AjaxResult updateTemDetails(@RequestBody SystemTemDetailsModel systemTemDetailsModel,HttpServletRequest httpRequest) {

        try {
            if (StrUtil.isBlank(systemTemDetailsModel.getTemdetailsId())) {
                return AjaxResult.error("模板ID不能为空");
            }
            //修改时，对前端标签进行转义处理
            if(StringUtils.isNotBlank(systemTemDetailsModel.getKeywordToValue())){

                systemTemDetailsModel.setKeywordToValue(HtmlUtils.htmlUnescape(systemTemDetailsModel.getKeywordToValue()));
            }

            iSystemTemDetailsService.updateTempDetailsWithModel(systemTemDetailsModel,httpRequest.getRemoteUser());


            return AjaxResult.success();

        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }




    /**
     * 根据ID删除对应模板详情
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/removeTemDetails", method = RequestMethod.DELETE)
    public AjaxResult deleteTemDetails(@RequestBody List<String> temdetailsId,HttpServletRequest httpRequest) {
        try {
            if (iSystemTemDetailsService.deleteTemplateDetails(temdetailsId,httpRequest.getRemoteUser()) > 0) {
                //删除关联表   tem_temdetails
                systemTmplToTmplDetailsService.dropTemplateAndDetails(temdetailsId);

                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("该字段为系统保留字段，不允许删除");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 导入excel入库并绑定模板ID
     * 未完成，如果导入数据有关键字，更新关键字。
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/excelimport", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestParam("file") MultipartFile multipartFile, @RequestParam String templateId,HttpServletRequest httpRequest) {

        //判断当前是否存在模板，如果没有模板不允许导入    HuangS
        if (StringUtils.isBlank(templateId)) {
            return AjaxResult.error("导入文件失败,请先创建模板!!!");
        }

        if (multipartFile.isEmpty()) {
            return AjaxResult.error("上传失败，请选择文件!!!");
        }

        try {

            iSystemTemDetailsService.exportExceltoDb(multipartFile.getOriginalFilename(), multipartFile.getInputStream(), templateId,httpRequest.getRemoteUser());
            return AjaxResult.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error("上传失败,请联系开发领奖!!!");
        }
    }

    /**
     * 导出用户模板详细
     *
     * @param httpServletRequest
     * @param temdetailsIds
     * @return
     */
    @RequestMapping(value = "/excelexport", method = RequestMethod.POST)
    public void exportTemDetails(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody List<String> temdetailsIds) {

        try {
            if (temdetailsIds.size() == 0) {
                return;
            }
            iSystemTemDetailsService.exportTemDetails(httpServletRequest, httpServletResponse, temdetailsIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 校验按钮 url 是否是可用的
     *
     * @param detailId
     */
    @RequestMapping(value = "/checkdetail/{detailId}", method = RequestMethod.GET)
    public AjaxResult exportTemDetails(@PathVariable String detailId) {
        try {
            List<String> urlList = new ArrayList<>();
            SystemTemDetailsModel systemTemDetailsModel = iSystemTemDetailsService.getById(detailId);
            String answerStr = systemTemDetailsModel.getKeywordToValue();
            if (answerStr.contains("http")) {
                Matcher matcher = CheckStrContainUrlUtil.WEB_URL.matcher(answerStr);
                while (matcher.find()) {
                    //循环输出所有匹配到的链接,并加上链接
                    String link = matcher.group();
                    urlList.add(link);
                }
                return AjaxResult.success(urlList);
            } else {
                return AjaxResult.error("无效URL");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error("无效URL");
        }
    }




}
