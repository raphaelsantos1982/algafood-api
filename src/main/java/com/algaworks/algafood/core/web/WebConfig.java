package com.algaworks.algafood.core.web;

import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebConfig
	implements WebMvcConfigurer { // Define metodos de callback para configurar o Spring MVC

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void addCorsMappings(CorsRegistry registry) { // Configura o mapeamento de CORS (Cross-Origin Resource Sharing) para permitir que recursos da API sejam acessados por diferentes origens (domínios). Isso é útil quando a aplicação front-end está hospedada em um domínio diferente do back-end.
		registry.addMapping("/**")// permite que qualquer origem acesse a API
			.allowedMethods("*"); // permite que qualquer metodo HTTP seja aceito
//			.allowedOrigins("*")
//			.maxAge(30);
	}

	// O @EnableWebMvc (usado para o Springfox gerar a documentação) desliga a autoconfiguração
	// padrão do Spring Boot para o Jackson, fazendo o Spring MVC montar seus próprios
	// HttpMessageConverters com um ObjectMapper novo, isolado do bean gerenciado pelo Spring
	// (o mesmo que o SquigglyConfig configura). Por isso o filtro de campos (?campos=) nunca
	// tinha efeito: a resposta HTTP real era serializada por esse ObjectMapper "paralelo".
	// Aqui forçamos os conversores Jackson a usarem o ObjectMapper correto (bean único do contexto).
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				((MappingJackson2HttpMessageConverter) converter).setObjectMapper(objectMapper);
			}
		}
	}
	
	@Bean
	public Filter shallowEtagHeaderFilter() { // INTERCEPTA AS REQUISIÇÕES HTTP E ADICIONA CABEÇALHOS ETAG (ENTITY TAG) ÀS RESPOSTAS. O ETAG É UM IDENTIFICADOR ÚNICO PARA UMA VERSÃO ESPECÍFICA DE UM RECURSO. QUANDO O CLIENTE FAZ UMA SOLICITAÇÃO SUBSEQUENTE PARA O MESMO RECURSO, ELE PODE ENVIAR O ETAG NO CABEÇALHO "IF-NONE-MATCH". SE O ETAG CORRESPONDER AO RECURSO NO SERVIDOR, O SERVIDOR PODE RESPONDER COM UM STATUS 304 (NOT MODIFIED), INDICANDO QUE O RECURSO NÃO FOI ALTERADO DESDE A ÚLTIMA SOLICITAÇÃO. ISSO AJUDA A REDUZIR A QUANTIDADE DE DADOS TRANSFERIDOS ENTRE O CLIENTE E O SERVIDOR, MELHORANDO A EFICIÊNCIA DA COMUNICAÇÃO.
		return new ShallowEtagHeaderFilter(); // "Etag" -> valida se o recurso foi modificado desde a última requisição, e caso não tenha sido, retorna um status 304 (Not Modified) para o cliente, evitando o envio desnecessário de dados.
											  // "Shallow" -> o filtro não verifica se o recurso foi modificado, apenas se o conteúdo da resposta HTTP mudou. Se o conteúdo não mudou, ele retorna um status 304 (Not Modified) para o cliente, evitando o envio desnecessário de dados. QUando retorna 304, o cliente pode usar a versão em cache do recurso, economizando largura de banda e melhorando o desempenho da aplicação e tambem o navegador pega o 304 e alterada pra 200.
											 // O filtro ShallowEtagHeaderFilter   é útil para melhorar o desempenho da aplicação, reduzindo a quantidade de dados enviados entre o servidor e o cliente, especialmente em recursos que não mudam com frequência.
											 // if-none-match -> quando a resposta esta Stale ele passa o if-none-match cabeçalho HTTP que é usado para validar se o recurso solicitado pelo cliente foi modificado desde a última vez que ele foi acessado. Se o recurso não foi modificado, o servidor retorna um status 304 (Not Modified) para o cliente, evitando o envio desnecessário de dados.
	}
	
	
}