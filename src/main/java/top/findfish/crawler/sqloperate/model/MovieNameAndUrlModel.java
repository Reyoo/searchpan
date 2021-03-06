package top.findfish.crawler.sqloperate.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;

/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.model
 * @ClassName: MovieNameAndUrlModel
 * @Author: sun71
 * @Description: 电影名以及url实体类
 * @Date: 2020/8/30 16:26
 * @Version: 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MovieNameAndUrlModel extends Model<MovieNameAndUrlModel> {

    /**
     * 主键id
     */
    @TableId(value = "id")
    Integer id;
    @TableField("movie_name")
    String movieName;
    @TableField("movie_url")
    String movieUrl;
    @TableField("wangpan_url")
    String wangPanUrl;
    @TableField("wangpan_passwd")
    String wangPanPassword;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
