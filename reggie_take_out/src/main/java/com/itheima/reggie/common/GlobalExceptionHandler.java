package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {       //全局异常处理

    /**
     *  抓取SQLIntegrityConstraintViolationException异常
     * @param e
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> SQLIntegrityConstraintViolationExceptionHandler(SQLIntegrityConstraintViolationException e){
        log.info(e.getMessage());       //异常日志
        if(e.getMessage().contains("Duplicate entry")){     //判断是否用户名重复导致异常
            String msg = e.getMessage().split(" ")[2] + "名称重复!";
            return R.error(msg);        //提示页面用户名重复
        }
        return R.error("未知错误!");        //其他错误
    }

    /**
     * 抓取CustomException异常
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> CustomExceptionHandler(CustomException e){
        log.info(e.getMessage());       //异常日志
        return R.error("操作不能完成! 该分类下存在数据!");        //响应页面异常消息
    }

    @ExceptionHandler(Exception.class)
    public R<String> UnknownException(Exception e){
        log.info(e.getMessage());       //异常日志
        return R.error("系统繁忙!");        //响应页面异常消息
    }
}
