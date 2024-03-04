package com.atguigu.handler;

import com.atguigu.execption.GuiguException;
import com.atguigu.result.ResultCodeEnum;
import com.atguigu.result.RetVal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 全局异常处理类
 *
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RetVal error(Exception e){
        e.printStackTrace();
        return RetVal.fail();
    }

    /**
     * 自定义异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(GuiguException.class)
    @ResponseBody
    public RetVal error(GuiguException e){
        return RetVal.build(null,e.getCode(), e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public RetVal llegalArgumentException(Exception e) {
        log.error("触发异常拦截: " + e.getMessage(), e);
        return RetVal.build(null, ResultCodeEnum.ARGUMENT_VALID_ERROR);
    }

    /**
     * spring security异常
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public RetVal error(AccessDeniedException e) throws AccessDeniedException {
        return RetVal.build(null, ResultCodeEnum.PERMISSION);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RetVal error(MethodArgumentNotValidException e){
        //1.拿到发生异常的字段信息
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> errorList = bindingResult.getAllErrors();
        List<String> errorMsgList = errorList.stream().map(myError -> {
            log.error("字段:" + myError.getObjectName() + ",信息:" + myError.getDefaultMessage());
            return myError.getDefaultMessage();
        }).collect(Collectors.toList());
        //2.把这些信息返回给前端
        return RetVal.fail(errorMsgList);
    }
}
