package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.IKeywordService;
import com.libbytian.pan.system.service.ISystemUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目名: pan
 * 文件名: KeywordController
 * 创建者: HS
 * 创建时间:2020/12/14 15:31
 * 描述: TODO
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/keyword")
public class KeywordController {


    private final IKeywordService iKeywordService;


    /**
     * 查询用户关键词
     * @param userModel
     * @return
     */
    @RequestMapping("/selectUserKeyword")
    public AjaxResult getUserKeyword(SystemUserModel userModel){

        String userId = userModel.getUserId();
        SystemKeywordModel keywordModel = iKeywordService.getKeywordByUser(userId);

        return AjaxResult.success(keywordModel);


    }

    /**
     * 更新关键字
     * @param systemKeywordModel
     * @return
     */
    @RequestMapping(value = "updateKeyword" ,method = RequestMethod.PATCH)
    public AjaxResult updateKeyword(SystemKeywordModel systemKeywordModel){

        iKeywordService.updateKeyword(systemKeywordModel);

        return AjaxResult.success("成功更新关键字");

    }
}
