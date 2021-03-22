package com.libbytian.pan.wechat.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import com.libbytian.pan.wechat.service.AsyncSearchCachedComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.LocalDateTime;
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
@EnableAsync
@SentinelResource("fantasy")
public class MoviePageShowController {

    final Base64.Decoder decoder = Base64.getDecoder();

    private final AsyncSearchCachedComponent asyncSearchCachedService;
    private final ISystemTemDetailsService iSystemTemDetailsService;
    private final ISystemUserService iSystemUserService;
    private final ISystemUserSearchMovieService iSystemUserSearchMovieService;


    private final ISystemTemplateService iSystemTemplateService;
    private final SystemTemDetailsMapper systemTemDetailsMapper;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;




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


            //返回Map初始化
            Map keynullMap = new HashMap();
            keynullMap.put("keywordToValue", "");

                map.put("head", keynullMap);
                map.put("foot", keynullMap);
                map.put("searchBox", keynullMap);

            //遍历
            for (SystemTemDetailsModel systemTemDetailsModel : systemdetails) {
                if (systemTemDetailsModel.getKeyword().equals("头部提示web") && systemTemDetailsModel.getEnableFlag()) {
                    systemTemDetailsModel.setKeyword("0");
                    map.put("head", systemTemDetailsModel);
                    continue;
                }

                if (systemTemDetailsModel.getKeyword().equals("底部提示web") && systemTemDetailsModel.getEnableFlag()) {
                    systemTemDetailsModel.setKeyword("1");
                    map.put("foot", systemTemDetailsModel);
                    continue;
                }

                if (systemTemDetailsModel.getKeyword().equals("web页搜索框") && systemTemDetailsModel.getEnableFlag()) {
                    systemTemDetailsModel.setKeyword("2");
                    map.put("searchBox", systemTemDetailsModel);
                    continue;
                }
            }

            return AjaxResult.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * @param fishEncryption
     * @return
     * @Description: 根据加密内容 返回会员信息 头尾list  X
     * @Description: 根据加密内容 返回会员大厅
     */
    @RequestMapping(path = "/member/{fishEncryption}/{searchName}", method = RequestMethod.GET)
    public AjaxResult getMemberList(@PathVariable String fishEncryption, @PathVariable String searchName) {
        try {
            long startTime=System.currentTimeMillis();

            String username = new String(decoder.decode(fishEncryption), "UTF-8");
            SystemUserModel systemUserModel = new SystemUserModel();
            systemUserModel.setUsername(username);

            List<SystemTemDetailsModel> systemdetails = iSystemTemDetailsService.getTemDetailsWithUser(systemUserModel);

            searchName.replace("+","");

            List<SystemTemDetailsModel> memberList = systemdetails.stream().
                    filter(systemTemdetailsModel -> systemTemdetailsModel.getKeyword().contains(searchName) && systemTemdetailsModel.getShowOrder()==0).collect(Collectors.toList());


            long endTime=System.currentTimeMillis(); //获取结束时间
            System.out.println("=====接口调用时间："+(endTime-startTime)+"ms==============");

            //获取接口最新调用时间
            systemUserModel.setCallTime(LocalDateTime.now());
            iSystemUserService.updateUser(systemUserModel);


            if (memberList.size() > 0) {
                return AjaxResult.success(memberList);
            } else {
                return AjaxResult.hide();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.hide();
        }
    }


    /**
     * @param fishEncryption
     * @param searchName
     * @return
     * @Description: 根据加密内容 返回待查询list
     * search 一号大厅  a
     * search 二号大厅  u
     * search 三号大厅  x
     */
    @RequestMapping(path = "/movie/{search}/{fishEncryption}/{searchName}", method = RequestMethod.GET)
    public AjaxResult getMovieList(@PathVariable String search, @PathVariable String fishEncryption, @PathVariable String searchName) throws UnsupportedEncodingException {

//        //判断接口是否过期
        String username = new String(decoder.decode(fishEncryption), "UTF-8");
        SystemUserModel systemUserModel = new SystemUserModel();
        systemUserModel.setUsername(username);
        SystemUserModel user = iSystemUserService.getUser(systemUserModel);
        if (LocalDateTime.now().isAfter(user.getActTime())){
            System.out.println("===============用户："+username+"接口已过期===============");
            return AjaxResult.error("该系统提供服务已过期,继续使用请 关注公众号：影子的胡言乱语 ");
        }

        System.out.println("===============用户："+username+"正在调用web页大厅===============");


        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();


        searchName = URLDecoder.decode(searchName,"UTF-8");

        System.out.println(searchName);


        /**
         * 统计用户查询记录
         */
        iSystemUserSearchMovieService.userSearchMovieCountInFindfish(searchName);


        try {
//            根据不同入参 给参数
            movieNameAndUrlModels = asyncSearchCachedService.searchWord(searchName.trim(), search);

            if (movieNameAndUrlModels.size() == 0) {
                return AjaxResult.hide("未找到该资源，请前往其他大厅查看");
            }
            return AjaxResult.success(movieNameAndUrlModels);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.hide("全网搜 '" + searchName + "' 中 挖坑埋点土数个一二三四五，再点一次大厅");
        }
    }


    /**
     * 当新增加系统关键词时，插入到每一个用户的模板详情中
      * @param details
     * @return
     */
    @RequestMapping(value = "/addKeyword", method = RequestMethod.POST)
    public AjaxResult addKeyword(@RequestBody SystemTemDetailsModel details){

        //获取所有模板
        List<SystemTemplateModel> allTemplate = iSystemTemplateService.getAllTemplate();
        //遍历所有模板，将新增关键词存入 详情表，绑定中间表。
        for (SystemTemplateModel templateModel : allTemplate) {
            details.setTemdetailsId(UUID.randomUUID().toString());
            details.setCreatetime(LocalDateTime.now());

            //插入模板详情表
            int result = systemTemDetailsMapper.insert(details);
            if (result == 1) {
                //插入模板_模板详情表
                SystemTemToTemdetail temToDetails = SystemTemToTemdetail.builder().templateid(templateModel.getTemplateid()).templatedetailsid(details.getTemdetailsId()).build();
                iSystemTmplToTmplDetailsService.save(temToDetails);
            }
        }
        return AjaxResult.success("新增成功");

    }




}
