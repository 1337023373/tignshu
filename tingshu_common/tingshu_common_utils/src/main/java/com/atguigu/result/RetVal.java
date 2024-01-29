package com.atguigu.result;


import lombok.Data;

/**
 * 全局统一返回结果类
 *
 */
@Data
public class RetVal<T> {

    //返回码
    private Integer code;

    //返回消息
    private String message;

    //返回数据
    private T data;

    public RetVal(){}

    // 返回数据
    protected static <T> RetVal<T> build(T data) {
        RetVal<T> retVal = new RetVal<T>();
        if (data != null)
            retVal.setData(data);
        return retVal;
    }

    public static <T> RetVal<T> build(T body, Integer code, String message) {
        RetVal<T> retVal = build(body);
        retVal.setCode(code);
        retVal.setMessage(message);
        return retVal;
    }

    public static <T> RetVal<T> build(T body, ResultCodeEnum resultCodeEnum) {
        RetVal<T> retVal = build(body);
        retVal.setCode(resultCodeEnum.getCode());
        retVal.setMessage(resultCodeEnum.getMessage());
        return retVal;
    }

    public static<T> RetVal<T> ok(){
        return RetVal.ok(null);
    }

    /**
     * 操作成功
     * @param data  baseCategory1List
     * @param <T>
     * @return
     */
    public static<T> RetVal<T> ok(T data){
        RetVal<T> retVal = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> RetVal<T> fail(){
        return RetVal.fail(null);
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static<T> RetVal<T> fail(T data){
        RetVal<T> retVal = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    public RetVal<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public RetVal<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
