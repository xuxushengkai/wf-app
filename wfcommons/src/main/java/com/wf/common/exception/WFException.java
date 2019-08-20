package com.wf.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WFException extends RuntimeException{

    private Integer code;

    public WFException() {
        super();
    }

    public WFException(String message) {
        super(message);
    }

    public WFException(String message, Throwable cause) {
        super(message, cause);
    }

    public WFException(String message, Integer code) {
        super(message);
        this.code = code;
    }

}
