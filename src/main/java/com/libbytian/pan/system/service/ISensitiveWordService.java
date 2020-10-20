package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SensitiveWordModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.service
 * @ClassName: ISensitiveWordService
 * @Author: sun71
 * @Description: 敏感词
 * @Date: 2020/10/20 10:44
 * @Version: 1.0
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISensitiveWordService extends IService<SensitiveWordModel> {

     /**
      *  是否包含敏感词
      * @param message
      * @return
      */
     Boolean isContaintSensitiveWord(String message);

     /**
      * 获取敏感词内容
      * @param systemTemDetailsModel
      * @return
      */
     Set<String> getSensitiveWord(SystemTemDetailsModel systemTemDetailsModel);

}
