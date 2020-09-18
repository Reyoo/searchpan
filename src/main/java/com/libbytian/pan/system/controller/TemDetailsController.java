package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TemDetailsController {

    private final ISystemTemDetailsService iSystemTemDetailsService;

    /**
     * 添加关键字及对应回复
     * @param keyword
     * @param keywordToValue
     * @return
     */
    @RequestMapping(value = "/details/addTemDetails",method = RequestMethod.POST)
    public AjaxResult addTemDetails(@RequestParam String keyword , @RequestParam String keywordToValue){

        try {
            int result = iSystemTemDetailsService.addTemDetails(keyword,keywordToValue);
            if(result == 1){
              return   AjaxResult.success();
            }else {
             return    AjaxResult.error("添加失败！");
            }
        } catch (Exception e) {
          return   AjaxResult.error(e.getMessage());
        }

    }


    public AjaxResult findTemDetails(@RequestParam(defaultValue = "0") int start ,@RequestParam(defaultValue = "10") int limit ){

        Page<SystemTemDetailsModel> page =  new Page<>(start,limit);

        iSystemTemDetailsService.findTemDetails(page);


        return AjaxResult.success();
    }

}
