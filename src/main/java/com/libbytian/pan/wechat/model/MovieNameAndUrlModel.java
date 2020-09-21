package com.libbytian.pan.wechat.model;

import lombok.Data;

/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.model
 * @ClassName: MovieNameAndUrlModel
 * @Author: sun71
 * @Description: 电影名以及url实体类
 * @Date: 2020/8/30 16:26
 * @Version: 1.0
 */
@Data
public class MovieNameAndUrlModel {

    String movieName;
    String movieUrl;
    String wangPanUrl;
    String wangPanPassword;
}
