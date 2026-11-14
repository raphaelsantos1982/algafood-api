package com.algaworks.algafood.core.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // revisar 22.3. Configurando Spring Security com HTTP Basic

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic()
			
			.and()
			.authorizeRequests()
				.antMatchers("/v1/cozinhas/**").permitAll() // autoriza todas as requisições para /v1/cozinhas/** sem necessidade de autenticação
				.anyRequest().authenticated() // autoriza todas as demais requisições apenas se o usuário estiver autenticado
			
			.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // define que a aplicação não irá criar sessão de usuário, ou seja, não irá armazenar informações do usuário entre requisições
				
			.and()
				.csrf().disable(); // desabilita a proteção contra ataques CSRF, que não é necessária em APIs RESTful
	}
	
}