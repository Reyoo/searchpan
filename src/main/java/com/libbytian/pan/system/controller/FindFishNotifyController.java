package com.libbytian.pan.system.controller;


import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemNotifyModel;
import com.libbytian.pan.system.service.ISystemNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/notify")
@Slf4j
public class FindFishNotifyController {

    private final ISystemNotifyService systemNotifyService;

    /**
     * 分页查询
     * @param systemNotifyModel
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public AjaxResult findNotifyByPage(@RequestBody(required = false) SystemNotifyModel systemNotifyModel) {
        // 下个版本 迭代出来
//        PageHelper.startPage(systemNotifyModel.page().intValue(), systemNotifyModel.limits().intValue());
        Long page = systemNotifyModel.page()== null ? 1L:systemNotifyModel.page();
        Long limits = systemNotifyModel.limits() == null?10L :systemNotifyModel.limits();
        Page<SystemNotifyModel> findpage = new Page<>(page, limits);
        try {
            IPage<SystemNotifyModel> result = systemNotifyService.findConditionByPage(findpage, systemNotifyModel);
//            PageInfo pageInfo = new PageInfo<>(list);
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 新增角色
     * @param systemNotifyModel
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public AjaxResult addNotify(@RequestBody SystemNotifyModel systemNotifyModel) {
        try {
            systemNotifyModel.id(UUID.fastUUID().toString());
            systemNotifyModel.modifyDate(LocalDateTime.now());
            systemNotifyService.addSystemNotify(systemNotifyModel);
            return AjaxResult.success("add is ok!!!");
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }



    /**
     * 删除
     * @param systemNotifyModel
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public AjaxResult deleteNotify(@RequestBody SystemNotifyModel systemNotifyModel) {

        try {
            systemNotifyService.removeSystemNoitfy(systemNotifyModel);
            return AjaxResult.success("delete is ok !");
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error("delete error");
        }
    }


    /**
     * 修改
     * @param systemNotifyModel
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PATCH)
    public AjaxResult updateNotify(@RequestBody SystemNotifyModel systemNotifyModel) {
        try {
            systemNotifyModel.modifyDate(LocalDateTime.now());
            systemNotifyService.updateSystemNoitfy(systemNotifyModel);
            return AjaxResult.success("update is OK !");
        } catch (Exception e) {
            log.error("更新失败 --》 " + e.getMessage());
            return AjaxResult.error("更新失败");
        }
    }

}
