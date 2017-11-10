package org.classfoo.onyx.spring;

import java.util.Arrays;
import java.util.Collection;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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

	@Bean
	public Node elasticSearchTestNode() throws NodeValidationException {
		Settings settings = Settings.builder().put("transport.type", "netty4").put("http.type", "netty4").put(
				"http.enabled", "true").put("path.home", "data/elasticsearch").put("node.max_local_storage_nodes",
						"10").build();
		Node node = new MyNode(settings, Arrays.asList(Netty4Plugin.class));
		node.start();
		return node;
	}

	private static class MyNode extends Node {
		public MyNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
			super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, null), classpathPlugins);
		}
	}

}