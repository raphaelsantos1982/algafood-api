package com.algaworks.algafood.core.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

  @Bean
  public Docket apiDocket() { // Docket(Sumario) -> Configuração do Swagger para gerar documentação da API - Comfigura o conjunto de serviços que devem ser documentados
    return new Docket(DocumentationType.OAS_30) // OAS_30 -> OpenAPI Specification 3.0
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.algaworks.algafood.api")) // Configura o pacote base onde estão os controllers da API
          //.apis(RequestHandlerSelectors.any()) // Seleciona todos os controladores da aplicação para serem documentados
        .paths(PathSelectors.any())
//      .paths(PathSelectors.ant("/restaurantes/*"))  // Configura o path /restaurantes/* a partir do path base declarado em .apis(RequestHandlerSelectors.basePackage("com.algaworks.algafood.api")). Funciona como filtro, nesse caso, a cima permite todos e nessa linha di z que do oath base somente configurar de restaurantes
        .build()
    	.apiInfo(apiInfo()); // chama metodo implementado abaixo que descrevendo informações da API na documentação - titulo, descrição, versão, contato
  }
  
  public ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("AlgaFood API")
				.description("API aberta para clientes e restaurantes")
				.version("1")
				.contact(new Contact("AlgaWorks", "https://www.algaworks.com", "contato@algaworks.com"))
				.build();
	}
  
}