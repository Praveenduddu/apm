package de.zeroco.apm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity
public class ApmSecurity {

	private static final String[] AUTH_WHITE_LIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/swagger-resources/**"
    };
	
	@Bean
	UserDetailsService users() {
		return new de.zeroco.apm.configuration.UserDetails();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(users());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers(AUTH_WHITE_LIST).permitAll().anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults()).authenticationProvider(authenticationProvider());
		return http.build();
	}

}
