package com.algaworks.algafood.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // revisar 22.3. Configurando Spring Security com HTTP Basic

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication() // define que a autenticação será feita em memória, ou seja, os usuários e senhas serão definidos diretamente no código
			.withUser("thiago")
				.password(passwordEncoder().encode("123")) // define o usuário "thiago" com a senha "123", que será codificada usando o codificador de senha definido no método passwordEncoder()
				.roles("ADMIN")
			.and()
			.withUser("joao")
				.password(passwordEncoder().encode("123"))
				.roles("ADMIN");
	}
	
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
	
	// define o bean de codificador de senha, que será usado para codificar as senhas dos usuários em memória. O BCryptPasswordEncoder é uma implementação do PasswordEncoder que utiliza o algoritmo BCrypt para codificar as senhas.
	@Bean
	public PasswordEncoder passwordEncoder() { // define o bean de codificador de senha.
		return new BCryptPasswordEncoder();
	}
}