package org.bica.julongchain.cfca.ra;

import org.bica.julongchain.cfca.ra.env.Environments;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author zhangchong
 * @create 2018/7/11
 * @Description 启动类
 * @CodeReviewer zhangqingan
 * @since v3.0.0.2
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        Environments.environments();
    }
}
