package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SensitiveWordMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.service.ISensitiveWordService;
import com.libbytian.pan.system.util.sensitive.SensitiveWordEngine;
import com.libbytian.pan.system.util.sensitive.SensitiveWordInit;
import com.libbytian.pan.system.model.SensitiveWordModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.util.sensitive
 * @ClassName: SensitiveWordService
 * @Author: sun71
 * @Description:
 * @Date: 2020/10/20 10:20
 * @Version: 1.0
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class SensitiveWordService  extends ServiceImpl<SensitiveWordMapper, SensitiveWordModel> implements ISensitiveWordService {

    private final SensitiveWordMapper sensitiveWordMapper;

    /**
     * @Description: 是否包含敏感词
     * @author lc
     */
    @Override
    public Boolean isContaintSensitiveWord(String message){
        log.info("[通用短信请求]是否包含敏感词验证, 短信内容为: {}", message);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Boolean boo = false;
        try {
//            paramMap.put("valid", PublicEnum.YES.getCode());
//            paramMap.put("type", PublicEnum.N.getCode());
            // 从数据库中获取敏感词对象集合
            List<SensitiveWordModel> list = this.list();
            // 初始化敏感词库对象
            SensitiveWordInit sensitiveWordInit = new SensitiveWordInit();
            // 构建敏感词库
            final Map sensitiveWordMap = sensitiveWordInit.initKeyWord(list);
            // 传入SensitivewordEngine类中的敏感词库
            SensitiveWordEngine.sensitiveWordMap = sensitiveWordMap;
            boo = SensitiveWordEngine.isContaintSensitiveWord(message, 2);
        } catch (Exception e) {
            log.error("[通用短信请求]是否包含敏感词验证异常！{}", e.getMessage());
        }
        return boo;
    }

    /**
     * @Description: 获取敏感词内容
     * @author lc
     */
    @Override
    public Set<String> getSensitiveWord(SystemTemDetailsModel systemTemDetailsModel){
        log.info("[通用短信请求]获取敏感词内容, 短信内容为: {}", systemTemDetailsModel.getKeyword());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Set<String> sensitiveWordList = null;
        try {
//            paramMap.put("valid", PublicEnum.YES.getCode());
//            paramMap.put("type", PublicEnum.N.getCode());
            // 从数据库中获取敏感词对象集合
            List<SensitiveWordModel> list = this.list();
            // 初始化敏感词库对象
            SensitiveWordInit sensitiveWordInit = new SensitiveWordInit();
            // 构建敏感词库
            Map sensitiveWordMap = sensitiveWordInit.initKeyWord(list);
            // 传入SensitivewordEngine类中的敏感词库
            SensitiveWordEngine.sensitiveWordMap = sensitiveWordMap;
            sensitiveWordList = SensitiveWordEngine.getSensitiveWord(systemTemDetailsModel.getKeyword(), 2);

            log.info("[通用短信请求]获取敏感词内容, 敏感词为: {}", sensitiveWordList);

            // 留存拦截记录
//            SmsBlackInterceptRecord record = new SmsBlackInterceptRecord(baseMessage, sensitiveWordList.toString());
//            interceptRecordService.insert(record);
        } catch (Exception e) {
            log.error("[通用短信请求]获取敏感词内容异常！{}", e.getMessage());
        }
        return sensitiveWordList;
    }
}
