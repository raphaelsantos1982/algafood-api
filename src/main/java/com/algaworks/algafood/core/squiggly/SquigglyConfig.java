//package com.algaworks.algafood.core.squiggly;
//
//import java.util.Arrays;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.bohnman.squiggly.Squiggly;
//import com.github.bohnman.squiggly.web.RequestSquigglyContextProvider;
//import com.github.bohnman.squiggly.web.SquigglyRequestFilter;
//
//@Configuration
//public class SquigglyConfig {
//
//	@Bean // Sempre que uma requisição chegar na API, vai passar por esse filtro 13.3. Limitando os campos retornados pela API com Squiggly -> Filtro mais automatico e menos manual do que aula 13.2...
//	public FilterRegistrationBean<SquigglyRequestFilter> squigglyRequestFilter(ObjectMapper objectMapper) {
//		Squiggly.init(objectMapper, new RequestSquigglyContextProvider("campos", null));
//		
////		ATIVO EM TODOS OS ENDPOINTS DA API,LOGO, TODOS QUE QUISER FILTAR POR CAMPOS, VAI FUNCIONAR EM QQ CONSULTA
////		POREM A LINHA A SEGUIR FAZ OCM QUE SO PERMITA PARA PEDIDOS E RESTANTES. PRA TODOS, É SO DELETAR A LINHA
////		var urlPatterns = Arrays.asList("/pedidos/*", "/restaurantes/*");
//		
//		var filterRegistration = new FilterRegistrationBean<SquigglyRequestFilter>();
//		filterRegistration.setFilter(new SquigglyRequestFilter());
//		filterRegistration.setOrder(1);
////		filterRegistration.setUrlPatterns(urlPatterns);
//		
//		return filterRegistration;
//	}
//	
//}