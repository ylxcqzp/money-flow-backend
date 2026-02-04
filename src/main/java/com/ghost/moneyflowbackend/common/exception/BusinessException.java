package com.ghost.moneyflowbackend.common.exception;

/**
 * 业务异常定义
 */
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码
     */
    private final Integer code;

    /**
     * 创建业务异常
     *
     * @param code 错误码
     * @param message 错误描述
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
}
