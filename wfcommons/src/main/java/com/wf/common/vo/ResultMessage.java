package com.wf.common.vo;

import com.wf.common.constants.SystemCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultMessage<T> {

    private static volatile ResultMessage resultMessage = null;

    public ResultMessage(){
        code = SystemCode.SUCCESS;
        message = "成功";
        this.httpStatus = 200;
    }

    private ResultMessage(T data){
        code = SystemCode.SUCCESS;
        message = "成功";
        this.data = data;
        this.httpStatus = 200;
    }

    private ResultMessage(int status, String message){
        this.code = status;
        this.message = message;
        this.httpStatus = 200;
    }

    private ResultMessage(int status, String message, T data){
        this.code = status;
        this.message = message;
        this.data = data;
        this.httpStatus = 200;
    }

    private ResultMessage(int status, String message, T data, Integer httpStatus){
        this.code = status;
        this.message = message;
        this.data = data;
        this.httpStatus = httpStatus;
    }

    public static ResultMessage success(){
        return new ResultMessage();
    }

    public static ResultMessage success(String message){
        return new ResultMessage(SystemCode.SUCCESS, message);
    }

    public static ResultMessage success(Object data){
        return new ResultMessage(data);
    }

    public static ResultMessage success(String message, Object data){
        return new ResultMessage(SystemCode.SUCCESS, message, data);
    }

    public static ResultMessage fail(String message){
        return new ResultMessage(SystemCode.FAILED, message);
    }

    public static ResultMessage fail(String message, Integer httpStatus){
        return new ResultMessage(SystemCode.FAILED, message, null, httpStatus);
    }

    public static ResultMessage fail(Integer code, String message){
        return new ResultMessage(code, message);
    }

    public static ResultMessage fail(Integer code, String message, Integer httpStatus){
        return new ResultMessage(code, message, null, httpStatus);
    }

    public static ResultMessage complete(Integer code, String message, Object data){
        return new ResultMessage(code, message, data);
    }

    /**
     * 返回码 0失败，1成功
     */
    private Integer code;

    /**
     * 失败的异常信息，成功的信息
     */
    private String message;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * http请求状态码
     */
    private Integer httpStatus;


}
