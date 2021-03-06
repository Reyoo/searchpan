package com.libbytian.pan.system.common;


import cn.hutool.core.util.ObjectUtil;

import java.util.HashMap;
import java.util.Objects;

/**
 * 操作消息提醒
 *
 * @author ruoyi
 */
public class AjaxResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    private static final String STATUS_TAG = "status";

    private static final String MSG_TAG = "msg";

    private static final String DATA_TAG = "data";

    /**
     * 状态类型
     */
    public enum Type {
        /**
         * 成功
         */
        SUCCESS(200),
        /**
         * 警告
         */
        WARN(301),

        FORBBIDENT(300),


        /**
         * 错误
         */
        TOKENMISS(-99),
        /**
         * 错误
         */
        ERROR(500);



        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 状态类型
     */
    private transient Type type;

    /**
     * 状态码
     */
    private int status;

    /**
     * 返回内容
     */
    private String msg;

    /**
     * 数据对象
     */
    private transient Object data;


    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult() {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param type 状态类型
     * @param msg  返回内容
     */
    public AjaxResult(Type type, String msg) {
        super.put(STATUS_TAG, type.value);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param type 状态类型
     * @param msg  返回内容
     * @param data 数据对象
     */
    public AjaxResult(Type type, String msg, Object data) {
        super.put(STATUS_TAG, type.value);
        super.put(MSG_TAG, msg);
        if(ObjectUtil.isNotNull(data)){
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxResult success() {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg) {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功数据
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(Object data){
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(Type.SUCCESS, msg, data);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult warn(String msg) {
        return AjaxResult.warn(msg, null);
    }

    /**
     * 返回警告消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult warn(String msg, Object data) {
        return new AjaxResult(Type.WARN, msg, data);
    }

    public static AjaxResult hide() {
        return AjaxResult.hide("隐藏");
    }

    public static AjaxResult hide(String msg, Object data) {
        return new AjaxResult(Type.FORBBIDENT, msg, data);
    }

    public static AjaxResult hide(String msg) {
        return AjaxResult.hide(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @return 默认错误信息
     */
    public static AjaxResult error() {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String msg) {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(Type.ERROR, msg, data);
    }

    @Override
    public String toString() {
        return "AjaxResult{" +
                "type=" + type +
                ", status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        if (!super.equals(o)){
            return false;
        }
        AjaxResult that = (AjaxResult) o;
        return status == that.status &&
                type == that.type &&
                Objects.equals(msg, that.msg) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, status, msg, data);
    }
}

