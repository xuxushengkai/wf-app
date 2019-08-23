package com.wf.app.wfapp.controller.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.app.wfapp.service.common.RedisService;
import com.wf.app.wfapp.util.JwtTokenUtil;
import com.wf.common.constants.PageConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;

public class BaseController extends com.wf.common.controller.BaseController {

    public static final String apiKey = "c378819e43a34b44bfea511e958b6781";
    public static final String apiSecret = "C382287E4BAA631B450F348BA7C0B297161691ECF6B94DEB381DE04434A5CB59";

    @Autowired
    protected RedisService redisService;
    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    protected Page buildPage(ServletRequest request) {
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        String[] orderField = request.getParameterValues("orderField");
        String order = request.getParameter("order");
        Page page = new Page(StringUtils.isNotEmpty(pageIndex) ? Long.parseLong(pageIndex) : PageConstant.DEFAULT_PAGE_Index, StringUtils.isNotEmpty(pageSize) ? Long.parseLong(pageSize) : PageConstant.DEFAULT_PAGE_SIZE);

        if (StringUtils.isNotEmpty(order) && orderField != null && orderField.length > 0 && (PageConstant.ORDER_ASC.equalsIgnoreCase(order) || PageConstant.ORDER_DESC.equalsIgnoreCase(order))) {
            if (PageConstant.ORDER_ASC.equalsIgnoreCase(order)) {
                page.setAsc(orderField);
            } else {
                page.setDesc(orderField);
            }
        }
        return page;
    }
}
