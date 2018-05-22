package com.cfca.ra.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhangchong
 * @create 2018/5/21
 * @Description 前端测试页面
 * @CodeReviewer
 * @since v3.0.0
 */
@Controller
public class WebController {
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Value("${server.version}")
    private String version;

    @RequestMapping(value = {"", "/"})
    public String welcome() {
        logger.warn("welcome");
        return "welcome";
    }

}
