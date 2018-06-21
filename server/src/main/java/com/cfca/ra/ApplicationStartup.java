package com.cfca.ra;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 启动类
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
    }
}
