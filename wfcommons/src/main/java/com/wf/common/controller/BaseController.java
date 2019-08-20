package com.wf.common.controller;

import com.wf.common.constants.SystemCode;
import com.wf.common.exception.WFException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public abstract class BaseController {

    /**
     * 接口入参参数判空验证
     *
     * @param bindingResult
     */
    protected void paramsValid(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            if (!errors.isEmpty()) {
                ObjectError error = errors.get(0);
                throw new WFException(error.getDefaultMessage(), SystemCode.PARAMS_ERROR);
            }
        }
    }

}
