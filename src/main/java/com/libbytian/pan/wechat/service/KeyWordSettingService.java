package com.libbytian.pan.wechat.service;

import com.libbytian.pan.system.common.TemplateDetailsGetKeywordComponent;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.wechat.constant.TemplateKeywordConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final TemplateDetailsGetKeywordComponent templateDetailsGetKeywordComponent;

    public StringBuffer getTemplateKeyWord(SystemUserModel systemUserModel, String searchName, StringBuffer stringBuffer,SystemKeywordModel systemKeywordModel) throws Exception  {

        SystemTemDetailsModel secretContent = templateDetailsGetKeywordComponent.getUserKeywordDetail(systemUserModel, TemplateKeywordConstant.SECRET_CONTENT);
        SystemTemDetailsModel secretReply = templateDetailsGetKeywordComponent.getUserKeywordDetail(systemUserModel, TemplateKeywordConstant.SECRET_REPLY);

        SystemTemDetailsModel keyContent = templateDetailsGetKeywordComponent.getUserKeywordDetail(systemUserModel, TemplateKeywordConstant.KEY_CONTENT);
        SystemTemDetailsModel preserveContent = templateDetailsGetKeywordComponent.getUserKeywordDetail(systemUserModel, TemplateKeywordConstant.PRESERVE_CONTENT);


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
                    if (secretReply.getEnableFlag()) {
                        stringBuffer.append(secretReply.getKeywordToValue());
                    } else {
                        Thread.sleep(5000);
                    }
                    break;
                }
            }
        }

//        SystemKeywordModel systemKeywordModel = systemKeywordService.getKeywordByUser(systemUserModel.getUsername());


        /**
         * 关键词 维护判断
         * 00：00-00：00全天开放
         * 其他相同起始时间为全天维护
         */

        //维护时间
        String userStart = systemKeywordModel.getStartTime();
        String userEnd = systemKeywordModel.getEndTime();

        String fansKey = systemKeywordModel.getFansKey();

        if (!"00:00".equals(userStart) || !"00:00".equals(userEnd)) {

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



            //如果当前时间在维护期内，返回维护内容,开始=结束 全天维护
            if (nowTime.after(beginTime) && nowTime.before(endTime) || userStart.equals(endTime)) {


                //维护期内判断秘钥功能
                if (!searchName.contains(fansKey) && ! fansKey.equals("000000") ) {

                    if (keyContent.getEnableFlag()) {
                        stringBuffer.setLength(0);
                        stringBuffer.append(keyContent.getKeywordToValue());
                    }else {
                        if (preserveContent.getEnableFlag()){
                            stringBuffer.setLength(0);
                            stringBuffer.append(preserveContent.getKeywordToValue());
                        }else {
                            Thread.sleep(5000);
                        }
                    }
                }
                //判断段维护期内 没开秘钥情况
                if (fansKey.equals("000000")){

                    if (preserveContent.getEnableFlag()){
                        stringBuffer.setLength(0);
                        stringBuffer.append(preserveContent.getKeywordToValue());
                    }else {
                        Thread.sleep(5000);
                    }
                }

            }
        }
        return stringBuffer;
    }

}
