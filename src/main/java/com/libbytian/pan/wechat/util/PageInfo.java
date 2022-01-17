//package com.libbytian.pan.wechat.util;
//
//import com.github.pagehelper.Page;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.io.Serializable;
//import java.util.Collection;
//import java.util.List;
//
///**
// * 项目名: pan
// * 文件名: PageInfo
// * 创建者: HS
// * 创建时间:2022/1/14 11:01
// * 描述: TODO
// */
//@Data
//@AllArgsConstructor//全参构造
//@NoArgsConstructor//空参构造
//public class PageInfo<T> implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//    // 当前页
//    private int pageNum;
//    // 每页的数量
//    private int pageSize;
//    // 总记录数
//    private long count;
//    // 总页数
//    private int pages;
//    // 结果集
//    private T data;
//    // 状态码
//    private int msgcode = 0;
//    // 是否为第一页
//    private boolean isFirstPage = false;
//    // 是否为最后一页
//    private boolean isLastPage = false;
//    // 错误信息
//    private String msg;
//
//    public PageInfo(T data) {
//        this.data = data;
//        this.msgcode = 0;
//    }
//
//    public PageInfo(T data, int msgcode, String msg) {
//        this.data = data;
//        this.msgcode = msgcode;
//        this.msg = msg;
//    }
//
//    public PageInfo(int code, String msg) {
//        this.msgcode = code;
//        this.msg = msg;
//    }
//
//
//    public PageInfo<?> setPages(int pages) {
//        this.pages = pages;
//        return null;
//    }
//
//
//    @Override
//    public String toString() {
//        final StringBuffer sb = new StringBuffer("PageInfo{");
//        sb.append("pageNum=").append(pageNum);
//        sb.append(", pageSize=").append(pageSize);
//        sb.append(", count=").append(count);
//        sb.append(", pages=").append(pages);
//        sb.append(", data=").append(data);
//        sb.append(", isFirstPage=").append(isFirstPage);
//        sb.append(", isLastPage=").append(isLastPage);
//        sb.append(", navigatepageNums=");
//        sb.append('}');
//        return sb.toString();
//    }
//
//    /**
//     * 包装Page对象
//     *
//     * @param data
//     */
//    public PageInfo(List<T> data) {
//        if (data instanceof Page) {
//            Page page = (Page) data;
//            this.pageNum = page.getPageNum();
//            this.pageSize = page.getPageSize();
//
//            this.pages = page.getPages();
//            this.data = (T) page;
//            this.count = page.getTotal();
//        } else if (data instanceof Collection) {
//            this.pageNum = 1;
//            this.pageSize = data.size();
//
//            this.pages = 1;
//            this.data = (T) data;
//            this.count = data.size();
//        }
//        if (data instanceof Collection) {
//            // 判断页面边界
//            judgePageBoudary();
//        }
//    }
//
//    /**
//     * 判定页面边界
//     */
//    private void judgePageBoudary() {
//        isFirstPage = pageNum == 1;
//        isLastPage = pageNum == pages;
//    }
//
//}
