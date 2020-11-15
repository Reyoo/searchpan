package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SensitiveWordModel;
import com.libbytian.pan.system.service.ISystemSensitiveWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目名: pan
 * 文件名: SensitiveWordController
 * 创建者: HuangS
 * 创建时间:2020/10/20 23:53
 * 描述: TODO
 */

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/sensitiveWord")
@Slf4j
public class SensitiveWordController {

    private final ISystemSensitiveWordService iSystemSensitiveWordService;

    /**
     * 删除敏感词
     * 根据传入ID，批量删除敏感词
     * @param
     * @return
     */
    @RequestMapping(value = "/removeSensitiveWord" ,method = RequestMethod.DELETE)
    public AjaxResult removeSensitiveWord(@RequestBody List<Integer> ids){

        try {
            boolean result = iSystemSensitiveWordService.removeByIds(ids);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());

        }

    }

    /**
     * 保存敏感词
     * 必传 word type
     * @param sensitiveWordModel
     * @return
     */
    @RequestMapping(value = "/saveSensitiveWord" ,method = RequestMethod.POST)
    public AjaxResult saveSensitiveWord(@RequestBody SensitiveWordModel sensitiveWordModel){
        try {
            sensitiveWordModel.setCreateTime(LocalDateTime.now());
            boolean result =  iSystemSensitiveWordService.save(sensitiveWordModel);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return  AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 查询敏感词
     * @param sensitiveWordModel
     * @return
     */
    @RequestMapping(value = "/listSensitiveWordObjects" ,method = RequestMethod.POST)
    public AjaxResult listSensitiveWordObjects(@RequestBody SensitiveWordModel sensitiveWordModel){

        try {
            List<SensitiveWordModel> result =  iSystemSensitiveWordService.listSensitiveWordObjects(sensitiveWordModel);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }


    }


    /**
     * 导入excel
     * 敏感词导入数据库
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/excelimport", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestParam("file") MultipartFile multipartFile) {
        try {
            if(multipartFile.isEmpty()){
                return AjaxResult.error("上传失败，请选择文件");
            }
            iSystemSensitiveWordService.exportExceltoDb(multipartFile.getOriginalFilename(),multipartFile.getInputStream());
            return AjaxResult.success();
        }catch (Exception e){
            log.error(e.getMessage());
            return AjaxResult.error("上传失败，请选择文件");
        }
    }


    /**
     * 数据库敏感词去重
     * @return
     */
    @RequestMapping(value = "/removeRepeat" , method = RequestMethod.DELETE)
    public AjaxResult removeRepeat(){

        try {
            return AjaxResult.success(iSystemSensitiveWordService.removeRepeat());
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }
}
