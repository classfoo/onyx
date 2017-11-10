package org.classfoo.onyx.spring;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.cassandra.service.EmbeddedCassandraService;
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
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.classfoo.onyx.impl")
public class SpringWebMvcConfig {

	@Bean(name = "SpringAnnotation")
	public ViewResolver viewResolver() {
		InternalResourceViewResolver view = new InternalResourceViewResolver();
		view.setViewClass(JstlView.class);
		view.setPrefix("/WEB-INF/views/");
		view.setSuffix(".jsp");
		return view;
	}

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

	@Bean
	public EmbeddedCassandraService getCassandraServer() throws IOException {
		URL res = SpringWebMvcConfig.class.getResource("cassandra.yaml");
		System.setProperty("cassandra.config", res.toString());
		System.setProperty("cassandra.storagedir", "data/cassandra");
		System.setProperty("cassandra.ignore_corrupted_schema_tables", "true");
		EmbeddedCassandraService service = new EmbeddedCassandraService();
		service.start();
		return service;
	}
}