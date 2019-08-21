package com.wf.app.wfapp.controller.basic;

import com.wf.app.wfapp.dto.vo.RedisVO;
import com.wf.app.wfapp.dto.vo.ResultMessage;
import com.wf.app.wfapp.service.common.RedisService;
import com.wf.common.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestMapping("/common/redis")
@RestController
public class RedisController extends BaseController {
    @Autowired
    protected RedisService redisService;

    @PostMapping("/list")
    public ResultMessage list(@RequestParam(required = false)String name) {
        List<RedisVO> redisVOS = new ArrayList<>();
        Set<String> keys = redisService.keys(StringUtils.isEmpty(name)?"*":name + "*");
        for (String key: keys){
            RedisVO redisVO = new RedisVO();
            redisVO.setKey(key);
            redisVO.setExpire(redisService.getExpire(key));
            redisVOS.add(redisVO);
        }
        return ResultMessage.success(redisVOS);
    }

    @PostMapping("/delete")
    public ResultMessage delete(@RequestParam String key) {
        redisService.keys("*"+key+"*").forEach(k->redisService.del(k));
        return ResultMessage.success();
    }

}
