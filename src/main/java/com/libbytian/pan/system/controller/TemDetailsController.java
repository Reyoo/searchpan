package com.libbytian.pan.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemRecordSensitiveModel;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.service.ISystemRecordSensitiveService;
import com.libbytian.pan.system.service.ISystemSensitiveWordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.system.util.CheckStrContainUrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final ISystemRecordSensitiveService iSystemRecordSensitiveService;

    private final ISystemUserService iSystemUserService;

    /**
     * 根据模板ID查询模板详情
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/getTempWithId", method = RequestMethod.GET)
    public AjaxResult findTemDetails(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit, @RequestParam String templateId) {

        Page<SystemTemDetailsModel> findpage = new Page<>(page, limit);
        try {
            IPage<SystemTemDetailsModel> result = iSystemTemDetailsService.findTemDetailsPage(findpage, templateId);
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 添加关键字及对应回复
     * @param systemTemDetailsModel
     * 传入 keyword，keywordToValue,templateId(存入的模板)
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestBody SystemTemDetailsModel systemTemDetailsModel) {

        try {

            if(iSystemSensitiveWordService.isContaintSensitiveWord(systemTemDetailsModel)){
//            if(iSystemSensitiveWordService.isContaintSensitiveWord(systemTemDetailsModel.getKeyword(),systemTemDetailsModel.getTemdetailsId())){

//                //敏感词存入
//                SystemRecordSensitiveModel record = new SystemRecordSensitiveModel();
//                record.setRecordSaveTime(LocalDateTime.now());
//                record.setRecordWord(systemTemDetailsModel.getKeyword());
//                //通过templateId查询到username
//                record.setRecordUsername(iSystemUserService.getUserByUerToTemplate(systemTemDetailsModel.getTemplateId()).getUsername());
//
//                iSystemRecordSensitiveService.save(record);

                return AjaxResult.error("包含敏感词请重新填写");
            }

            int result = iSystemTemDetailsService.addTemDetails(systemTemDetailsModel, systemTemDetailsModel.getTemplateId());
            if (result == 1) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error("添加失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 更新关键字及对应回复
     * @param systemTemDetailsModel
     * @return
     */
    @RequestMapping(value = "/updateTemDetails", method = RequestMethod.PATCH)
    public AjaxResult updateTemDetails(@RequestBody SystemTemDetailsModel systemTemDetailsModel) {

        try {
            if(StrUtil.isBlank(systemTemDetailsModel.getTemdetailsId())){
                return AjaxResult.error("模板ID不能为空");
            }
            if (iSystemTemDetailsService.updateById(systemTemDetailsModel)) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error("修改失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据ID删除对应模板详情
     * @param
     * @return
     */
    @RequestMapping(value = "/removeTemDetails", method = RequestMethod.DELETE)
    public AjaxResult deleteTemDetails(@RequestBody List<String>  temdetailsId) {
        try {
            if(iSystemTemDetailsService.removeByIds(temdetailsId)){
                return AjaxResult.success("删除成功");
            }else {
                return AjaxResult.success("删除失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 导入excel入库并绑定模板ID
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/excelimport", method = RequestMethod.POST)
    public AjaxResult addTemDetails( @RequestParam("file")  MultipartFile multipartFile ,@RequestParam String templateId) {
        try {
            if(multipartFile.isEmpty()){
                return AjaxResult.error("上传失败，请选择文件");
            }
            iSystemTemDetailsService.exportExceltoDb(multipartFile.getOriginalFilename(),multipartFile.getInputStream(),templateId);
            return AjaxResult.success();
        }catch (Exception e){
            log.error(e.getMessage());
            return AjaxResult.error("上传失败，请选择文件");
        }
    }

    /**
     * 导出用户模板详细
     * @param httpServletRequest
     * @param templateId
     * @return
     */
    @RequestMapping(value = "/excelexport/{templateId}", method = RequestMethod.GET)
    public void exportTemDetails(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @PathVariable String templateId) {

        try {
            iSystemTemDetailsService.exportTemDetails(httpServletRequest, httpServletResponse, templateId);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 校验按钮 url 是否是可用的
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
            }else {
                return AjaxResult.error("无效URL");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error("无效URL");
        }
    }


}
