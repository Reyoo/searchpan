package com.libbytian.pan.system.config;


import java.util.*;

import com.libbytian.pan.system.model.SensitiveWordModel;
import com.libbytian.pan.system.service.ISystemSensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.util.sensitive
 * @ClassName: SensitiveWordInit
 * @Author: sun71
 * @Description: 敏感词库初始化
 * @Date: 2020/10/20 10:14
 * @Version: 1.0
 */
@Slf4j
@Component
public class SensitiveWordInit {


    @Autowired
    ISystemSensitiveWordService iSystemSensitiveWordService;


    /**
     * 敏感词库
     */
    public static HashMap sensitiveWordMap = new HashMap();

    /**
     * 初始化敏感词
     *
     * @return
     */
    @PostConstruct
    public Map initKeyWord() {
        try {
            // 从敏感词集合对象中取出敏感词并封装到Set集合中
            Set<String> keyWordSet = new HashSet<String>();
            for (SensitiveWordModel s : iSystemSensitiveWordService.list()) {
                keyWordSet.add(s.getWord().trim());
            }
            // 将敏感词库加入到HashMap中
            addSensitiveWordToHashMap(keyWordSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    /**
     * 封装敏感词库
     *
     * @param keyWordSet
     */
    @SuppressWarnings("rawtypes")
    private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        // 初始化HashMap对象并控制容器的大小
        sensitiveWordMap = new HashMap(keyWordSet.size());
        // 敏感词
        String key = null;
        // 用来按照相应的格式保存敏感词库数据
        Map nowMap = null;
        // 用来辅助构建敏感词库
        Map<String, String> newWorMap = null;
        // 使用一个迭代器来循环敏感词集合
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext()) {
            key = iterator.next();
            // 等于敏感词库，HashMap对象在内存中占用的是同一个地址，所以此nowMap对象的变化，sensitiveWordMap对象也会跟着改变
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                // 截取敏感词当中的字，在敏感词库中字为HashMap对象的Key键值
                char keyChar = key.charAt(i);

                // 判断这个字是否存在于敏感词库中
                Object wordMap = nowMap.get(keyChar);
                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap<String, String>();
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }
                // 如果该字是当前敏感词的最后一个字，则标识为结尾字
                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }
}