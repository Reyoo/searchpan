package com.libbytian.pan.wechat.controller;

import com.libbytian.pan.system.common.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Base64;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.wechat.controller
 * @ClassName: MoviePageShowController
 * @Author: sun71
 * @Description: 电影展示页
 * @Date: 2020/10/14 17:35
 * @Version: 1.0
 */

@Controller
@RequestMapping("/fantasy")
public class MoviePageShowController {

    final Base64.Decoder decoder = Base64.getDecoder();
    /**
     * @Description: 根据加密内容返回list页 头尾list
     * @param fishEncryption
     * @return
     */
    @RequestMapping( path = "/headtail/{fishEncryption}", method = RequestMethod.GET)
    public AjaxResult getHeadAndEndingPageShow(@PathVariable  String fishEncryption) {
        try {
            String username = new String(decoder.decode(fishEncryption), "UTF-8");

            // 如果没有的则返回空并提示
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error();
        }
    }



    /**
     * @Description: 根据加密内容 返回会员信息 头尾list
     * @param fishEncryption
     * @return
     */
    @RequestMapping( path = "/member/{fishEncryption}", method = RequestMethod.GET)
    public AjaxResult getMemberList(@PathVariable  String fishEncryption){
        try {
            String username = new String(decoder.decode(fishEncryption), "UTF-8");
            // 如果没有的则返回空并提示
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error();
        }
    }


    /**
     * @Description: 根据加密内容 返回待查询list
     * @param fishEncryption
     * @param searchName
     * @return
     */
    @RequestMapping( path = "/movie/{fishEncryption}/{searchName}", method = RequestMethod.GET)
    public AjaxResult getMovieList(@PathVariable  String fishEncryption,@PathVariable String searchName){
        try {
            String username = new String(decoder.decode(fishEncryption), "UTF-8");
            // 如果没有的则返回空并提示
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error();
        }
    }

}
