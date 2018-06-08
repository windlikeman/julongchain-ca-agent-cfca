package com.cfca.ra.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by 10046663 on 2017/1/16.
 */
@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {

	private final MyInterceptor interceptor;

	@Autowired
	public MyWebAppConfigurer(MyInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 多个拦截器组成一个拦截器链
		// addPathPatterns 用于添加拦截规则
		// excludePathPatterns 用户排除拦截
		registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/enroll");
//		registry.addInterceptor(new MyInterceptor2()).addPathPatterns("/**");
		super.addInterceptors(registry);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//貌似只能是html
//		registry.addViewController("/yourpath").setViewName("your.html");
//		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
//		super.configurePathMatch(configurer);
//		configurer.setUseSuffixPatternMatch(false);
	}

}
