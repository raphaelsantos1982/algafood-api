package com.algaworks.algafood.core.openapi;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.algaworks.algafood.api.exceptionhandler.Problem;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RepresentationBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Response;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@Import(BeanValidatorPluginsConfiguration.class) // Importa a configuração para que o Swagger reconheça as anotações de validação do Bean Validation e as inclua na documentação da API
public class SpringFoxConfig {

  @Bean
  public Docket apiDocket() { // Docket(Sumario) -> Configuração do Swagger para gerar documentação da API - Comfigura o conjunto de serviços que devem ser documentados
	  var typeResolver = new TypeResolver();
		
	  return new Docket(DocumentationType.OAS_30) // OAS_30 -> OpenAPI Specification 3.0
        .select() // Seleciona os endpoints que devem ser documentados. 
        .apis(RequestHandlerSelectors.basePackage("com.algaworks.algafood.api")) // Configura o pacote base onde estão os controllers da API
          //.apis(RequestHandlerSelectors.any()) // Seleciona todos os controladores da aplicação para serem documentados
        .paths(PathSelectors.any())
//      .paths(PathSelectors.ant("/restaurantes/*"))  // Configura o path /restaurantes/* a partir do path base declarado em .apis(RequestHandlerSelectors.basePackage("com.algaworks.algafood.api")). Funciona como filtro, nesse caso, a cima permite todos e nessa linha di z que do oath base somente configurar de restaurantes
        .build()
        .useDefaultResponseMessages(false)
        .globalResponses(HttpMethod.GET, globalGetResponseMessages()) // Configura as respostas globais para todos os endpoints GET da API. As respostas globais são mensagens de resposta que são aplicadas a todos os endpoints de um determinado método HTTP (GET, POST, PUT, DELETE, etc.). Nesse caso, estamos configurando respostas globais para todos os endpoints GET da API.
        .globalResponses(HttpMethod.POST, globalPostPutResponseMessages())
        .globalResponses(HttpMethod.PUT, globalPostPutResponseMessages())
        .globalResponses(HttpMethod.DELETE, globalDeleteResponseMessages())
        .additionalModels(typeResolver.resolve(Problem.class)) // Adiciona o modelo Problem à documentação da API. O modelo Problem é usado para representar erros na API, seguindo o padrão RFC 7807.
        .apiInfo(apiInfo()) // chama metodo implementado abaixo que descrevendo informações da API na documentação - titulo, descrição, versão, contato
    	.tags(new Tag("Cidades", "Gerencia as cidades"));
  }
  
  private List<Response> globalPostPutResponseMessages() {
	    return Arrays.asList(
	        new ResponseBuilder()
	            .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
	            .description("Requisição inválida (erro do cliente)")
	            .representation( MediaType.APPLICATION_JSON )
                .apply(getProblemaModelReference())
	            .build(),
	        new ResponseBuilder()
	            .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
	            .description("Erro interno no servidor")
	            .representation( MediaType.APPLICATION_JSON )
                .apply(getProblemaModelReference())
	            .build(),
	        new ResponseBuilder()
	            .code(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()))
	            .description("Recurso não possui representação que poderia ser aceita pelo consumidor")
	            .build(),
	        new ResponseBuilder()
	            .code(String.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
	            .description("Requisição recusada porque o corpo está em um formato não suportado")
	            .representation( MediaType.APPLICATION_JSON )
                .apply(getProblemaModelReference())
	            .build()
	    );
	  }

	  private List<Response> globalDeleteResponseMessages() {
	    return Arrays.asList(
	        new ResponseBuilder()
	            .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
	            .description("Requisição inválida (erro do cliente)")
	            .representation( MediaType.APPLICATION_JSON )
                .apply(getProblemaModelReference())
	            .build(),
	        new ResponseBuilder()
	            .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
	            .description("Erro interno no servidor")
	            .representation( MediaType.APPLICATION_JSON )
                .apply(getProblemaModelReference())
	            .build()
	    );
	  }
  
  private List<Response> globalGetResponseMessages() {
	  
	  return Arrays.asList(
	  new ResponseBuilder()
	  .code("500")
	  .description("Erro interno do servidor")
	  .representation(MediaType.APPLICATION_JSON )
      .apply(getProblemaModelReference())
	  .build(),
	  new ResponseBuilder()
	  .code("406") // Not Acceptable
	  .description("Recurso não possui representação que poderia ser aceita pelo consumidor")
	  .build()
	  );
}
  
  @Bean
  public JacksonModuleRegistrar springFoxJacksonConfig() {
	  return objectMapper -> objectMapper.registerModule(new JavaTimeModule()); // Configura o Jackson para serializar e desserializar corretamente os tipos de data e hora do Java 8 (como LocalDate, LocalDateTime, etc.) na documentação da API gerada pelo Swagger.
  }
  
  private Consumer<RepresentationBuilder> getProblemaModelReference() {
	    return r -> r.model(m -> m.name("Problema")
	            .referenceModel(ref -> ref.key(k -> k.qualifiedModelName(
	                    q -> q.name("Problema").namespace("com.algaworks.algafood.api.exceptionhandler")))));
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