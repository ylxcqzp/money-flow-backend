package com.ghost.moneyflowbackend.common.exception;

import com.ghost.moneyflowbackend.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param ex 业务异常
     * @return 统一响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: {}", ex.getMessage(), ex);
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理方法参数校验异常
     *
     * @param ex 参数校验异常
     * @return 统一响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数校验失败" : fieldError.getDefaultMessage();
        return Result.fail(ErrorCode.INVALID_PARAM, message);
    }

    /**
     * 处理表单绑定校验异常
     *
     * @param ex 表单绑定异常
     * @return 统一响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数校验失败" : fieldError.getDefaultMessage();
        return Result.fail(ErrorCode.INVALID_PARAM, message);
    }

    /**
     * 处理约束校验异常
     *
     * @param ex 约束校验异常
     * @return 统一响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException ex) {
        return Result.fail(ErrorCode.INVALID_PARAM, ex.getMessage());
    }

    /**
     * 处理请求体解析异常
     *
     * @param ex 请求体解析异常
     * @return 统一响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return Result.fail(ErrorCode.INVALID_PARAM, "请求体格式错误");
    }

    /**
     * 处理未知异常
     *
     * @param ex 未知异常
     * @return 统一响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error("系统异常", ex);
        return Result.fail(ErrorCode.INTERNAL_ERROR, "系统繁忙，请稍后重试");
    }
}
