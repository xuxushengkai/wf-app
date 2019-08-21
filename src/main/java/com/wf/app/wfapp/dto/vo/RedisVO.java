package com.wf.app.wfapp.dto.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisVO implements Serializable {

    private static final long serialVersionUID = 2575709372263966397L;
    private String key;

    private long expire;

}
