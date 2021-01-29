package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: QiSun
 * @date: 2021-01-29
 * @Description: 用户电影search
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_user")
public class SystemUserSearchMovieModel extends Model<SystemUserSearchMovieModel> {



    String searchName;

    Integer searchTimes;

    LocalDateTime lastSearchTime;




}