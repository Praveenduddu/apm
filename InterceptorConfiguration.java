package de.zeroco.apm.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.zeroco.apm.interceptors.SaveInterceptor;

@Configuration
@EnableWebMvc
public class InterceptorConfiguration implements WebMvcConfigurer {

	@Autowired
	SaveInterceptor saveInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(saveInterceptor).addPathPatterns("/api/server/save", "/save", "/update");
	}
}
