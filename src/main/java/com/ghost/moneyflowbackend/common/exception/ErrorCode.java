package com.ghost.moneyflowbackend.common.exception;

/**
 * 业务错误码定义
 */
public final class ErrorCode {

    /**
     * 参数校验错误
     */
    public static final int INVALID_PARAM = 400;

    /**
     * 未认证或认证失败
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 无权限访问
     */
    public static final int FORBIDDEN = 403;

    /**
     * 业务冲突
     */
    public static final int CONFLICT = 409;

    /**
     * 系统内部错误
     */
    public static final int INTERNAL_ERROR = 500;

    private ErrorCode() {
    }
}
