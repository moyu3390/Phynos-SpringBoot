package com.phynos.solar.config;

import com.phynos.solar.util.json.R;
import com.phynos.solar.util.json.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * @author by lupc
 * @date 2021-01-29 11:47
 */
@RestControllerAdvice
public class GlobalException {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = RuntimeException.class)
    public R<?> runtimeException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        return R.tip(ResultCodeEnum.SYSTEM_UNKNOWN_ERROR, ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public R<?> handlerBindException(BindException ex) {
        //校验 除了 requestbody 注解方式的参数校验 对应的 bindingresult 为 BeanPropertyBindingResult
        logger.error(ex.getMessage(), ex);
        List<FieldError> fieldErrors = ex.getFieldErrors();
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return R.tip(ResultCodeEnum.PARAMETER_ERROR, ex.getMessage());
        } else {
            FieldError fieldError = fieldErrors.get(0);
            String msg = fieldError.getDefaultMessage();
            return R.msg(ResultCodeEnum.PARAMETER_ERROR, msg);
        }
    }

    @ResponseBody
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public R<?> handlerBindException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        logger.error(ex.getMessage(), ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String msg = fieldError.getDefaultMessage();
        return R.msg(ResultCodeEnum.PARAMETER_ERROR, msg);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<?> postJsonReadError(HttpMessageNotReadableException ex) {
        logger.error(ex.getMessage(), ex);
        return R.tip(ResultCodeEnum.JSON_READ_ERROR, ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<?> dataRepeatError(HttpMessageNotReadableException ex) {
        logger.error(ex.getMessage(), ex);
        return R.tip(ResultCodeEnum.DATA_REPEAT_ERROR, ex.getMessage());
    }

    //参数类型不匹配
    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    public R<?> requestTypeMismatch(TypeMismatchException ex) {
        logger.error(ex.getMessage(), ex);
        return R.tip(ResultCodeEnum.PARAMETER_ERROR, "参数类型不匹配,参数" + ex.getPropertyName() + "类型应该为" + ex.getRequiredType());
    }

    //缺少参数异常
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public R<?> requestMissingServletRequest(MissingServletRequestParameterException ex) {
        logger.error(ex.getMessage(), ex);
        return R.tip(ResultCodeEnum.PARAMETER_ERROR, "缺少必要参数,参数名称为" + ex.getParameterName());
    }

}
