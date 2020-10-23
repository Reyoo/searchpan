package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SensitiveWordModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.mapper
 * @ClassName: SensitiveWordMapper
 * @Author: sun71
 * @Description:
 * @Date: 2020/10/20 10:40
 * @Version: 1.0
 */

@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWordModel> {

    List<SensitiveWordModel> listSensitiveWordObjects(SensitiveWordModel sensitiveWordModel);

    int removeRepeat();

}
