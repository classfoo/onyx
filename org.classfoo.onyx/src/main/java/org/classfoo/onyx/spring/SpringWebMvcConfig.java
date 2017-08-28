package org.classfoo.onyx.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.classfoo")
public class SpringWebMvcConfig extends WebMvcConfigurerAdapter {

//	@Bean(name = "SpringAnnotation")
//	public ViewResolver viewResolver() {
//
//		InternalResourceViewResolver view = new InternalResourceViewResolver();
//		view.setViewClass(JstlView.class);
//		view.setPrefix("/WEB-INF/views/");
//		view.setSuffix(".jsp");
//		return view;
//	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multi = new CommonsMultipartResolver();
		multi.setMaxUploadSize(100000000);
		return multi;
	}

}