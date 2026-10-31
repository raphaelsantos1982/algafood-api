package com.algaworks.algafood.core.springfox;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.ServletWebRequest;

import com.algaworks.algafood.api.exceptionhandler.Problem;
import com.algaworks.algafood.api.v1.controller.EstatisticasController;
import com.algaworks.algafood.api.v1.model.CidadeModel;
import com.algaworks.algafood.api.v1.model.CozinhaModel;
import com.algaworks.algafood.api.v1.model.EstadoModel;
import com.algaworks.algafood.api.v1.model.FormaPagamentoModel;
import com.algaworks.algafood.api.v1.model.GrupoModel;
import com.algaworks.algafood.api.v1.model.PedidoResumoModel;
import com.algaworks.algafood.api.v1.model.PermissaoModel;
import com.algaworks.algafood.api.v1.model.ProdutoModel;
import com.algaworks.algafood.api.v1.model.RestauranteBasicoModel;
import com.algaworks.algafood.api.v1.model.UsuarioModel;
import com.algaworks.algafood.api.v1.openapi.model.CidadesModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.CozinhasModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.EstadosModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.FormasPagamentoModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.GruposModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.LinksModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.PageableModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.PedidosResumoModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.PermissoesModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.ProdutosModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.RestaurantesBasicoModelOpenApi;
import com.algaworks.algafood.api.v1.openapi.model.UsuariosModelOpenApi;
import com.algaworks.algafood.api.v2.model.CidadeModelV2;
import com.algaworks.algafood.api.v2.model.CozinhaModelV2;
import com.algaworks.algafood.api.v2.openapi.model.CidadesModelV2OpenApi;
import com.algaworks.algafood.api.v2.openapi.model.CozinhasModelV2OpenApi;
import com.ctc.wstx.shaded.msv_core.util.Uri;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RepresentationBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.schema.AlternateTypeRules;
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
	

	private final EstatisticasController estatisticasController;
		SpringFoxConfig(EstatisticasController estatisticasController) {
			this.estatisticasController = estatisticasController;
	}

	@Bean
	public Docket apiDocketV1() { // Docket(Sumario) -> Configuração do Swagger para gerar documentação da API - Comfigura o conjunto de serviços que devem ser documentados
		var typeResolver = new TypeResolver();
		
		return new Docket(DocumentationType.OAS_30) // OAS_30 -> OpenAPI Specification 3.0
		.groupName("V1")
        .select() // SELECIONA OS ENDPOINTS QUE DEVEM SER DOCUMENTADOS. 
        .apis(RequestHandlerSelectors.basePackage("com.algaworks.algafood.api")) // Configura o pacote base onde estão os controllers da API
          //.apis(RequestHandlerSelectors.any()) // Seleciona todos os controladores da aplicação para serem documentados
        .paths(PathSelectors.ant("/v1/**"))
//      .paths(PathSelectors.ant("/restaurantes/*"))  // Configura o path /restaurantes/* a partir do path base declarado em .apis(RequestHandlerSelectors.basePackage("com.algaworks.algafood.api")). Funciona como filtro, nesse caso, a cima permite todos e nessa linha di z que do oath base somente configurar de restaurantes
        .build()
        .useDefaultResponseMessages(false)
        .globalResponses(HttpMethod.GET, globalGetResponseMessages()) // Configura as respostas globais para todos os endpoints GET da API. As respostas globais são mensagens de resposta que são aplicadas a todos os endpoints de um determinado método HTTP (GET, POST, PUT, DELETE, etc.). Nesse caso, estamos configurando respostas globais para todos os endpoints GET da API.
        .globalResponses(HttpMethod.POST, globalPostPutResponseMessages())
        .globalResponses(HttpMethod.PUT, globalPostPutResponseMessages())
        .globalResponses(HttpMethod.DELETE, globalDeleteResponseMessages())
//        .globalRequestParameters(Collections.singletonList(
//				new RequestParameterBuilder()
//						.name("campos")
//						.description("Nomes das propriedades para filtrar na resposta, separados por vírgula")
//						.in(ParameterType.QUERY)
//						.required(true)
//						.query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
//						.build())) // Configura um parâmetro de requisição global para todos os endpoints da API. Nesse caso, estamos configurando um parâmetro de query chamado "campos" que permite filtrar as propriedades retornadas na resposta da API.
        .additionalModels(typeResolver.resolve(Problem.class)) // Adiciona o modelo Problem à documentação da API. O modelo Problem é usado para representar erros na API, seguindo o padrão RFC 7807.
        .ignoredParameterTypes(ServletWebRequest.class, URL.class, Uri.class, URLStreamHandler.class, Resource.class, File.class, InputStream.class) // Ignora o parâmetro ServletWebRequest na documentação da API. O parâmetro ServletWebRequest é usado internamente pelo Spring para lidar com requisições HTTP, mas não é relevante para a documentação da API.
        .directModelSubstitute(Pageable.class, PageableModelOpenApi.class) // Substitui o modelo Pageable pelo modelo PageableModelOpenApi na documentação da API. O modelo Pageable é usado para representar paginação em consultas, mas não é bem representado na documentação do Swagger. Por isso, criamos um modelo específico para a documentação.
        .directModelSubstitute(Links.class, LinksModelOpenApi.class)
        .alternateTypeRules(AlternateTypeRules.newRule(
//				typeResolver.resolve(Page.class, CozinhaModel.class), // Substitui o modelo Page<CozinhaModel> pelo modelo CozinhasModelOpenApi na documentação da API. O modelo Page é usado para representar uma página de resultados em consultas paginadas, mas não é bem representado na documentação do Swagger. Por isso, criamos um modelo específico para a documentação.
        		typeResolver.resolve(PagedModel.class, CozinhaModel.class), // Substitui o modelo PagedModel<CozinhaModel> pelo modelo CozinhasModelOpenApi na documentação da API. O modelo PagedModel é usado para representar uma página de resultados em consultas paginadas, mas não é bem representado na documentação do Swagger. Por isso, criamos um modelo específico para a documentação.
        		CozinhasModelOpenApi.class)) 
        .alternateTypeRules(AlternateTypeRules.newRule(
				typeResolver.resolve(PagedModel.class, PedidoResumoModel.class),
				PedidosResumoModelOpenApi.class))
        .alternateTypeRules(AlternateTypeRules.newRule(
				typeResolver.resolve(CollectionModel.class, CidadeModel.class),
				CidadesModelOpenApi.class))
        .alternateTypeRules(AlternateTypeRules.newRule(
                typeResolver.resolve(CollectionModel.class, EstadoModel.class),
                EstadosModelOpenApi.class))
        .alternateTypeRules(AlternateTypeRules.newRule(
        	    typeResolver.resolve(CollectionModel.class, FormaPagamentoModel.class),
        	    FormasPagamentoModelOpenApi.class))
        .alternateTypeRules(AlternateTypeRules.newRule(
        	    typeResolver.resolve(CollectionModel.class, GrupoModel.class),
        	    GruposModelOpenApi.class))
    	.alternateTypeRules(AlternateTypeRules.newRule(
    	        typeResolver.resolve(CollectionModel.class, PermissaoModel.class),
    	        PermissoesModelOpenApi.class))
    	.alternateTypeRules(AlternateTypeRules.newRule(
    		    typeResolver.resolve(CollectionModel.class, ProdutoModel.class),
    		    ProdutosModelOpenApi.class))
    	.alternateTypeRules(AlternateTypeRules.newRule(
    		    typeResolver.resolve(CollectionModel.class, RestauranteBasicoModel.class),
    		    RestaurantesBasicoModelOpenApi.class))
		.alternateTypeRules(AlternateTypeRules.newRule(
		        typeResolver.resolve(CollectionModel.class, UsuarioModel.class),
		        UsuariosModelOpenApi.class))
        .apiInfo(apiInfoV1()) // chama metodo implementado abaixo que descrevendo informações da API na documentação - titulo, descrição, versão, contato
        .tags(new Tag("Cidades", "Gerencia as cidades"),
        	  new Tag("Grupos", "Gerencia os grupos de usuários"),
        	  new Tag("Cozinhas", "Gerencia as cozinhas"),
        	  new Tag("Formas de pagamento", "Gerencia as formas de pagamento"),
        	  new Tag("Pedidos", "Gerencia os pedidos"),
        	  new Tag("Restaurantes", "Gerencia os restaurantes"),
        	  new Tag("Estados", "Gerencia os estados"),
			  new Tag("Produtos", "Gerencia os produtos de restaurantes"),
			  new Tag("Usuários", "Gerencia os usuários"),
			  new Tag("Estatísticas", "Estatísticas da AlgaFood"),
			  new Tag("Permissões", "Gerencia as permissões"));
  	}
  
  
	@Bean
	public Docket apiDocketV2() {
		var typeResolver = new TypeResolver();

		return new Docket(DocumentationType.OAS_30)
				.groupName("V2")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.algaworks.algafood.api"))
				.paths(PathSelectors.ant("/v2/**"))
				.build()
				.useDefaultResponseMessages(false)
				.globalResponses(HttpMethod.GET, globalGetResponseMessages())
				.globalResponses(HttpMethod.POST, globalPostPutResponseMessages())
				.globalResponses(HttpMethod.PUT, globalPostPutResponseMessages())
				.globalResponses(HttpMethod.DELETE, globalDeleteResponseMessages())
				.additionalModels(typeResolver.resolve(Problem.class))
				.ignoredParameterTypes(ServletWebRequest.class,
						URL.class, URI.class, URLStreamHandler.class, Resource.class,
						File.class, InputStream.class)
				.directModelSubstitute(Pageable.class, PageableModelOpenApi.class)
				.directModelSubstitute(Links.class, LinksModelOpenApi.class)

				.alternateTypeRules(AlternateTypeRules.newRule(
						typeResolver.resolve(PagedModel.class, CozinhaModelV2.class),
						CozinhasModelV2OpenApi.class))

				.alternateTypeRules(AlternateTypeRules.newRule(
						typeResolver.resolve(CollectionModel.class, CidadeModelV2.class),
						CidadesModelV2OpenApi.class))

				.apiInfo(apiInfoV2())

				.tags(new Tag("Cidades", "Gerencia as cidades"),
						new Tag("Cozinhas", "Gerencia as cozinhas"));
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
  
  public ApiInfo apiInfoV1() { 
		return new ApiInfoBuilder()
				.title("AlgaFood API")
				.description("API aberta para clientes e restaurantes")
				.version("1")
				.contact(new Contact("AlgaWorks", "https://www.algaworks.com", "contato@algaworks.com"))
				.build();
	}
  
  private ApiInfo apiInfoV2() {
		return new ApiInfoBuilder()
				.title("AlgaFood API")
				.description("API aberta para clientes e restaurantes")
				.version("2")
				.contact(new Contact("AlgaWorks", "https://www.algaworks.com", "contato@algaworks.com"))
				.build();
	}
  
}