package com.libbytian.pan.wechat.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.wechat.model.MovieNameAndUrlModel;
import com.libbytian.pan.wechat.service.AsyncSearchCachedServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            //获取用户模板
//            List<SystemTemplateModel> systemTemplateModels = systemTemplateService.getTemplateModelByUser(systemUserModel,true);
//            //通过模板ID，查询对应的模板详情，取出关键词，头部广告，底部广告
//            List<SystemTemDetailsModel> systemdetails = systemTemplateService.findTemDetails(systemTemplateModels.get(0).getTemplateid());

            List<SystemTemDetailsModel> systemdetails = iSystemTemDetailsService.getTemDetailsWithUser(systemUserModel);
            Map map = new HashMap();

            for(SystemTemDetailsModel systemTemDetailsModel : systemdetails){

                if(systemTemDetailsModel.getKeyword().equals("头部提示web")){
                    systemTemDetailsModel.setKeyword("0");
                    map.put("head",systemTemDetailsModel);
                }
                if(systemTemDetailsModel.getKeyword().equals("底部提示web")){
                    systemTemDetailsModel.setKeyword("1");
                    map.put("foot",systemTemDetailsModel);
                }
            }

//            List<SystemTemDetailsModel> headAndTialDetail = systemdetails.stream().
//                    filter(systemTemdetailsModel -> "头部提示web".equals(systemTemdetailsModel.getKeyword())
//                            || "底部提示web".equals(systemTemdetailsModel.getKeyword())).collect(Collectors.toList());





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
     * @return
     * @Description: 根据加密内容 返回会员信息 头尾list
     */
    @RequestMapping(path = "/member/{fishEncryption}/{searchName}", method = RequestMethod.GET)
    public AjaxResult getMemberList(@PathVariable String fishEncryption, @PathVariable String searchName) {
        try {
            String username = new String(decoder.decode(fishEncryption), "UTF-8");
            SystemUserModel systemUserModel = new SystemUserModel();
            systemUserModel.setUsername(username);

            List<SystemTemDetailsModel> systemdetails = iSystemTemDetailsService.getTemDetailsWithUser(systemUserModel);

            /**
             *  searchName 后期要改用模糊查询
             */
            List<SystemTemDetailsModel> memberList = systemdetails.stream().
                    filter(systemTemdetailsModel -> searchName.equals(systemTemdetailsModel.getKeyword())).collect(Collectors.toList());


            if (memberList.size() > 0) {
                return AjaxResult.success(memberList);
            } else {
                return AjaxResult.success();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error();
        }
    }


    /**
     * @param fishEncryption
     * @param searchName
     * @return
     * @Description: 根据加密内容 返回待查询list
     */
    @RequestMapping(path = "/movie/{fishEncryption}/{searchName}", method = RequestMethod.GET)
    public AjaxResult getMovieList(@PathVariable String fishEncryption, @PathVariable String searchName) {
        try {
            String username = new String(decoder.decode(fishEncryption), "UTF-8");
            // 调用验证用户名是否合法
            List<MovieNameAndUrlModel> movieNameAndUrlModels = asyncSearchCachedService.searchWord(searchName.trim());

            if (movieNameAndUrlModels!=null && movieNameAndUrlModels.size() > 0) {
                return AjaxResult.success(movieNameAndUrlModels);
            } else {
                return AjaxResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error();
        }
    }

}
