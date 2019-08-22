package com.wf.app.wfapp.dto.vo.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : zhanglei
 * @description
 * @date : 2019/3/7 17:49
 */
@Getter
@Setter
@AllArgsConstructor
public class JWTInfo {
    private String userId;
    private String loginTime;
}
