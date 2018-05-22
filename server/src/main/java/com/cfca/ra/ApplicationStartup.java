package com.cfca.ra;

import com.cfca.ra.service.IRAService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 启动类
 * @CodeReviewer
 * @since v3.0.0
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final IRAService service = event.getApplicationContext().getBean(IRAService.class);
        try {
            service.initialize();
        } catch (RAServerException e) {
            throw new RuntimeException(e);
        }
    }
}
