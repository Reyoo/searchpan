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

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TemDetailsController {

    private final ISystemTemDetailsService iSystemTemDetailsService;

    /**
     * 添加关键字及对应回复
     *
     * @param keyword
     * @param keywordToValue
     * @return
     */
//    @RequestMapping(value = "/details/addTemDetails",method = RequestMethod.POST)
//    public AjaxResult addTemDetails(@RequestParam String keyword , @RequestParam String keywordToValue){
//
//        try {
//            int result = iSystemTemDetailsService.addTemDetails(keyword,keywordToValue);
//            if(result == 1){
//              return   AjaxResult.success();
//            }else {
//             return    AjaxResult.error("添加失败！");
//            }
//        } catch (Exception e) {
//          return   AjaxResult.error(e.getMessage());
//        }
//
//    }
    @RequestMapping(value = "/details/addTemDetails", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestParam String keyword, @RequestParam String keywordToValue) {

        try {
            int result = iSystemTemDetailsService.addTemDetails(keyword, keywordToValue);
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
     * 查询默认模板（tt.template_id =1）下模板详情(后续可改成动态)
     *
     * @param start
     * @param limit
     * @return
     */
    @RequestMapping(value = "/details/findTemDetails", method = RequestMethod.GET)
    public AjaxResult findTemDetails(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "10") int limit) {

        Page<SystemTemDetailsModel> page = new Page<>(start, limit);
        try {
            IPage<SystemTemDetailsModel> result = iSystemTemDetailsService.findTemDetails(page);
            return AjaxResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }



    @RequestMapping(value = "/details/export", method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestBody MultipartFile multipartFile) {

        try {
            if(multipartFile.isEmpty()){
                return AjaxResult.error("上传失败，请选择文件");
            }
            iSystemTemDetailsService.exportExceltoDb(multipartFile.getOriginalFilename(),multipartFile.getInputStream());

            return AjaxResult.success();

        }catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }

    }




}
