package com.cfca.ra.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 10046663 on 2017/1/16.
 */
@Component
public class MyInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MyInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        final String authorization = request.getHeader("Authorization");
        logger.info("preHandle >>>>>> authorization: " + authorization);
//        ServletInputStream ris = request.getInputStream();
//        StringBuilder content = new StringBuilder();
//        byte[] b = new byte[1024];
//        int lens = -1;
//        while ((lens = ris.read(b)) > 0) {
//            content.append(new String(b, 0, lens));
//        }
//        String strcont = content.toString();// 内容


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
