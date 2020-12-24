package com.libbytian.pan.wechat.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.wechat.service.AsyncSearchCachedServiceImpl;
import com.libbytian.pan.wechat.service.NormalPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.wechat.controller
 * @ClassName: MoviePageShowController
 * @Author: sun71
 * @Description: 电影展示页
 * @Date: 2020/10/14 17:35
 * @Version: 1.0
 */

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/fantasy")
public class MoviePageShowController {

    final Base64.Decoder decoder = Base64.getDecoder();

    private final AsyncSearchCachedServiceImpl asyncSearchCachedService;
    private final ISystemTemDetailsService iSystemTemDetailsService;
    private final NormalPageService normalPageService;

    /**
     * @param fishEncryption
     * @return
     * @Description: 根据加密内容返回list页 头尾list
     */
    @RequestMapping(path = "/headtail/{fishEncryption}", method = RequestMethod.GET)
    public AjaxResult getHeadAndEndingPageShow(@PathVariable String fishEncryption) {
        try {
//           待封装  根据用户username 取出在使用的用户模板
            String username = new String(decoder.decode(fishEncryption), "UTF-8");
            SystemUserModel systemUserModel = new SystemUserModel();
            systemUserModel.setUsername(username);

            List<SystemTemDetailsModel> systemdetails = iSystemTemDetailsService.getTemDetailsWithUser(systemUserModel);
            Map map = new HashMap();

            for (SystemTemDetailsModel systemTemDetailsModel : systemdetails) {

                if (systemTemDetailsModel.getKeyword().equals("头部提示web")) {
                    systemTemDetailsModel.setKeyword("0");
                    map.put("head", systemTemDetailsModel);
                }
                if (systemTemDetailsModel.getKeyword().equals("底部提示web")) {
                    systemTemDetailsModel.setKeyword("1");
                    map.put("foot", systemTemDetailsModel);
                }
            }

            if (map.size() > 0) {
                return AjaxResult.success(map);
            } else {
                return AjaxResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }




    /**
     * @param fishEncryption
     * @param searchName
     * @return
     * @Description: 根据加密内容 返回待查询list
     * search 一号大厅  a
     * search 二号大厅  u
     * search 三号号大厅  x
     */
    @RequestMapping(path = "/movie/{search}/{fishEncryption}/{searchName}", method = RequestMethod.GET)
    public AjaxResult getMovieList(@PathVariable String search, @PathVariable String fishEncryption, @PathVariable String searchName) {
        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        try {

//            根据不同入参 给参数
            movieNameAndUrlModels = asyncSearchCachedService.searchWord(searchName.trim(), search);

            if (movieNameAndUrlModels.size() == 0) {
                return AjaxResult.error("未查询到 '" + searchName + "' 的相关资源");
            }

            return AjaxResult.success(movieNameAndUrlModels);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("未查询到 '" + searchName + "' 的相关资源");
        }

    }



}
