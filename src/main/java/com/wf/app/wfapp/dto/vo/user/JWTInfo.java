package com.wf.app.wfapp.dto.vo.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JWTInfo {
    private String userId;
    private String account;
    private String loginTime;
}
