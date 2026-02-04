package com.ghost.moneyflowbackend.common.model;

import lombok.Data;

/**
 * 统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /**
     * 业务状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 构建成功响应
     *
     * @param data 返回数据
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 构建失败响应
     *
     * @param code 错误码
     * @param message 错误描述
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
