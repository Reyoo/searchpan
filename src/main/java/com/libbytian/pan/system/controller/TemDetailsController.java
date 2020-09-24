package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/details")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TemDetailsController {

    private final ISystemTemDetailsService iSystemTemDetailsService;


    /**
     * 添加关键字及对应回复
     * @param systemTemDetailsModel 传入 key，value
     * @param templateId  存入的模板
     * @return
     */
    @RequestMapping(value = "/addTemDetails", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestBody SystemTemDetailsModel systemTemDetailsModel , @RequestParam String templateId) {

        try {
            int result = iSystemTemDetailsService.addTemDetails(systemTemDetailsModel, templateId);
            if (result == 1) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error("添加失败！");
            }
        } catch (Exception e) {
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
            int result = iSystemTemDetailsService.updateTemDetails(systemTemDetailsModel);
            if (result == 1) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error("修改失败！");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }


    }

    /**
     * 根据ID删除对应模板详情
     * @param
     * @return
     */
    @RequestMapping(value = "/removeTemDetails", method = RequestMethod.DELETE)
    public AjaxResult deleteTemDetails(@RequestParam int temdetailsId) {

        try {
            iSystemTemDetailsService.removeById(temdetailsId);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }


    }


    /**
     * 导入excel入库并绑定模板ID
     * @param multipartFile
     * @param templateId
     * @return
     */
    @RequestMapping(value = "/excelinport", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestBody MultipartFile multipartFile,@RequestParam String templateId) {

        try {
            if(multipartFile.isEmpty()){
                return AjaxResult.error("上传失败，请选择文件");
            }
            iSystemTemDetailsService.exportExceltoDb(multipartFile.getOriginalFilename(),multipartFile.getInputStream(),templateId);

            return AjaxResult.success();

        }catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }

    }


}
