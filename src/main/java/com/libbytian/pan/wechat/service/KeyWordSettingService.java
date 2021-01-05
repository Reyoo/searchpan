package com.libbytian.pan.wechat.service;

import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.wechat.constant.TemplateKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 项目名: pan
 * 文件名: KeyWordSettingService
 * 创建者: HS
 * 创建时间:2021/1/4 16:16
 * 描述: TODO
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class KeyWordSettingService {

    private final ISystemTemDetailsService iSystemTemDetailsService;
    private final ISystemKeywordService systemKeywordService;


    public StringBuffer Setting(String username ,String searchName ,StringBuffer stringBuffer ,String searchContent) throws ParseException {

        SystemTemDetailsModel secretContent = iSystemTemDetailsService.getUserKeywordDetail(username, TemplateKeyword.SECRET_CONTENT);
        SystemTemDetailsModel secretReply = iSystemTemDetailsService.getUserKeywordDetail(username, TemplateKeyword.SECRET_REPLY);

        SystemTemDetailsModel keyContent = iSystemTemDetailsService.getUserKeywordDetail(username, TemplateKeyword.KEY_CONTENT);
        SystemTemDetailsModel preserveContent = iSystemTemDetailsService.getUserKeywordDetail(username, TemplateKeyword.PRESERVE_CONTENT);

        /**
         * 关键词 隐藏判断
         * 空格作为区分
         */
        if (secretContent.getEnableFlag()) {
            //隐藏的片名以空格分隔，获取隐藏片名的数组
            String[] str = secretContent.getKeywordToValue().split(" ");

            for (String s : str) {
                //判断传入的 片名 是否在隐藏资源中
                if (s.equals(searchName)) {
                    stringBuffer.setLength(0);
                    if (secretReply.getEnableFlag()){
                        stringBuffer.append(secretReply.getKeywordToValue());
                    }
                    break;
                }
            }
        }

        SystemKeywordModel systemKeywordModel = systemKeywordService.getKeywordByUser(username);

        /**
         * 关键词 维护判断
         * 00：00-00：00全天维护
         * 其他相同起始时间为全天放开
         */

        //维护时间
        String userStart = systemKeywordModel.getStartTime();
        String userEnd = systemKeywordModel.getEndTime();

        if (!"00:00".equals(userStart) && !"00:00".equals(userEnd)) {

            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date dateStart = df.parse(userStart);
            Date dateEnd = df.parse(userEnd);

            //当前时间
            Date now = df.parse(df.format(new Date()));

            Calendar nowTime = Calendar.getInstance();
            nowTime.setTime(now);

            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(dateStart);

            Calendar endTime = Calendar.getInstance();
            endTime.setTime(dateEnd);

            //如果开始时间 > 结束时间，跨天 给结束时间加一天
            if (beginTime.after(endTime)) {
                endTime.add(Calendar.DATE, 1);
            }

            //如果当前时间在维护期内，返回维护内容
            if (nowTime.after(beginTime) && nowTime.before(endTime) || "00:00".equals(userStart) && "00:00".equals(userEnd)) {
                stringBuffer.setLength(0);
                if (preserveContent.getEnableFlag()){
                    stringBuffer.append(preserveContent.getKeywordToValue());
                }

            }
        }


        /**
         * HuangS
         * 关键词 判断秘钥
         * 最简单实现
         * 判断传入的关键词中是否包含秘钥
         */
        String fansKey = systemKeywordModel.getFansKey();

        if (StringUtils.isNotBlank(fansKey) && !searchContent.contains(fansKey)) {

            stringBuffer.setLength(0);
            if (StringUtils.isNotBlank(fansKey)) {
                stringBuffer.append(keyContent.getKeywordToValue());
            }
        }

        return stringBuffer;
    }

}
